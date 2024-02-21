fun main(args: Array<String>) {

    if (args.isEmpty()) {
        Printer.printHelp()
        return
    }

    if (args.any { it == "init" }) {
        val defaultName = getValueOfArg(args,"-n")
        val defaultPath = getValueOfArg(args,"-d")
        Init(defaultName, defaultPath)
        return
    }

    Printer.printCLIHelp()
}


fun getValueOfArg(args: Array<String>,arg: String): String? {
    val index = args.indexOf(arg)
    if (index != -1 && index != args.size - 1) {
        return args[index + 1]
    }
    return null
}
