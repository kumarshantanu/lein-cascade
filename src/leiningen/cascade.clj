(ns leiningen.cascade
  (:require [clojure.pprint :as pp]
            [clojure.string :as str]
            [leiningen.core.main :as main]))


(defn abort
  [& msgs]
  (apply main/abort "[lein-cascade] [ERROR]" msgs))


(defn run-tasks
  [tasks project]
  (doseq [[task-name & args] tasks]
    (let [resolved-task-name (main/lookup-alias task-name project)]
      (main/apply-task resolved-task-name project args))))


(defn conj-unique
  [visited ckey]
  (if (contains? visited ckey)
    (abort "Cyclic dependency found - key already encountered:" ckey)
    (conj visited ckey)))


(defn resolve-val
  "Resolve symbol %* as command-line arg, symbol as env-var and rest unchanged"
  [args token]
  (if-not (symbol? token)
    token
    (let [token-str (name token)
          [c1 & more] token-str]
      (if-not (= c1 \%)
        (or (System/getenv token-str)
            (abort "No such environment variable:" token-str))
        (let [anum (Integer/parseInt (str/join (or (seq more) ["1"])))]
          (if-not (or (< anum 1) (> anum (count args)))
            (nth args (dec anum))
            (abort "Cannot resolve argument" token "- args="
                   (pr-str args))))))))


(declare resolve-tasks)


(defn resolve-task-args
  [cmap visited args each-val]
  (cond
    (vector? each-val) (if (seq each-val)
                         (-> (partial resolve-val args)
                             (map each-val)
                             vector)
                         (abort "Inner task-vector must not be empty:"
                                (pr-str each-val)))
    (coll? each-val)   (abort "Inner val should be a key or task-vector:"
                              (pr-str each-val))
    :else (resolve-tasks cmap visited each-val args)))


(defn resolve-tasks
  [cmap visited ckey args]
  (println "[lein-cascade] Executing" (pr-str ckey))
  (if (contains? cmap ckey)
    (let [cvals (get cmap ckey)]
      (if (vector? cvals)
        (-> resolve-task-args
            (partial cmap (conj-unique visited ckey) args)
            (mapcat cvals))
        (abort "Expected cascade vals to be a vector but found"
               (pr-str cvals))))
    (abort "No such cascade key:" (pr-str ckey)
           (str "in\n" (with-out-str (pp/pprint cmap))))))


(defn cascade
  "Run cascading tasks from :cascade map in project.clj"
  [project cascade-key & args]
  (when-not (contains? project :cascade)
    (abort
             "No cascades defined. Define with :cascade key in project.clj, e.g.
             :cascade {\"foo\" [[\"clean\"]]
                       \"bar\" [\"foo\"
                              [\"javac\"]]
                       \"baz\" [\"bar\"
                              [\"test\"]]
                       \"quux\" [\"bar\"
                               [\"uberjar\"]]}
Note: Cascade keys are string tokens with acyclic dependencies"))
  (-> (:cascade project)
      (resolve-tasks #{} cascade-key args)
      (run-tasks project)))
