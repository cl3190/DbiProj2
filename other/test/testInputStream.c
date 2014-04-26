#include<stdio.h>

int main(int argc, char** argv){
	int i;
	while(scanf("%d", &i) == 1){
		printf("Got one: %d\n", i);
	}
	return 0;
}
