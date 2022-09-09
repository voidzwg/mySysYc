/**func test**/
#include<stdio.h>  //test

FILE *input, *output; //test

int getint() { //test
    int n; //test
    fscanf(input, "%d", &n); //test
    return n; //test
} //test

int func_a() {
    fprintf(output, "Hello, my compiler!\n"); //test
    return 666;
}

void func_b() {
    fprintf(output, "Hi, my compiler!\n"); //test
    return;
}

int func_c(int a, int b) {
    fprintf(output, "a = %d, b = %d\n", a, b); //test
    return a;
}

void func_d(int c) {
    fprintf(output, "c = %d\n", c); //test
    return;
}

int main() {
	input = fopen("../files/input4.txt", "r"); //test
	output = fopen("../files/output4.txt", "w"); //test
	/**BEGIN**/
    fprintf(output, "20373227\n"); //test
    int a, b, c;
    fprintf(output, "please input a\n"); //test
    a = getint();
    fprintf(output, "please input b\n"); //test
    b = getint();
    fprintf(output, "please input c\n"); //test
    c = getint();
    fprintf(output, "func_a() = %d\n", func_a()); //test
    func_b();
    fprintf(output, "func_c() = %d\n", func_c(a, b)); //test
    func_d(c);
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
