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

package org.xowl.client;

import org.xowl.store.storage.remote.XOWLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Main program for the xOWL client
 * Implementation of the CLI client for the xOWL protocol
 *
 * @author Laurent Wouters
 */
public class Program {
    /**
     * Main entry point for the program
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        Program program = new Program();
        program.run(args);
    }

    /**
     * The remote host to connect to
     */
    private String host = "localhost";
    /**
     * The port on the remote host
     */
    private int port = 3400;
    /**
     * The login
     */
    private String login;
    /**
     * The password
     */
    private String password;
    /**
     * Flag whether to exit
     */
    private boolean exit;

    /**
     * Runs this program
     *
     * @param args The arguments
     */
    public void run(String[] args) {
        for (int i = 0; i != args.length; i++) {
            if (args[i].equals("--help") || args[i].equals("-h")) {
                printHelp();
                return;
            }
            if (args[i].equals("--host") && i < args.length - 1) {
                host = args[i + 1];
                i++;
            }
            if (args[i].equals("--port") && i < args.length - 1) {
                port = Integer.parseInt(args[i + 1]);
                i++;
            }
            if (args[i].equals("--login") && i < args.length - 1) {
                login = args[i + 1];
                i++;
            }
            if (args[i].equals("--password") && i < args.length - 1) {
                password = args[i + 1];
                i++;
            }
        }
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
        while (login == null) {
            try {
                System.out.println("login?");
                login = input.readLine();
            } catch (IOException exception) {
                // do nothing
            }
        }
        while (password == null) {
            try {
                System.out.println("password?");
                password = input.readLine();
            } catch (IOException exception) {
                // do nothing
            }
        }

        try (final XOWLConnection connection = new XOWLConnection(host, port, login, password)) {
            connection.connect();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String line = connection.read();
                            if (line == null) {
                                exit = true;
                                return;
                            }
                            System.out.println(line);
                        } catch (IOException exception) {
                            return;
                        }
                    }
                }
            }, Program.class.getCanonicalName() + ".ConnectionListener").start();
            while (!exit) {
                String data = input.readLine();
                if ("LOGOUT".equals(data)) {
                    exit = true;
                }
                connection.send(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the help for this program
     */
    private static void printHelp() {
        System.out.println("xOWL CLI interface");
        System.out.println("xowl --host <host> --port <port> --login <login> --password <password>");
    }
}
