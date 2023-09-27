import os

MAIN_FILE = "main.czh"
BUILD_FOLDER = "build"
DEBUG = True
BUILD_FILE = f"{BUILD_FOLDER}/czechtina.h"


content= ""
defined=""

def proccessInclude(line, path):
    arg = line.split(" ")[1].strip().replace("\"", "").split("/")
    newPath = "/".join(arg[:-1])
    if newPath != "":
        newPath = f"/{newPath}"
    addFileToContent(path+newPath, arg[-1])

def proccessDefine(line, singleLine=True):
    global content, defined
    if singleLine:
        content += line.replace("@", "#define", 1)
    else:
        defined = line.split(" ")[1].strip()


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
            proccessDefine(line, len(line.split(" ")) == 3)
        elif (line.startswith("- ")):
            content += line.replace("-", "#define", 1).replace("\n","") + f" {defined}\n"
        else:
            content += line



if __name__ == "__main__":
    if not os.path.exists(BUILD_FOLDER):
        os.makedirs(BUILD_FOLDER)
    addFileToContent("src", MAIN_FILE)
    with open(BUILD_FILE, "w") as f:
        f.write(content)
    print(f"Build file created: {BUILD_FILE}")
