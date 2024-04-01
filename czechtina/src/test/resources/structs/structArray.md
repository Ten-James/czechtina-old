# structArray test

```
struct DATA {
    data:int
    next:DATA
}

main {
    a = new DATA
    a.data = 1
    b = new DATA
    b.data = 2
    a.next = b
    arr = new DATA, 2
    arr[0] = a
    arr[1] = b
    print a.data
    print a.next.data
    print arr[0].data
    print arr[0].next.data
    return 0
}
```

```c
typedef struct {
    int data;
    DATA * next;
} DATA;

int main() {
    DATA * a = (DATA *)malloc(sizeof(DATA));
    a->data = 1;
    DATA * b = (DATA *)malloc(sizeof(DATA));
    b->data = 2;
    a->next = b;
    DATA ** arr = (DATA **)malloc(2 * sizeof(DATA *));
    arr[0] = a; arr[1] = b;
    printf("%d",a->data);
    printf("%d",a->next->data);
    printf("%d",arr[0]->data);
    printf("%d",arr[0]->next->data);
    if(a)free(a);
    if(b)free(b);
    if(arr)free(arr);
    return 0;
}
```
