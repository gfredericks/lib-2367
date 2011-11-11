(ns lib-2367.hash
  (:import java.security.MessageDigest
           java.security.DigestInputStream
           java.io.ByteArrayInputStream))

(defn- byte-array-to-hex
  [bytes]
  (apply str (for [b bytes] (format "%02x" b))))

(defn sha-1
  [stringish]
  (let [md (MessageDigest/getInstance "SHA1"),
        dis (new DigestInputStream
                 (new ByteArrayInputStream (.getBytes (name stringish)))
                 md)]
    (loop [x (.read dis)]
      (when (> x -1) (recur (.read dis))))
    (byte-array-to-hex (.digest md))))
