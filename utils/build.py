import os

MAIN_FILE = "main.czh"
BUILD_FOLDER = "build"
VERSION = "0.1"
DEBUG = True
WRITE_TABLE = True
TABLE_NAME = "table.md"
BUILD_FILE = f"{BUILD_FOLDER}/czechtina.h"


content= ""
defined=""
dictionary = {}

def proccessInclude(line, path):
    arg = line.split(" ")[1].strip().replace("\"", "").split("/")
    newPath = "/".join(arg[:-1])
    if newPath != "":
        newPath = f"/{newPath}"
    addFile(path+newPath, arg[-1])

def proccessDefine(line, multiLine=True, debugArr = []):
    global dictionary, defined
    if multiLine:
        split = line.split(" ")
        defined = " ".join(split[1:]).strip()
        if defined in dictionary.keys():
            raise Exception(f"Define {defined} already exist, {debugArr}")
        dictionary[defined] = [[], debugArr]
    else:
        split = line.split(" ")
        data = " ".join(split[2:]).strip()
        if data in dictionary.keys():
            raise Exception(f"Define {data} already exist, {debugArr}")
        dictionary[data] = [[split[1].strip()], debugArr]


def addFile(path, name):
    global content, dictionary, defined
    newcontent = open(f"{path}/{name}", "r").readlines()
    for line in newcontent:
        if (line.startswith("@include")):
            proccessInclude(line, path)
        elif (line.startswith("@@ ")):
            proccessDefine(line, True, [f"{path}/{name}"])
        elif (line.startswith("@ ")):
            proccessDefine(line, False, [f"{path}/{name}"])
        elif (line.startswith("- ")):
            dictionary[defined][0].append(line.replace("-", "", 1).replace("\n","").strip())


def writeTable():
    global dictionary
    # write to file
    with open(TABLE_NAME, "w", encoding="utf-8") as f:
        f.write("| Anglický název | Český název |\n")
        f.write("| ----------- | -------------- |\n")
        for key, value in dictionary.items():
            if (key is not "1"):
                f.write(f"| {key} | {', '.join(value[0])} |\n")


def testCzechtinaWords():
    global dictionary
    wordset = {}
    for key, value in dictionary.items():
        for line in value[0]:
            if line in wordset.keys():
                raise Exception(f"Word {line} already exist {wordset[line]} and {key}")
            wordset[line] = key


def generateContent():
    global content, dictionary
    content = "#ifndef CZECHTINA_H\n#define CZECHTINA_H\n\n"
    for key, value in dictionary.items():
        if DEBUG:
            content += f"//DEBUG - {value[1]}\n"
        for line in value[0]:
            content += f"#define {line} {key}\n"
        content += "\n"
    content += f"//CZECHTINA BY JAMES version:{VERSION}\n"
    content += "#endif"
        


if __name__ == "__main__":
    if not os.path.exists(BUILD_FOLDER):
        os.makedirs(BUILD_FOLDER)
    addFile("src", MAIN_FILE)
    testCzechtinaWords()
    generateContent()
    with open(BUILD_FILE, "w") as f:
        f.write(content)
    print(f"Build file created: {BUILD_FILE}")
    if WRITE_TABLE:
        writeTable()
        print(f"Table file created: {TABLE_NAME}")
