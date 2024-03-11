import utils.DependencyFile
import utils.readBuildFile
import java.io.File

fun BuildMain(debug: Boolean = false) {
    val build = readBuildFile()
    // create build directory
    val builddir = File(build.buildDir)
    if (!builddir.exists()) {
        builddir.mkdirs()
    }
    //compile main
    val maincmd = "czechtina ${build.main} --set-dir ${build.buildDir}/compiled ${if (debug) "--debug" else ""} --use-cache"
    Printer.info(maincmd)
    val main = Runtime.getRuntime().exec(maincmd)
    main.waitFor()
    val mainout = main.inputStream.bufferedReader().readText()
    val mainerr = main.errorStream.bufferedReader().readText()
    if (mainerr.isNotEmpty()) {
        Printer.err(mainerr)
    }
    if (mainout.isNotEmpty()) {
        print(mainout)
    }

    val allfiles = mutableListOf<String>()
    val compiledDir = File("${build.buildDir}/compiled")
    compiledDir.walk().forEach {
        if (it.isFile) {
            allfiles.add(build.buildDir +it.absolutePath.substringAfterLast(build.buildDir))
        }
    }


    val withoutMain = allfiles.filter { it != "${build.buildDir}/compiled/${RemoveFirstFolder(RemoveFileExtension(build.main))}.c" }


    //compile main.c
    val gcccmd = "gcc ${build.buildDir}/compiled/${RemoveFirstFolder(RemoveFileExtension(build.main))}.c ${withoutMain.joinToString(" ")} -o ${build.buildDir}/${build.Output}"
    Printer.info(gcccmd)
    val gcc = Runtime.getRuntime().exec(gcccmd)
    gcc.waitFor()
    val gccout = gcc.inputStream.bufferedReader().readText()
    val gccerr = gcc.errorStream.bufferedReader().readText()
    if (gccerr.isNotEmpty()) {
        Printer.err(gccerr)
    }
    if (gccout.isNotEmpty()) {
        print(gccout)
    }
    Printer.success("Build successful");
}

fun CompileFile(debug: Boolean = false, file: String) {
    val build = readBuildFile()
    // create build directory
    val builddir = File(build.buildDir)
    if (!builddir.exists()) {
        builddir.mkdirs()
    }
    //compile file
    val cmd = "czechtina $file --set-dir ${build.buildDir}/compiled ${if (debug) "--debug" else ""} --use-cache"
    Printer.info(cmd)
    val process = Runtime.getRuntime().exec(cmd)
    process.waitFor()
    val out = process.inputStream.bufferedReader().readText()
    val err = process.errorStream.bufferedReader().readText()
    if (err.isNotEmpty()) {
        Printer.err(err)
    }
    if (out.isNotEmpty()) {
        print(out)
    }
}

fun BuildProject(debug: Boolean = false, force: Boolean = false) {
    val build = readBuildFile();
    val builddir = File(build.buildDir)
    if (!builddir.exists()) {
        builddir.mkdirs()
    }
    val needCompile = DependencyFile.walkFiles(force)
    needCompile.forEach {
        if (it != build.main)
            CompileFile(debug, it)
    }
    if (needCompile.isNotEmpty()) {
        BuildMain(debug)
    }
}