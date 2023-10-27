# Czechtina

Current version: 0.1.6

Czechtina is programming language based on C and czech language.
Czechtina should be faster to write than c and with additional features, easier to maintain.
lot of features are still missing, but it is still in development.
Compiler is written in Kotlin using [Klang](https://github.com/j-jzk/klang) Library
For more information about language see [CHANGELOG.md](CHANGELOG.md)

## Good to know

Czechtina started as preprocesor language for C. I had to renamed it to czecheader.

## Example

czecheader is completely valid to write in c.

```c
#include <stdio.h>
#include "czechtina.h"

cele zacatek bal pak
    vypisaj rozbal "Hello World!" zabal kafe
    finito
potom

```

Now you can also write in czechtina, which is compiled to c.

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

### Variables

#### Definition

`VAR_NAME:<TYPE>`

#### Assignment

`VAR_NAME je <VALUE>`

#### Type Casting

`VAR_NAME je <VALUE> jako <TYPE>`
`VAR_NAME je <VALUE> as <TYPE>`

#### Type Deduction

`VAR_NAME je <VALUE>`

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

### Working with memory

#### Allocating memory

`VAR_NAME je new <SIZE>`

#### Moving ownership of memory to function

Setted as paramater in function via `&`

```cz
reduceArr x:&pointer<int>, size:int {
    //do something with x
    //x is deallocated after function ends
}
```

Calling function with `&`, it creates variant of function with memory deallocation

```cz
another x:pointer<int> ....

main {
    arr je new 10 jako pointer<int>
    another &arr, 10
}

```

### Function Overloading

```cz
fce1 x:cele -> x krat 2
fce1 x:pointer<cele> -> (hodnota x) krat 2
```

### Function Templating

```cz
fce1 x:T -> x krat 2
fce1 x:T, y:T -> x krat y
fce1 x:T, y:T1 -> x krat y
```


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
To get address of variable use virtual Function `adresa`

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

`pripoj NAME` - links file *NAME.cz* to current file


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

- std lib
- string anotation
- better cli
- file structure
- rest of loop features.
- structures
- range definition
- pointer array borrow checking
- check validation of return type

## Credits

Jonatan Lepik - assistance on keywords **veget** and **bal**

Jirka Ježek - Klang developer