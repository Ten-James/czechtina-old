# Dynamic test

For dynamic memory allocation we can use `new`.

syntax:

`SOME_VARIABLE = new TYPE, SIZE`

`SOME_VARIABLE = new TYPE`


```
main {
    x = new int, 5
    z = x[3]
    x[3] = 7
}
```
Should be translated to:
```c
int main() {
    int* x = (int*)malloc(5 * sizeof(int));
    int z = x[3];
    x[3] = 7;
    if(x)free(x);
}
```
