# GenericFunctions test

```
sumArr arr:pointer<T>, size:int { T
    sum:T = 0
    for i:int -> 0 do size ->
        sum = sum + arr[i]
    return sum
}

main {
    arr = [1, 2, 3, 4, 5]
    sum = sumArr arr, 5
    arr2 = [1.0, 2.0, 3.0, 4.0, 5.0]
    sum2 = sumArr arr2, 5
}
```

Should be translated to:

```c
double sumArr_v2(double* arr, int size);
int sumArr_v1(int* arr, int size);


double sumArr_v2(double* arr, int size) {
	double sum = 0;
	for (int i = 0; i < size; i = i + 1) {
		sum = sum + arr[i];
	}
	return sum;
}
int sumArr_v1(int* arr, int size) {
	int sum = 0;
	for (int i = 0; i < size; i = i + 1) {
		sum = sum + arr[i];
	}
	return sum;
}


int main() {
	int* arr = {1,2,3,4,5};
	int sum = sumArr_v1(arr,5);
	double* arr2 = {1.0,2.0,3.0,4.0,5.0};
	double sum2 = sumArr_v2(arr2,5);
}
```
