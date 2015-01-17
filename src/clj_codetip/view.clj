(ns clj-codetip.view
  (:require [hiccup.element :refer [javascript-tag link-to]]
            [hiccup.form :as form]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [url]]
            [clj-time.format :as f]))


(def ^:private syntax-modes
  [{:name "APL" :mime "text/apl" :mode "apl" :ext ["dyalog" "apl"]}
   {:name "Asterisk" :mime "text/x-asterisk" :mode "asterisk"}
   {:name "C" :mime "text/x-csrc" :mode "clike" :ext ["c" "h"]}
   {:name "C++" :mime "text/x-c++src" :mode "clike" :ext ["cpp" "c++" "hpp" "h++"] :alias ["cpp"]}
   {:name "Cobol" :mime "text/x-cobol" :mode "cobol" :ext ["cob" "cpy"]}
   {:name "C#" :mime "text/x-csharp" :mode "clike" :ext ["cs"] :alias ["csharp"]}
   {:name "Clojure" :mime "text/x-clojure" :mode "clojure" :ext ["clj"]}
   {:name "CoffeeScript" :mime "text/x-coffeescript" :mode "coffeescript" :ext ["coffee"] :alias ["coffee" "coffee-script"]}
   {:name "Common Lisp" :mime "text/x-common-lisp" :mode "commonlisp" :ext ["cl" "lisp" "el"] :alias ["lisp"]}
   {:name "Cypher" :mime "application/x-cypher-query" :mode "cypher"}
   {:name "Cython" :mime "text/x-cython" :mode "python" :ext ["pyx" "pxd" "pxi"]}
   {:name "CSS" :mime "text/css" :mode "css" :ext ["css"]}
   {:name "CQL" :mime "text/x-cassandra" :mode "sql" :ext ["cql"]}
   {:name "D" :mime "text/x-d" :mode "d" :ext ["d"]}
   {:name "Dart" :mimes ["application/dart" "text/x-dart"] :mode "dart" :ext ["dart"]}
   {:name "diff" :mime "text/x-diff" :mode "diff" :ext ["diff" "patch"]}
   {:name "Django" :mime "text/x-django" :mode "django"}
   {:name "Dockerfile" :mime "text/x-dockerfile" :mode "dockerfile"}
   {:name "DTD" :mime "application/xml-dtd" :mode "dtd" :ext ["dtd"]}
   {:name "Dylan" :mime "text/x-dylan" :mode "dylan" :ext ["dylan" "dyl" "intr"]}
   {:name "EBNF" :mime "text/x-ebnf" :mode "ebnf"}
   {:name "ECL" :mime "text/x-ecl" :mode "ecl" :ext ["ecl"]}
   {:name "Eiffel" :mime "text/x-eiffel" :mode "eiffel" :ext ["e"]}
   {:name "Embedded Javascript" :mime "application/x-ejs" :mode "htmlembedded" :ext ["ejs"]}
   {:name "Embedded Ruby" :mime "application/x-erb" :mode "htmlembedded" :ext ["erb"]}
   {:name "Erlang" :mime "text/x-erlang" :mode "erlang" :ext ["erl"]}
   {:name "Fortran" :mime "text/x-fortran" :mode "fortran" :ext ["f" "for" "f77" "f90"]}
   {:name "F#" :mime "text/x-fsharp" :mode "mllike" :ext ["fs"] :alias ["fsharp"]}
   {:name "Gas" :mime "text/x-gas" :mode "gas" :ext ["s"]}
   {:name "Gherkin" :mime "text/x-feature" :mode "gherkin" :ext ["feature"]}
   {:name "GitHub Flavored Markdown" :mime "text/x-gfm" :mode "gfm"}
   {:name "Go" :mime "text/x-go" :mode "go" :ext ["go"]}
   {:name "Groovy" :mime "text/x-groovy" :mode "groovy" :ext ["groovy"]}
   {:name "HAML" :mime "text/x-haml" :mode "haml" :ext ["haml"]}
   {:name "Haskell" :mime "text/x-haskell" :mode "haskell" :ext ["hs"]}
   {:name "Haxe" :mime "text/x-haxe" :mode "haxe" :ext ["hx"]}
   {:name "HXML" :mime "text/x-hxml" :mode "haxe" :ext ["hxml"]}
   {:name "ASP.NET" :mime "application/x-aspx" :mode "htmlembedded" :ext ["aspx"] :alias ["asp" "aspx"]}
   {:name "HTML" :mime "text/html" :mode "htmlmixed" :ext ["html" "htm"] :alias ["xhtml"]}
   {:name "HTTP" :mime "message/http" :mode "http"}
   {:name "IDL" :mime "text/x-idl" :mode "idl" :ext ["pro"]}
   {:name "Jade" :mime "text/x-jade" :mode "jade" :ext ["jade"]}
   {:name "Java" :mime "text/x-java" :mode "clike" :ext ["java"]}
   {:name "Java Server Pages" :mime "application/x-jsp" :mode "htmlembedded" :ext ["jsp"] :alias ["jsp"]}
   {:name "JavaScript" :mimes ["text/javascript" "text/ecmascript" "application/javascript" "application/x-javascript" "application/ecmascript"]
    :mode "javascript" :ext ["js"] :alias ["ecmascript" "js" "node"]}
   {:name "JSON" :mimes ["application/json" "application/x-json"] :mode "javascript" :ext ["json" "map"] :alias ["json5"]}
   {:name "JSON-LD" :mime "application/ld+json" :mode "javascript" :alias ["jsonld"]}
   {:name "Jinja2" :mime "null" :mode "jinja2"}
   {:name "Julia" :mime "text/x-julia" :mode "julia" :ext ["jl"]}
   {:name "Kotlin" :mime "text/x-kotlin" :mode "kotlin" :ext ["kt"]}
   {:name "LESS" :mime "text/x-less" :mode "css" :ext ["less"]}
   {:name "LiveScript" :mime "text/x-livescript" :mode "livescript" :ext ["ls"] :alias ["ls"]}
   {:name "Lua" :mime "text/x-lua" :mode "lua" :ext ["lua"]}
   {:name "Markdown" :mime "text/x-markdown" :mode "markdown" :ext ["markdown" "md" "mkd"]}
   {:name "mIRC" :mime "text/mirc" :mode "mirc"}
   {:name "MariaDB SQL" :mime "text/x-mariadb" :mode "sql"}
   {:name "Modelica" :mime "text/x-modelica" :mode "modelica" :ext ["mo"]}
   {:name "MS SQL" :mime "text/x-mssql" :mode "sql"}
   {:name "MySQL" :mime "text/x-mysql" :mode "sql"}
   {:name "Nginx" :mime "text/x-nginx-conf" :mode "nginx"}
   {:name "NTriples" :mime "text/n-triples" :mode "ntriples" :ext ["nt"]}
   {:name "Objective C" :mime "text/x-objectivec" :mode "clike" :ext ["m" "mm"]}
   {:name "OCaml" :mime "text/x-ocaml" :mode "mllike" :ext ["ml" "mli" "mll" "mly"]}
   {:name "Octave" :mime "text/x-octave" :mode "octave" :ext ["m"]}
   {:name "Pascal" :mime "text/x-pascal" :mode "pascal" :ext ["p" "pas"]}
   {:name "PEG.js" :mime "null" :mode "pegjs"}
   {:name "Perl" :mime "text/x-perl" :mode "perl" :ext ["pl" "pm"]}
   {:name "PHP" :mime "application/x-httpd-php" :mode "php" :ext ["php" "php3" "php4" "php5" "phtml"]}
   {:name "Pig" :mime "text/x-pig" :mode "pig"}
   {:name "Plain Text" :mime "text/plain" :mode "null" :ext ["txt" "text" "conf" "def" "list" "log"]}
   {:name "PLSQL" :mime "text/x-plsql" :mode "sql"}
   {:name "Properties files" :mime "text/x-properties" :mode "properties" :ext ["properties" "ini" "in"] :alias ["ini" "properties"]}
   {:name "Python" :mime "text/x-python" :mode "python" :ext ["py" "pyw"]}
   {:name "Puppet" :mime "text/x-puppet" :mode "puppet" :ext ["pp"]}
   {:name "Q" :mime "text/x-q" :mode "q" :ext ["q"]}
   {:name "R" :mime "text/x-rsrc" :mode "r" :ext ["r"] :alias ["rscript"]}
   {:name "reStructuredText" :mime "text/x-rst" :mode "rst" :ext ["rst"] :alias ["rst"]}
   {:name "RPM Changes" :mime "text/x-rpm-changes" :mode "rpm"}
   {:name "RPM Spec" :mime "text/x-rpm-spec" :mode "rpm" :ext ["spec"]}
   {:name "Ruby" :mime "text/x-ruby" :mode "ruby" :ext ["rb"] :alias ["jruby" "macruby" "rake" "rb" "rbx"]}
   {:name "Rust" :mime "text/x-rustsrc" :mode "rust" :ext ["rs"]}
   {:name "Sass" :mime "text/x-sass" :mode "sass" :ext ["sass"]}
   {:name "Scala" :mime "text/x-scala" :mode "clike" :ext ["scala"]}
   {:name "Scheme" :mime "text/x-scheme" :mode "scheme" :ext ["scm" "ss"]}
   {:name "SCSS" :mime "text/x-scss" :mode "css" :ext ["scss"]}
   {:name "Shell" :mime "text/x-sh" :mode "shell" :ext ["sh" "ksh" "bash"] :alias ["bash" "sh" "zsh"]}
   {:name "Sieve" :mime "application/sieve" :mode "sieve"}
   {:name "Slim" :mimes ["text/x-slim" "application/x-slim"] :mode "slim"}
   {:name "Smalltalk" :mime "text/x-stsrc" :mode "smalltalk" :ext ["st"]}
   {:name "Smarty" :mime "text/x-smarty" :mode "smarty" :ext ["tpl"]}
   {:name "SmartyMixed" :mime "text/x-smarty" :mode "smartymixed"}
   {:name "Solr" :mime "text/x-solr" :mode "solr"}
   {:name "SPARQL" :mime "application/sparql-query" :mode "sparql" :ext ["rq" "sparql"] :alias ["sparul"]}
   {:name "SQL" :mime "text/x-sql" :mode "sql" :ext ["sql"]}
   {:name "MariaDB" :mime "text/x-mariadb" :mode "sql"}
   {:name "sTeX" :mime "text/x-stex" :mode "stex"}
   {:name "LaTeX" :mime "text/x-latex" :mode "stex" :ext ["text" "ltx"] :alias ["tex"]}
   {:name "SystemVerilog" :mime "text/x-systemverilog" :mode "verilog" :ext ["v"]}
   {:name "Tcl" :mime "text/x-tcl" :mode "tcl" :ext ["tcl"]}
   {:name "Textile" :mime "text/x-textile" :mode "textile"}
   {:name "TiddlyWiki " :mime "text/x-tiddlywiki" :mode "tiddlywiki"}
   {:name "Tiki wiki" :mime "text/tiki" :mode "tiki"}
   {:name "TOML" :mime "text/x-toml" :mode "toml"}
   {:name "Tornado" :mime "text/x-tornado" :mode "tornado"}
   {:name "Turtle" :mime "text/turtle" :mode "turtle" :ext ["ttl"]}
   {:name "TypeScript" :mime "application/typescript" :mode "javascript" :ext ["ts"] :alias ["ts"]}
   {:name "VB.NET" :mime "text/x-vb" :mode "vb" :ext ["vb"]}
   {:name "VBScript" :mime "text/vbscript" :mode "vbscript"}
   {:name "Velocity" :mime "text/velocity" :mode "velocity" :ext ["vtl"]}
   {:name "Verilog" :mime "text/x-verilog" :mode "verilog" :ext ["v"]}
   {:name "XML" :mimes ["application/xml" "text/xml"] :mode "xml" :ext ["xml" "xsl" "xsd"] :alias ["rss" "wsdl" "xsd"]}
   {:name "XQuery" :mime "application/xquery" :mode "xquery" :ext ["xy" "xquery"]}
   {:name "YAML" :mime "text/x-yaml" :mode "yaml" :ext ["yaml"] :alias ["yml"]}
   {:name "Z80" :mime "text/x-z80" :mode "z80" :ext ["z80"]}])


