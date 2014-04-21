#include<stdio.h>
#include<math.h>
#include<stdint.h>
#include "emmintrin.h"
#include "smmintrin.h"


int32_t probe(int32_t target, int32_t* keys, int32_t* loads, int32_t* multi, int32_t S);

int main(int argc, char** argv){

	int32_t B,S,h,N;
	int32_t* keys;
	int32_t* loads;
	int32_t multi[2];
	int32_t i;
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
	
	for(i=0;i<h;i++){
		fscanf(fp, "%d", multi[i]);
	}

	keys = (int32_t*) malloc(pow(2,10)*sizeof(int32_t));
	loads = (int32_t*) malloc(pow(2,10)*sizeof(int32_t));

	for(i=0;i<pow(2,10);i++){
		fscanf(fp, "%d", keys[i]);
		fscanf(fp, "%d", loads[i]);
	}

	while(scanf("%d", &i==1)){
		int32_t load = probe(i,keys,loads,multi,S);
		if(load){
			printf("%d %d\n",i,load);
		}
	}
	
}

int32_t probe(int32_t target, int32_t* keys, int32_t* loads, int32_t* multi, int32_t S){
	int32_t b1,b2;
	int32_t ret[4];

	__m128i tarPack = _mm_set1_epi32(target);
	__m128i multiNumPack = _mm_set_epi32(multi[0],0,multi[1],0);
	__m128i multiedPack = _mm_mullo_epi32(tarPack, multiPack);
	__m128i bucketPack = _mm_srli_epi32(multiedPack, 34-S);
	__m128i entryPack = _mm_slli_epi32(bucketPack, 2);
	b1 = _mm_extract_epi32(entryPack, 3);
	b2 = _mm_extract_epi32(entryPack, 1);
	
	//get the keys and loads from bucket b1 and b2
	__m128i b1keys = _mm_set_epi32(keys[b1],keys[b1+1],keys[b1+2],keys[b1+3]);
	__m128i b1loads = _mm_set_epi32(loads[b1],loads[b1+1],loads[b1+2],loads[b1+3]);
}
	__m128i b2keys = _mm_set_epi32(keys[b2],keys[b2+1],keys[b2+2],keys[b2+3]);
	__m128i b2loads = _mm_set_epi32(loads[b2],loads[b2+1],loads[b2+2],loads[b2+3]);

	__m128i b1cmpeq = _mm_cmpeq_epi32(tarPack, b1keys);
	__m128i b2cmpeq = _mm_cmpeq_epi32(tarPack, b2keys);
	__m128i b1loadAnd = _mm_and_si128(b1loads, b1cmpeq);
	__m128i b2loadAnd = _mm_and_si128(b2loads, b2cmpeq);

	__m128i result = _mm_or_si128(b1loadAnd,b2loadAnd);

	ret[0] = _mm_extract_epi32(result, 0);
	ret[1] = _mm_extract_epi32(result, 1);
	ret[2] = _mm_extract_epi32(result, 2);
	ret[3] = _mm_extract_epi32(result, 3);

	return ret[0]|ret[1]|ret[2]|ret[3];

