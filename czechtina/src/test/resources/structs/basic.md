# basic structures test

test for basic structures data types


```
struct DATA {
    x:int
    y:DATA
}

main {
    b = new DATA
    b.x = 5
    a = b.x
}
```

Should be translated to:

```c
typedef struct { int x; DATA * y; } DATA;

int main() {
    DATA * b = (DATA *)malloc(sizeof(DATA));
    b->x = 5;
    int a = b->x;
    if(b)free(b);
}
```
