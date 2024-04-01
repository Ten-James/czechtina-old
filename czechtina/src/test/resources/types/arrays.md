# Arrays test

test for static array creation

```
main {
    x:pole<int,3> = [1,2,3]
    y:array<int,3> = [1,2,3]
    z = [1,2,3]
    return 0;
}
```
Should be translated to:

```c
int main() {
    int* x = {1,2,3};
    int* y = {1,2,3};
    int* z = {1,2,3};
    return 0;
}
```
