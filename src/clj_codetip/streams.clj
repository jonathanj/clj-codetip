(ns clj-codetip.streams
  (:import [clj-codetip.streams.limited-input-stream LimitedInputStream]))


(defn limited-input-stream
  ""
  [^java.io.InputStream stream ^long max-length]
  (LimitedInputStream. stream max-length))
