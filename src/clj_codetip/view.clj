(ns clj-codetip.view
  (:require [hiccup.element :refer [javascript-tag link-to]]
            [hiccup.form :as form]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [url]]
            [clj-time.format :as f]))


(def ^:private syntax-modes
  [{:name "APL" :mimes ["text/apl"] :mode "apl" :ext ["dyalog" "apl"]}
   {:name "Asterisk" :mimes ["text/x-asterisk"] :mode "asterisk"}
   {:name "C" :mimes ["text/x-csrc"] :mode "clike" :ext ["c" "h"]}
   {:name "C++" :mimes ["text/x-c++src"] :mode "clike" :ext ["cpp" "c++" "hpp" "h++"] :alias ["cpp"]}
   {:name "Cobol" :mimes ["text/x-cobol"] :mode "cobol" :ext ["cob" "cpy"]}
   {:name "C#" :mimes ["text/x-csharp"] :mode "clike" :ext ["cs"] :alias ["csharp"]}
   {:name "Clojure" :mimes ["text/x-clojure"] :mode "clojure" :ext ["clj"]}
   {:name "CoffeeScript" :mimes ["text/x-coffeescript"] :mode "coffeescript" :ext ["coffee"] :alias ["coffee" "coffee-script"]}
   {:name "Common Lisp" :mimes ["text/x-common-lisp"] :mode "commonlisp" :ext ["cl" "lisp" "el"] :alias ["lisp"]}
   {:name "Cypher" :mimes ["application/x-cypher-query"] :mode "cypher"}
   {:name "Cython" :mimes ["text/x-cython"] :mode "python" :ext ["pyx" "pxd" "pxi"]}
   {:name "CSS" :mimes ["text/css"] :mode "css" :ext ["css"]}
   {:name "CQL" :mimes ["text/x-cassandra"] :mode "sql" :ext ["cql"]}
   {:name "D" :mimes ["text/x-d"] :mode "d" :ext ["d"]}
   {:name "Dart" :mimes ["application/dart" "text/x-dart"] :mode "dart" :ext ["dart"]}
   {:name "diff" :mimes ["text/x-diff"] :mode "diff" :ext ["diff" "patch"]}
   {:name "Django" :mimes ["text/x-django"] :mode "django"}
   {:name "Dockerfile" :mimes ["text/x-dockerfile"] :mode "dockerfile"}
   {:name "DTD" :mimes ["application/xml-dtd"] :mode "dtd" :ext ["dtd"]}
   {:name "Dylan" :mimes ["text/x-dylan"] :mode "dylan" :ext ["dylan" "dyl" "intr"]}
   {:name "EBNF" :mimes ["text/x-ebnf"] :mode "ebnf"}
   {:name "ECL" :mimes ["text/x-ecl"] :mode "ecl" :ext ["ecl"]}
   {:name "Eiffel" :mimes ["text/x-eiffel"] :mode "eiffel" :ext ["e"]}
   {:name "Embedded Javascript" :mimes ["application/x-ejs"] :mode "htmlembedded" :ext ["ejs"]}
   {:name "Embedded Ruby" :mimes ["application/x-erb"] :mode "htmlembedded" :ext ["erb"]}
   {:name "Erlang" :mimes ["text/x-erlang"] :mode "erlang" :ext ["erl"]}
   {:name "Fortran" :mimes ["text/x-fortran"] :mode "fortran" :ext ["f" "for" "f77" "f90"]}
   {:name "F#" :mimes ["text/x-fsharp"] :mode "mllike" :ext ["fs"] :alias ["fsharp"]}
   {:name "Gas" :mimes ["text/x-gas"] :mode "gas" :ext ["s"]}
   {:name "Gherkin" :mimes ["text/x-feature"] :mode "gherkin" :ext ["feature"]}
   {:name "GitHub Flavored Markdown" :mimes ["text/x-gfm"] :mode "gfm"}
   {:name "Go" :mimes ["text/x-go"] :mode "go" :ext ["go"]}
   {:name "Groovy" :mimes ["text/x-groovy"] :mode "groovy" :ext ["groovy"]}
   {:name "HAML" :mimes ["text/x-haml"] :mode "haml" :ext ["haml"]}
   {:name "Haskell" :mimes ["text/x-haskell"] :mode "haskell" :ext ["hs"]}
   {:name "Haxe" :mimes ["text/x-haxe"] :mode "haxe" :ext ["hx"]}
   {:name "HXML" :mimes ["text/x-hxml"] :mode "haxe" :ext ["hxml"]}
   {:name "ASP.NET" :mimes ["application/x-aspx"] :mode "htmlembedded" :ext ["aspx"] :alias ["asp" "aspx"]}
   {:name "HTML" :mimes ["text/html"] :mode "htmlmixed" :ext ["html" "htm"] :alias ["xhtml"]}
   {:name "HTTP" :mimes ["message/http"] :mode "http"}
   {:name "IDL" :mimes ["text/x-idl"] :mode "idl" :ext ["pro"]}
   {:name "Jade" :mimes ["text/x-jade"] :mode "jade" :ext ["jade"]}
   {:name "Java" :mimes ["text/x-java"] :mode "clike" :ext ["java"]}
   {:name "Java Server Pages" :mimes ["application/x-jsp"] :mode "htmlembedded" :ext ["jsp"] :alias ["jsp"]}
   {:name "JavaScript" :mimes ["text/javascript" "text/ecmascript" "application/javascript" "application/x-javascript" "application/ecmascript"]
    :mode "javascript" :ext ["js"] :alias ["ecmascript" "js" "node"]}
   {:name "JSON" :mimes ["application/json" "application/x-json"] :mode "javascript" :ext ["json" "map"] :alias ["json5"]}
   {:name "JSON-LD" :mimes ["application/ld+json"] :mode "javascript" :alias ["jsonld"]}
   {:name "Jinja2" :mimes ["null"] :mode "jinja2"}
   {:name "Julia" :mimes ["text/x-julia"] :mode "julia" :ext ["jl"]}
   {:name "Kotlin" :mimes ["text/x-kotlin"] :mode "kotlin" :ext ["kt"]}
   {:name "LESS" :mimes ["text/x-less"] :mode "css" :ext ["less"]}
   {:name "LiveScript" :mimes ["text/x-livescript"] :mode "livescript" :ext ["ls"] :alias ["ls"]}
   {:name "Lua" :mimes ["text/x-lua"] :mode "lua" :ext ["lua"]}
   {:name "Markdown" :mimes ["text/x-markdown"] :mode "markdown" :ext ["markdown" "md" "mkd"]}
   {:name "mIRC" :mimes ["text/mirc"] :mode "mirc"}
   {:name "MariaDB SQL" :mimes ["text/x-mariadb"] :mode "sql"}
   {:name "Modelica" :mimes ["text/x-modelica"] :mode "modelica" :ext ["mo"]}
   {:name "MS SQL" :mimes ["text/x-mssql"] :mode "sql"}
   {:name "MySQL" :mimes ["text/x-mysql"] :mode "sql"}
   {:name "Nginx" :mimes ["text/x-nginx-conf"] :mode "nginx"}
   {:name "NTriples" :mimes ["text/n-triples"] :mode "ntriples" :ext ["nt"]}
   {:name "Objective C" :mimes ["text/x-objectivec"] :mode "clike" :ext ["m" "mm"]}
   {:name "OCaml" :mimes ["text/x-ocaml"] :mode "mllike" :ext ["ml" "mli" "mll" "mly"]}
   {:name "Octave" :mimes ["text/x-octave"] :mode "octave" :ext ["m"]}
   {:name "Pascal" :mimes ["text/x-pascal"] :mode "pascal" :ext ["p" "pas"]}
   {:name "PEG.js" :mimes ["null"] :mode "pegjs"}
   {:name "Perl" :mimes ["text/x-perl"] :mode "perl" :ext ["pl" "pm"]}
   {:name "PHP" :mimes ["application/x-httpd-php"] :mode "php" :ext ["php" "php3" "php4" "php5" "phtml"]}
   {:name "Pig" :mimes ["text/x-pig"] :mode "pig"}
   {:name "Plain Text" :mimes ["text/plain"] :mode "null" :ext ["txt" "text" "conf" "def" "list" "log"]}
   {:name "PLSQL" :mimes ["text/x-plsql"] :mode "sql"}
   {:name "Properties files" :mimes ["text/x-properties"] :mode "properties" :ext ["properties" "ini" "in"] :alias ["ini" "properties"]}
   {:name "Python" :mimes ["text/x-python" "text/x-python-script"] :mode "python" :ext ["py" "pyw"]}
   {:name "Puppet" :mimes ["text/x-puppet"] :mode "puppet" :ext ["pp"]}
   {:name "Q" :mimes ["text/x-q"] :mode "q" :ext ["q"]}
   {:name "R" :mimes ["text/x-rsrc"] :mode "r" :ext ["r"] :alias ["rscript"]}
   {:name "reStructuredText" :mimes ["text/x-rst"] :mode "rst" :ext ["rst"] :alias ["rst"]}
   {:name "RPM Changes" :mimes ["text/x-rpm-changes"] :mode "rpm"}
   {:name "RPM Spec" :mimes ["text/x-rpm-spec"] :mode "rpm" :ext ["spec"]}
   {:name "Ruby" :mimes ["text/x-ruby"] :mode "ruby" :ext ["rb"] :alias ["jruby" "macruby" "rake" "rb" "rbx"]}
   {:name "Rust" :mimes ["text/x-rustsrc"] :mode "rust" :ext ["rs"]}
   {:name "Sass" :mimes ["text/x-sass"] :mode "sass" :ext ["sass"]}
   {:name "Scala" :mimes ["text/x-scala"] :mode "clike" :ext ["scala"]}
   {:name "Scheme" :mimes ["text/x-scheme"] :mode "scheme" :ext ["scm" "ss"]}
   {:name "SCSS" :mimes ["text/x-scss"] :mode "css" :ext ["scss"]}
   {:name "Shell" :mimes ["text/x-sh"] :mode "shell" :ext ["sh" "ksh" "bash"] :alias ["bash" "sh" "zsh"]}
   {:name "Sieve" :mimes ["application/sieve"] :mode "sieve"}
   {:name "Slim" :mimes ["text/x-slim" "application/x-slim"] :mode "slim"}
   {:name "Smalltalk" :mimes ["text/x-stsrc"] :mode "smalltalk" :ext ["st"]}
   {:name "Smarty" :mimes ["text/x-smarty"] :mode "smarty" :ext ["tpl"]}
   {:name "SmartyMixed" :mimes ["text/x-smarty"] :mode "smartymixed"}
   {:name "Solr" :mimes ["text/x-solr"] :mode "solr"}
   {:name "SPARQL" :mimes ["application/sparql-query"] :mode "sparql" :ext ["rq" "sparql"] :alias ["sparul"]}
   {:name "SQL" :mimes ["text/x-sql"] :mode "sql" :ext ["sql"]}
   {:name "MariaDB" :mimes ["text/x-mariadb"] :mode "sql"}
   {:name "sTeX" :mimes ["text/x-stex"] :mode "stex"}
   {:name "LaTeX" :mimes ["text/x-latex"] :mode "stex" :ext ["text" "ltx"] :alias ["tex"]}
   {:name "SystemVerilog" :mimes ["text/x-systemverilog"] :mode "verilog" :ext ["v"]}
   {:name "Tcl" :mimes ["text/x-tcl"] :mode "tcl" :ext ["tcl"]}
   {:name "Textile" :mimes ["text/x-textile"] :mode "textile"}
   {:name "TiddlyWiki " :mimes ["text/x-tiddlywiki"] :mode "tiddlywiki"}
   {:name "Tiki wiki" :mimes ["text/tiki"] :mode "tiki"}
   {:name "TOML" :mimes ["text/x-toml"] :mode "toml"}
   {:name "Tornado" :mimes ["text/x-tornado"] :mode "tornado"}
   {:name "Turtle" :mimes ["text/turtle"] :mode "turtle" :ext ["ttl"]}
   {:name "TypeScript" :mimes ["application/typescript"] :mode "javascript" :ext ["ts"] :alias ["ts"]}
   {:name "VB.NET" :mimes ["text/x-vb"] :mode "vb" :ext ["vb"]}
   {:name "VBScript" :mimes ["text/vbscript"] :mode "vbscript"}
   {:name "Velocity" :mimes ["text/velocity"] :mode "velocity" :ext ["vtl"]}
   {:name "Verilog" :mimes ["text/x-verilog"] :mode "verilog" :ext ["v"]}
   {:name "XML" :mimes ["application/xml" "text/xml"] :mode "xml" :ext ["xml" "xsl" "xsd"] :alias ["rss" "wsdl" "xsd"]}
   {:name "XQuery" :mimes ["application/xquery"] :mode "xquery" :ext ["xy" "xquery"]}
   {:name "YAML" :mimes ["text/x-yaml"] :mode "yaml" :ext ["yaml"] :alias ["yml"]}
   {:name "Z80" :mimes ["text/x-z80"] :mode "z80" :ext ["z80"]}])


(defn- explode-mimes [t]
  (for [mime (:mimes t [])]
    (assoc (dissoc t :mimes) :mime mime)))


(def ^:private syntax-modes-select (map (juxt :name (comp first :mimes)) syntax-modes))
(def ^:private exploded-syntax-modes (mapcat explode-mimes syntax-modes))
(def syntax-mime-modes (into {} (map (juxt :mime :name) exploded-syntax-modes)))


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
                      (cdnjs-uri "crel/2.1.8/crel.min.js")
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
   (javascript-tag (format "initialisePasteView(\"%s\");" content-type))))


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
                  (form/drop-down {:id "content-type"}
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
                                   :placeholder "Paste content or drag files here"}
                                  "content" "")])
   (javascript-tag "initialiseNewPaste();")])
