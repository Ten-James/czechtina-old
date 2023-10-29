package utils

object ArgsProvider {
    var args: Array<String> = arrayOf()

    var noCompilation: Boolean = false
    var showTree: Boolean = false
    var writeCode: Boolean = false
    var fpeterek: Boolean = false
    var friendly: Boolean = false
    var setDir: Boolean = false
    var debug: Boolean = false
    var dir: String = ""

    fun processArgs(args: Array<String>) {
        this.args = args

        noCompilation = args.any { it == "--no-compile" }
        showTree = args.any { it == "--show-tree" }
        writeCode = args.any { it == "--write-code" }
        fpeterek = args.any { it == "--fpeterek" }
        friendly = args.any { it == "--friendly" }
        setDir = args.any { it == "--set-dir" }
        debug = args.any { it == "--debug" }

        val setDirIndex = args.indexOf("--set-dir")
        if (setDirIndex != -1 && setDirIndex != args.size - 1) {
            dir = args[setDirIndex + 1] + "/"
        }
    }
}