(def syntax-mime-modes (into {} (map (juxt :mime :name) syntax-modes)))


(defn- cdnjs-uri [& paths]
  (apply url "//cdnjs.cloudflare.com/ajax/libs/" paths))


(def ^:private -codemirror-uri "//cdnjs.cloudflare.com/ajax/libs/codemirror/4.8.0/")


(def ^:private codemirror-uri (partial cdnjs-uri "codemirror/4.8.0/"))


(defn application [title & content]
  ""
  (html5 {:lang "en"}
         [:head
          [:title title]
          (include-css (codemirror-uri "codemirror.min.css")
                       (codemirror-uri "theme/" "solarized.min.css")
                       "/static/codetip.css")
          (include-js (codemirror-uri "codemirror.min.js")
                      (codemirror-uri "addon/mode/" "loadmode.min.js")
                      (codemirror-uri "addon/display/" "placeholder.js")
                      (codemirror-uri "mode/" "meta.min.js")
                      (cdnjs-uri "mousetrap/1.4.6/mousetrap.min.js")
                      (cdnjs-uri "mousetrap/1.4.6/mousetrap-global-bind.min.js")
                      "/static/codetip.js")
          (javascript-tag
           (format "CodeMirror.modeURL = \"%smode/%%N/%%N.min.js\";" -codemirror-uri))
          [:link {:rel "apple-touch-icon"
                  :href "/static/codetip-touch.png"}]
          [:link {:rel "icon"
                  :href "/static/codetip.ico"
                  :sizes "16x16 32x32 48x48"
                  :type "image/vnd.microsoft.icon"}]
          [:link {:rel "icon"
                  :href "/static/codetip.svg"
                  :sizes "any"
                  :type "image/svg+xml"}]]
         [:body
          [:div.navbar [:h1 (link-to "https://github.com/jonathanj/clj-codetip" "Codetip")]]
          [:div.page-content content]]))


