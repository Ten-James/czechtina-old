# CZECHTINA - CHANGELOG

### v 0.1.5

- function parameters can be casted to const via `@`
- function can take entire heap memory via `&` it will automatically deallocate memory
- templating via T keyword (T is replaced by type, available T - T999999999999999 types)
- function overloading
- `as` or `jako` keyword for type casting
- `new` virtual function for allocating memory
- automatical deallocation of memory (weak - garbage collector)
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