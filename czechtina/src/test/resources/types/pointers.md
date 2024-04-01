# Pointers test and arithmetic

For pointers we can use `pointer` type with any other type. 

For working with pointers we can use arithmetic operations `+` and `-`.

For dereferencing we can use `hodnota` function.
For getting address of variable we can use `adresa` function.


```
main {
    x:pointer<int>
    y = x + 3
    z = x - y
}
```
Should be translated to:
```c
int main() {
    int* x;
    int* y = x + 3;
    int z = x - y;
}
```
