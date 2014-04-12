#include<stdio.h>
#include<stdint.h>
#include "emmintrin.h"
#include "smmintrin.h"

int main(int argc, char** argv){
	int32_t arr[4];
	int32_t ret;
	__m128i tmp = _mm_set_epi32( 0, 222, 0, 0);
	
	arr[0] = _mm_extract_epi32(tmp,0);
	arr[1] = _mm_extract_epi32(tmp,1);
	arr[2] = _mm_extract_epi32(tmp,2);
	arr[3] = _mm_extract_epi32(tmp,3);
	ret = arr[0]|arr[1]|arr[2]|arr[3];
	
	printf("Element: %d\n", ret);
	
}
