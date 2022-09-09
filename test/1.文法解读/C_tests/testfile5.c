/**func and calc and simple if test**/
#include<stdio.h>

FILE *input, *output; //test

int getint() {
    int n;
    fscanf(input, "%d", &n); //test
    return n;
}

int add(const int a, const int b) {
    const int c = a + b;
    fprintf(output, "%d + %d = %d\n", a, b, c); //test
    return c;
}

int diff(int a, int b) {
    int c = a - b;
    fprintf(output, "%d - %d = %d\n", a, b, c); //test
    return c;
}

int mul(int a, int b) {
    int c = a * b;
    fprintf(output, "%d * %d = %d\n", a, b, c); //test
    return c;
}

int div(int a, int b) {
    if (b == 0) {
        fprintf(output, "b cannot be 0!\n"); //test
        return -1;
    }
    int c = a / b;
    fprintf(output, "%d / %d = %d\n", a, b, c); //test
    return c;
}

int mod(int a, int b) {
    if (b == 0) {
        fprintf(output, "b cannot be 0!\n"); //test
        return -1;
    }
    int c = a % b;
    fprintf(output, "%d mod %d = %d\n", a, b, c); //test
    return c;
}

int main() {
	input = fopen("../files/input5.txt", "r"); //test
	output = fopen("../files/output5.txt", "w"); //test
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
    int add_r, diff_r, mul_r, div_r, mod_r;
    add_r = add(a * b / c, (a + b) * c);
    diff_r = diff(c % d + 2, a * (c / b));
    mul_r = mul((a / (c * (b - c)) - b) % d, -b + c);
    div_r = div(a + c, -(a / (c - d)));
    mod_r = mod(a * (b % c), +(-a * (+-+c)));
    fprintf(output, "add_r = %d, diff_r = %d, mul_r = %d, div_r = %d, mod_r = %d\n", add_r, diff_r, mul_r, div_r, mod_r); //test
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
