/**&& and || test**/
#include<stdio.h>

FILE *input, *output; //test

int getint() {
    int n;
    fscanf(input, "%d", &n); //test
    return n;
}

int main() {
	input = fopen("../files/input1.txt", "r"); //test
	output = fopen("../files/output1.txt", "w"); //test
    /**BEGIN**/
    fprintf(output, "20373227\n"); //test
    int a, b, c, d;
    fprintf(output, "please input a\n"); //test
    a = getint();
    fprintf(output, "please input b\n"); //test
    b = getint();
    fprintf(output, "please input c\n"); //test
    c = getint();
    fprintf(output, "please input d\n"); //test
    d = getint();
    fprintf(output, "a = %d, b = %d, c = %d, d = %d\n", a, b, c, d); //test
    int array_a[2][2] = {{a, b}, {c, d}};
    if ((array_a[0][0] >= array_a[0][1] && array_a[0][0] >= array_a[1][0]) || !(array_a[1][1] < array_a[1][0] || array_a[1][1] < array_a[0][1])) {
        fprintf(output, "Hello, &&\n"); //test
    }
    else {
        fprintf(output, "Hello, ||\n"); //test
    }
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
