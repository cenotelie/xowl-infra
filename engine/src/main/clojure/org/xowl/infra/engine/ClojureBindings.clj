;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
; This program is free software: you can redistribute it and/or modify
; it under the terms of the GNU Lesser General Public License as
; published by the Free Software Foundation, either version 3
; of the License, or (at your option) any later version.
;
; This program is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU Lesser General Public License for more details.
;
; You should have received a copy of the GNU Lesser General
; Public License along with this program.
; If not, see <http://www.gnu.org/licenses/>.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns org.xowl.infra.engine.ClojureBindings
  (:import (org.xowl.infra.engine ClojureAPI)
           (org.xowl.infra.store RDFUtils)
           (org.xowl.infra.store.rdf RDFPatternSolution Node)
           (org.xowl.infra.store.sparql Result ResultSolutions)
           (org.xowl.infra.lang.owl2 IRI)
           (org.xowl.infra.utils.logging Logging)))

(defn rdfIriNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_IRI))
  )

(defn rdfNlankNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_BLANK))
  )

(defn rdfAnonymousNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_ANONYMOUS))
  )

(defn rdfLiteralNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_LITERAL))
  )

(defn rdfVariableNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_VARIABLE))
  )

(defn rdfDynamicNode? [^Node node]
  (if (nil? node)
    false
    (= (.getNodeType node) Node/TYPE_DYNAMIC))
  )

(defn iriToStr [^IRI entity]
  (.getHasValue entity))

(defn native [^Node node]
  (RDFUtils/getNative node))

(defn sparql [^String query]
  (ClojureAPI/sparql query))

(defn valueOf [^RDFPatternSolution solution ^String variable]
  (native (.get solution variable)))

(defn firstValueOf [^ResultSolutions result ^String variable]
  (let [solutions (.getSolutions result)]
    (if (= 0 (.size solutions))
      nil
      (valueOf (.next (.iterator solutions)) variable)
      )))

(defn getObjectValue [^IRI entity ^String property]
  (ClojureAPI/getObjectValue entity property))

(defn getObjectValues [^IRI entity ^String property]
  (ClojureAPI/getObjectValues entity property))

(defn getDataValue [^IRI entity ^String property]
  (ClojureAPI/getDataValue entity property))

(defn getDataValues [^IRI entity ^String property]
  (ClojureAPI/getDataValues entity property))

(defn logInfo [message]
  (.info (Logging/get) message))

(defn logWarning [message]
  (.warning (Logging/get) message))

(defn logError [message]
  (.error (Logging/get) message))