#include<stdio.h>
#include<math.h>
#include<stdint.h>

int main(int argc, char** argv){

	int32_t B,S,h,N;
	int32_t* keys;
	int32_t* loads;
	FILE *fp = fopen(*++argv, "r");
	if(fp == NULL){
		printf("Cannot find the dumpfile.\n");
		return 1;
	}
	
	fscanf(fp, "%d", &B);
	fscanf(fp, "%d", &S);
	fscanf(fp, "%d", &h);
	fscanf(fp, "%d", &N);
	if(B!=4 || h!=2){
		printf("B must be 4 and h must be 2.\n");
		return 1;
	}

	
	
	
}
