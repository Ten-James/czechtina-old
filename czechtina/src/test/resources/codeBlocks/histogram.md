# Histogram test

Minor functionality test for pointers and input/output.

```
zpracuj histo:pointer<int>,minimum:int { void
    c:int = 0
    scanf "%d", (adresa c)
    for i:int -> 0 do 9 {
        if c == i+minimum {
            histo[i] = histo[i] + 1
            vrat
        }
    }
    histo[9] = histo[9] + 1
}

main {
    t:char
    histo:pointer<int> = [0,0,0,0,0,0,0,0,0,0]
    scanf "%c", (adresa t)
    if 'v' != t != 'h' {
        printf "Neplatny mod vykresleni\n"
        vrat 1
    }
    n:int
    minimum:int
    scanf "%d", (adresa n)
    scanf "%d", (adresa minimum)
    for i:cele -> 0 do n {
        zpracuj histo, minimum
    }
    vrat 0
}
```
Should be translated to:
```c
void zpracuj(int* histo, int minimum);

void zpracuj(int* histo, int minimum) {
	int c = 0;
	scanf("%d",&c);
	for  (int i = 0; i < 9; i = i + 1) {
		if (c == i + minimum) {
			histo[i] = histo[i] + 1;
			return ;
		}
	}
	histo[9] = histo[9] + 1;
}

int main() {
	char t;
	int* histo = {0,0,0,0,0,0,0,0,0,0};
	scanf("%c",&t);
	if ('v' != t && t != 'h') {
		printf("Neplatny mod vykresleni\n");
		return 1;
	}
	int n;
	int minimum;
	scanf("%d",&n);
	scanf("%d",&minimum);
	for  (int i = 0; i < n; i = i + 1) {
		zpracuj(histo,minimum);
	}
	return 0;
}
```