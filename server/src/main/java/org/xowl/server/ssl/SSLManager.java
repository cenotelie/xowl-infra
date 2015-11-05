/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.server.ssl;

import org.xowl.utils.collections.Couple;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Scanner;

/**
 * New code
 *
 * @author Laurent Wouters
 */
public class SSLManager {
    /**
     * The alias for generated certificates
     */
    public static final String GENERATED_ALIAS = "org.xowl.server";

    /**
     * Hexadecimal characters
     */
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Generates a key store with a new self-signed certificate
     *
     * @return The couple of the key store and the password to the private key
     */
    public static Couple<KeyStore, String> generateKeyStore() {
        SecureRandom random = new SecureRandom();
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        char[] chars = new char[buffer.length * 2];
        int j = 0;
        for (int i = 0; i != buffer.length; i++) {
            chars[j++] = HEX[(buffer[i] & 0xF0) >>> 4];
            chars[j++] = HEX[buffer[i] & 0x0F];
        }
        String password = new String(chars);

        try {
            File temp = File.createTempFile("store", ".jks");
            if (!temp.delete()) {
                // failed to delete
                return null;
            }
            String[] command = new String[]{"keytool", "-genkeypair",
                    "-alias", GENERATED_ALIAS,
                    "-keyalg", "RSA", "-keysize", "2048",
                    "-dname", "CN=" + GENERATED_ALIAS,
                    "-validity", "3650", "-storetype", "JKS",
                    "-keystore", temp.getAbsolutePath()};
            final Process process = Runtime.getRuntime().exec(command);
            new Thread(new Runnable() {
                public void run() {
                    try (InputStream inStream = process.getInputStream()) {
                        InputStreamReader reader = new InputStreamReader(inStream);
                        Scanner scan = new Scanner(reader);
                        while (scan.hasNextLine()) {
                            System.out.println(scan.nextLine());
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }).start();
            try (OutputStream outStream = process.getOutputStream()) {
                PrintWriter writer = new PrintWriter(outStream);
                writer.println(password);
                writer.flush();
                writer.println(password);
                writer.flush();
                writer.println();
                writer.flush();
                writer.close();
            }
            process.waitFor();

            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream stream = new FileInputStream(temp)) {
                keyStore.load(stream, password.toCharArray());
            }

            if (!temp.delete()) {
                // failed to delete
                return null;
            }
            return new Couple<>(keyStore, password);
        } catch (IOException | InterruptedException | KeyStoreException | NoSuchAlgorithmException | CertificateException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