(defn paste
  "Read-only paste view."
  [{:keys [content content-type expires]}]
  (list
   [:div.sub-navbar
    [:a.btn {:href "/"} "New paste"]
    [:label "Expires"]
    [:span (f/unparse (f/formatters :rfc822) expires)]]
   [:div#focus.paste-content
    (form/text-area {:id "content"
                     :readonly "readonly"}
                    "content" content)]
   (include-js (codemirror-uri "addon/runmode/" "runmode.min.js"))
   (javascript-tag (format "initialisePaste(true, \"%s\");" content-type))))


(def ^:private syntax-modes-select (map (juxt :name :mime) syntax-modes))

(defn new-paste
  "Paste creation view."
  []
  [:div.new-paste
   (form/form-to {:id "form"
                  :enctype "multipart/form-data"}
                 [:post "/"]
                 [:div.sub-navbar
                  (form/submit-button "Create paste")
                  (form/label "content-type" "Syntax")
                  (form/drop-down {:id "content-type"
                                   :onchange "modeInputChanged(this);"}
                                  "content-type"
                                  syntax-modes-select
                                  "text/plain")
                  (form/label "expires" "Expires in")
                  (form/drop-down {:id "expires"}
                                  "expires"
                                  [["1 hour" "hour"]
                                   ["1 day"  "day"]
                                   ["1 week" "week"]]
                                  "week")]
                 [:div#focus.paste-content
                  (form/text-area {:id "content"
                                   :placeholder "Paste content here"}
                                  "content" "")])
   (javascript-tag "initialisePaste();")])
