# ArrayAsParameter test

test for passing array as parameter


```
struct DATA {
    data:int
    next:DATA
}

create b:DATA { void
    b.data = 2
}

main {
    a = new DATA
    create a
}

```

Should be translated to:

```c
typedef struct {
	int data;
	DATA * next;
 } DATA;

void create(DATA * b);

void create(DATA * b) {
	b->data = 2;
}

int main() {
	DATA * a = (DATA *)malloc(sizeof(DATA));
	create(a);
	if(a)free(a);

}
```
