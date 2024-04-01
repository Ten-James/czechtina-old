# Hello, World!

This is a simple example of a program that prints "Hello, world" to the console.

```
main {
    println "Hello, world"
    return 0
}
```
Should be translated to:
```c
int main() {
    fputs("Hello, world",stdout);fputs("\n",stdout);
	return 0;
}
```
