# Virtual Functions test

In czechtina we have lots of virtual functions:

- `adresa` - returns the address of a variable
- `typeof` - returns the type of a variable as string literal (e.g. "Pointer<Primitive<int>>")
- `inC` - includes a C code
- `println` - prints to the console with a newline
- `print` - prints to the console without a newline

Virtual functions works like function in czechtina but thy are not defined in the czechtina code. They are defined at compile time.

```
main {
    a:int = 5
    b:double = 5.0
    c = adresa b
    d = adresa a

    println a, " ", b, " ", c, " ", d
    println (typeof c)
    inC "int g = 5"
}
```

Should be translated to:

```c
int main() {
	int a = 5;
	double b = 5.0;
	double* c = &b;
	int* d = &a;
	printf("%d",a);
	fputs(" ",stdout);
	printf("%f",b);
	fputs(" ",stdout);
	printf("%x",c);
	fputs(" ",stdout);
	printf("%x",d);fputs("\n",stdout);
	fputs("Pointer<Primitive<double>>",stdout);fputs("\n",stdout);
	int g = 5;
}
```
