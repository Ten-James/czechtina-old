import utils.readBuildFile
import java.io.File


fun Run(debug: Boolean = false) {
    try {
        val build = readBuildFile()
        if (debug) Printer.setDebug()
        // create build directory
        BuildProject(debug, false)
        //run binary
        val runcmd = "./${build.buildDir}/${build.Output}"
        Printer.info(runcmd)
        val run = Runtime.getRuntime().exec(runcmd)
        run.waitFor()
        val runout = run.inputStream.bufferedReader().readText()
        val runerr = run.errorStream.bufferedReader().readText()
        if (runerr.isNotEmpty()) {
            Printer.err(runerr)
        }
        println(runout)



    }
    catch (e: Exception) {
        Printer.fatal(e.message ?: "Error")
    }
}