package utils

import Printer

object ArgsProvider {
    var args: Array<String> = arrayOf()

    var noCompilation: Boolean = false
    var useCache: Boolean = false
    var showTree: Boolean = false
    var writeCode: Boolean = false
    var setDir: Boolean = false
    var debug: Boolean = false
    var dir: String = ""
    var outputName: String = ""


    fun getValueOfArg(arg: String): String? {
        val index = args.indexOf(arg)
        if (index != -1 && index != args.size - 1) {
            return args[index + 1]
        }
        return null
    }

    fun processArgs(args: Array<String>) {
        this.args = args

        noCompilation = args.any { it == "--no-compile" }
        showTree = args.any { it == "--show-tree" }
        writeCode = args.any { it == "--write-code" }
        setDir = args.any { it == "--set-dir" }
        debug = args.any { it == "--debug" }
        useCache = args.any { it == "--use-cache" }
        if (debug) {
            Printer.setDebug();
        }

        dir = getValueOfArg("--set-dir")  ?: ""
        if (dir.isNotEmpty() && !dir.endsWith("/")) {
            dir += "/"
        }

        outputName = getValueOfArg("-o") ?: ""
    }
}