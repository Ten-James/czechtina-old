# Czechtina

Current version: 0.1.5

Czechtina is programming language based on C and czech language.
Compiler is written in Kotlin using [Klang](https://github.com/j-jzk/klang) Library
For more information about language see [CHANGELOG.md](CHANGELOG.md)

## Good to know

Czechtina started as preprocesor language for C. I had to renamed it to czecheader.

## Example

czecheader is completly valid to write in c.

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

fce1 x:cele { cele
    y je x plus 2
    vrat y
}

timesTwo x:cele -> x * 2

main {
    x je 't' plus (1 plus 2) krat 3 deleno 1 minus 4
    x je timesTwo x
    for i -> 0 do 10 ->
        printf "%d", i
    printf "%d", x
    vrat 0
}
```

which is compiled to c

```c
#include "stdio.h"

int timesTwo(int x);
int fce1(int x);



int timesTwo(int x) {
	return x * 2;
}

int fce1(int x) {
	int y = x + 2;
	return y;
}

int main() {
	int x = 't' + (1 + 2) * 3 / 1 - 4;
	x = timesTwo(x);
	for (int i = 0; i < 10; i = i + 1) 
		printf("%d",i);
	printf("%d",x);
	return 0;
}
```

## Syntax

### Function Definition

main function has to exist for file to compile.

Normal way

```cz
fce1 x:cele { cele
    y je x plus 2
    vrat y
}
```

Inline way

```cz
fce1 x:cele -> x krat 2
```

Syntax:
`<FUNC_NAME> ...params { <FUNC_RET_TYPE> ...lines }`
`<FUNC_NAME> ...params -> <FUNC_RET_TYPE> expression`

### Function calling

Without param 
`zavolej <FUNC_NAME>`

With param
`<FUNC_NAME> ...paramas`

Nested Calling
`<FUNC_NAME> (<FUNC_NAME> ...params) ...params`



### For Loops

```cz

opakuj i -> 1 az 10 {
	//will be executed 0,1,2,3...8,9,10
}

opakuj i:cele -> 1 do 10 {
	//will be executed 0,1,2,3...7,8,9
}

opakuj i je 0; i < 10; i je i + 1 {
	// like c
}


```

### Dump Types 

#### Pointers

Definition:
`VAR_NAME: ukazatel<TYP>`
To get address of variable use virtual Function adresa

### VIRTUAL FUNCTIONS

`adresa VAR_NAME` => `&VAR_NAME`

`hodnota VAR_NAME` => `*VAR_NAME`

`new COUNT` => `malloc(COUNT)`

#### Arrays
`VAR_NAME: pole<TYP>`
`VAR_NAME: pole<TYP, count>`

### Importing 

#### Importing C header files

`pripoj c NAME`

- NAME without .h extension
- Import **lines must be on top of the file**
#### Importing czechtina files
*Preprocessor like link*

TODO

## Old Table for czecheader (czechtina.h)

[Table here](table.md)

## Building compiler

TODO - use intelij xd

## Using Compiler

```bash
java -jar czechtina.jar build/ukol.cz --no-compile --fpeterek --friendly --set-dir build
```

- **arg[0]** - Path to file with main function
- **--help** - Show this help
- **--no-compile** - Do not compile the output C code, it will be created in the same directory as the input file
- **--show-tree** - Show the AST tree
- **--write-code** - Write Code in comment before C code
- **--fpeterek** - Uses macros from old czechtina.h file
- **--friendly** - Generate valid C without macros in comment bellow code
- **--set-dir** - Set dir for file creation

## TODO

- invalidate "1 je 3"
- std lib
- string anotation
- better cli
- file structure
- rest of loop features.
- structures
- range definition
- data borrowing
- fix garbage collector for functions with return value


## Credits

Jonatan Lepik - assisstance on keywords **veget** and **bal**
Jirka Ježek - Klang developer