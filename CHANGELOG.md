# CZECHTINA - CHANGELOG

### v 0.1.6.5

#### Main feature - packages

All czechtina files now can have package name instead of main function syntax is `package <[A-Z:]+>`.
`::` acts like splitter and makes sub packages.

Things to mind:

- Recursive package files, all files will be compiled just once
- Dependency checker, czcli will make cache file that compiler uses to include package.
- if package file is modified all dependency files has to be compiled right now.
- to use package in other files use syntax: `use <package_name>`, its free to use it anywhere in code. its preprocessor stuff.



#### Other

- fixing bugs
- added +=, -=, *=, /=
- preincrement and postincrement (++, --)
- better printer for cli
- better definition for virtual functions
- methods for structures
- easier alocating for arrays
- function variants are now disabled
- recreated better type checker

#### CZCLI

- commands `init`, `build`, `run`, `clean` for working with project
- currently cache and compile only on modified type


### v 0.1.6

- throw keyword
- variables now has to start with lowercase letter
- functions now has to start with lowercase letter
- types now has to start with uppercase letter
- structures in work
- while loop
- `-1` is now valid
- undefining functions via `#undefine <function_name>`

### v 0.1.5

- function parameters can be cast to const via `@`
- function can take entire heap memory via `&` it will automatically deallocate memory
- templating via T keyword (T is replaced by type, available T - T999999999999999 types)
- function overloading
- `as` or `jako` keyword for type casting
- `new` virtual function for allocating memory
- automatically deallocation of memory (weak - garbage collector)
- preprocessor
- file structure
- including file (before compilation)

### v 0.1.4

- type deducing
- no longer semicolon needed
- inline for loop with dynamic type
```
opakuj i -> 0 do 10 ->
    s += i
```
- inline function support with dynamic type
```
timesTwo x:cele -> x * 2
```
- type definition support
```
typ b je cele
```
- else statement, else if statement
- boolean support
- function declaration for compiling C


### v 0.1.3
- array support