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

Now you can also write in czechtina

```cz
pripoj c stdio

fce1 cele x { cele
    y je x plus 2 kafe
    vrat y kafe
}


main {
    cele x kafe
    x je 't' plus (1 plus 2) krat 3 deleno 1 minus 4;
    x je timesTwo x kafe
    printf "%d", x kafe
    vrat 0 kafe
}

timesTwo cele x je cele x krat 2;

```

which is compiled to c

```c
#include "stdio.h"

int timesTwo(int x) {
    return x * 2;
}

int fce1(int x) {
    y = x + 2;
    return y;
}

int main() {
    int x;
    x = 't' + (1 + 2) * 3 / 1 - 4;
    x = timesTwo(x);
    printf("%d", x);
    return 0;
}
```

## Syntax

### Function Definition

normal function definition
```cz
fce1 cele x { cele
    y je x plus 2 kafe
    vrat y kafe
}
```

inline function definition
```cz
fce1 cele x je cele x krat 2;
```


For Loops

```cz

opakuj i:cele -> 1 az 10 {
}

opakuj i:cele je 0; i < 10; i je i + 1 {
}


```


## Table for czechtina.h

[Table here](table.md)

## building compiler

TODO - use intelij xd

## Using Compiler

```bash
java -jar czechtina.jar build/ukol.cz --no-compile --fpeterek --friendly --set-dir build
```

- **arg[0]** - Path to file with main function
- **--help** - Show this help
- **--no-compile** - Do not compile the output C code, it will be created in the same directory as the input file
- **--show-tree** - Show the AST tree
- **--fpeterek** - Uses macros from old czechtina.h file
- **--friendly** - Generate valid C without macros in comment bellow code
- **--set-dir** - Set dir for file creation

## TODO

- Loops, if, else
- invalidate "1 je 3"
- Writing \n in strings
- linking files
- std lib
- string anotation
- structures
- dynamic typing
- range definition


## Credits

Jonatan Lepik - assisstance on keywords **veget** and **bal**
