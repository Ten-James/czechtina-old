import os

MAIN_FILE = "main.czh"
BUILD_FOLDER = "build"
DEBUG = True
BUILD_FILE = f"{BUILD_FOLDER}/czechtina.h"


content= ""

def addFileToContent(path, name):
    global content
    newcontent = open(f"{path}/{name}", "r").readlines()
    newcontent = [line for line in newcontent if not line.startswith("//")]
    if DEBUG:
        content = f"{content}\n//DEBUG - {path}/{name}"
    content = content + "\n"
    for line in newcontent:
        if (line.startswith("@include")):
            arg = line.split(" ")[1].strip().replace("\"", "").split("/")
            newPath = "/".join(arg[:-1])
            if newPath != "":
                newPath = f"/{newPath}"
            addFileToContent(path+newPath, arg[-1])
            continue
        if (line.startswith("@")):
            content = content + line.replace("@", "#define", 1)
            continue
        
        content = content + line



if __name__ == "__main__":
    if not os.path.exists(BUILD_FOLDER):
        os.makedirs(BUILD_FOLDER)
    addFileToContent("src", MAIN_FILE)
    with open(BUILD_FILE, "w") as f:
        f.write(content)
    print(f"Build file created: {BUILD_FILE}")
