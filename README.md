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



## Table for czechtina.h

[Table here](table.md)

## Building header file

```bash
python utils/build.py
```

## building compiler

TODO - use intelij xd

## TODO

- Loops
- fpeterek 
- Writing \n
- linking files
- std lib
- string anotation
- structures


## Credits

Jonatan Lepik - assisstance on key words **veget** and **bal**
