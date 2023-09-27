import os

MAIN_FILE = "main.czh"
BUILD_FOLDER = "build"
DEBUG = True
WRITE_TABLE = True
TABLE_NAME = "table.md"
BUILD_FILE = f"{BUILD_FOLDER}/czechtina.h"


content= ""
defined=""

def proccessInclude(line, path):
    arg = line.split(" ")[1].strip().replace("\"", "").split("/")
    newPath = "/".join(arg[:-1])
    if newPath != "":
        newPath = f"/{newPath}"
    addFileToContent(path+newPath, arg[-1])

def proccessDefine(line, multiLine=True):
    global content, defined
    if multiLine:
        defined = line.split(" ")[1].strip()
    else:
        content += line.replace("@", "#define", 1)


def addFileToContent(path, name):
    global content
    newcontent = open(f"{path}/{name}", "r").readlines()
    newcontent = [line for line in newcontent if not line.startswith("//")]
    if DEBUG:
        content = f"{content}\n//DEBUG - {path}/{name}"
    content += "\n"
    for line in newcontent:
        if (line.startswith("@include")):
            proccessInclude(line, path)
        elif (line.startswith("@ ")):
            proccessDefine(line, len(line.split(" ")) == 2)
        elif (line.startswith("- ")):
            content += line.replace("-", "#define", 1).replace("\n","") + f" {defined}\n"
        else:
            content += line


def writeTable():
    global content
    dic = {}
    # add all defines to dictionary
    # if exist add to list else create new list
    for line in content.split("\n"):
        if line.startswith("#define"):
            line = line.split(" ")
            value = line[1]
            key = " ".join(line[2:])
            if key in dic:
                dic[key].append(value)
            else:
                dic[key] = [value]
    # sort dictionary
    dic = {k: v for k, v in sorted(dic.items(), key=lambda item: item[0])}
    # write to file
    with open(TABLE_NAME, "w", encoding="utf-8") as f:
        f.write("| Anglický název | Český název |\n")
        f.write("| ----------- | -------------- |\n")
        for key, value in dic.items():
            if (key is not "1"):
                f.write(f"| {key} | {', '.join(value)} |\n")




if __name__ == "__main__":
    if not os.path.exists(BUILD_FOLDER):
        os.makedirs(BUILD_FOLDER)
    addFileToContent("src", MAIN_FILE)
    with open(BUILD_FILE, "w") as f:
        f.write(content)
    print(f"Build file created: {BUILD_FILE}")
    if WRITE_TABLE:
        writeTable()
        print(f"Table file created: {TABLE_NAME}")
