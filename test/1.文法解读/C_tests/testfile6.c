/**if else and while test**/
#include<stdio.h> //test

FILE *input, *output; //test

int getint() { //test
    int n; //test
    fscanf(input, "%d", &n); //test
    return n; //test
} //test

int main() {
	input = fopen("../files/input6.txt", "r"); //test
	output = fopen("../files/output6.txt", "w"); //test
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
    int sum = 0;
    while (a) {
        sum = sum + a / (b * (c % d));
        a = a - 1;
        fprintf(output, "a = %d, sum = %d\n", a, sum); //test
    }
    fprintf(output, "please input a again\n"); //test
    a = getint();
    if (a == c) {
        fprintf(output, "a == c\n"); //test
    } else if (a != c) {
        fprintf(output, "a != c\n"); //test
    }
    if (a >= 100) {
        fprintf(output, "a >= 100\n"); //test
    } else if (a < 100) {
        if (a > 0)
            fprintf(output, "0 < a < 100\n"); //test
        else if (a <= 0)
            fprintf(output, "a <= 0\n"); //test
    }
    while (1) {
        if (a == b) {
            fprintf(output, "a == b\n"); //test
            break;
        } else if (a > b) {
            a = a - 1;
            continue;
        } else {
            a = a + 1;
            continue;
        }
    }
    if (c != d) {
        while (c != d) {
            if (c > d) {
                c = c - 1;
                continue;
            }
            c = c + 1;
        }
    }
    fprintf(output, "a = %d, b = %d, c = %d, d = %d\n", a, b, c, d); //test
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
