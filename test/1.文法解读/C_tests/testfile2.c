/**const and var test**/
#include<stdio.h> //test

FILE *input, *output; //test

int getint() { //test
    int n; //test
    fscanf(input, "%d", &n); //test
    return n; //test
} //test

int main() {
	input = fopen("../files/input2.txt", "r"); //test
	output = fopen("../files/output2.txt", "w"); //test
    /**BEGIN**/
    fprintf(output, "20373227\n"); //test
    const int const_int_a = 4, const_int_b = 6, const_int_c = const_int_a + const_int_b;
    fprintf(output, "%d %d %d\n", const_int_a, const_int_b, const_int_c); //test
    int int_a, int_b = 666;
    int_a = getint();
    fprintf(output, "%d %d\n", int_a, int_b); //test
    const int const_array_a[3] = {0, 1, 2};
    const int const_array_b[4] = {const_int_a, const_int_b, const_int_c, 44};
    const int const_array_c[2][2] = {{const_int_a, 4}, {6, const_int_b}};
    fprintf(output, "%d %d %d\n", const_array_a[2], const_array_b[2], const_array_c[1][1]); //test
    int array_a[4] = {int_a, int_b, 3, 4};
    int array_b[2][3] = {{int_a, 2, 3}, {array_a[1], array_a[2], array_a[3]}};
    fprintf(output, "%d %d %d %d\n", array_a[1], array_a[2], array_b[0][0], array_b[1][2]); //test
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
