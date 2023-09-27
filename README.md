# Czechtina

czechtina is precompiled language for C

## Example

czechtina is completly valid to write in c.

```c
#include <stdio.h>
#include "czechtina.h"

cele zacatek bal pak
    vypisaj rozbal "Hello World!" zabal kafe
    finito
potom

```

## Table

[Table here](table.md)

## Building header file

```bash
python utils/build.py
```

Build preprocessor header file so you don't have write in czechtina and use its completely in C.

## TODO

- Entire preprocessor file
- custom preprocessing unit
- custom syntax highlighting


## description

- *.czh files is language for defining czechtina.
- *.cz files are czechtina files without preprocessor header.
