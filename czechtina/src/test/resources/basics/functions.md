```
timestwo a:int -> a * 2

timesthree a:int { int
    return a * 3
}

main {
    x = timestwo 5
    y = timesthree 5
    x += x + 5 * y
    
    return 0
}
```
Should be translated to:
```c
int timesthree(int a);
int timestwo(int a);

int timesthree(int a) {
    return a * 3;
}

int timestwo(int a) {
    return a * 2;
}


int main() {
    int x = timestwo(5);
    int y = timesthree(5);
    x += x + 5 * y;
	return 0;
}
```
