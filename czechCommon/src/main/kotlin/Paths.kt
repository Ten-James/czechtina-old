fun GetFileLinkedFilePath(fromFile:String, name:String):String {
    return GetFolderFromFilePath(fromFile) + AddFileExtension(name)
}

fun RemoveFileExtension(filePath: String):String = filePath.substringBeforeLast(".")


fun AddFileExtension(filePath: String) = RemoveFileExtension(filePath) + ".cz"


fun GetFolderFromFilePath(filePath:String): String {
    return filePath.substringBeforeLast("/") + "/"
}