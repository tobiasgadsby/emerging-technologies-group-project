(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]))

(def lib 'me.jamescrake-merani/resource-mapping)
(def version "0.1.0-SNAPSHOT")
(def main 'jamescrake-merani.resource-mapping)
(def class-dir "target/classes")

(defn clean "Delete the build target directory." [_]
  (b/delete {:path "target"}))

(defn test "Run all the tests." [opts]
  (let [basis    (b/create-basis {:aliases [:test]})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn- uber-opts [opts]
  (assoc opts
         :lib lib :main main
         :uber-file (format "target/%s-%s-standalone.jar" (name lib) version)
         :basis (b/create-basis {})
         :class-dir class-dir
         :src-dirs ["src"]
         :ns-compile [main]))

(defn jar "Build the library JAR file." [opts]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis (b/create-basis {})
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (let [jar-file (format "target/%s-%s.jar" (name lib) version)]
    (b/jar {:class-dir class-dir
            :jar-file jar-file})
    (println "Built" jar-file)
    (assoc opts :jar-file jar-file)))

(defn install "Install the library JAR to the local Maven repository." [opts]
  (let [basis (b/create-basis {})
        {:keys [jar-file]} (jar opts)]
    (b/install {:basis basis
                :lib lib
                :version version
                :jar-file jar-file
                :class-dir class-dir})
    (println "Installed" jar-file "to local repo")
    opts))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]
  (test opts)
  (clean opts)
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding Uberjar..." (:uber-file opts))
    (b/uber opts))
  opts)
