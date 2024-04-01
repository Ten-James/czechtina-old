# Input / Output test

for basic input and output we can use `scanf` and `printf` functions from C language. The syntax is the same as in C language.

```
main {
    x:int
    scanf "%d", (adresa x)
    printf "%d", x
}
```
Should be translated to:
```c
int main() {
    int x;
    scanf("%d",&x);
    printf("%d",x);
}
```
