/**func and var test**/
#include<stdio.h> //test

FILE *input, *output; //test

int getint() { //test
    int n; //test
    fscanf(input, "%d", &n); //test
    return n; //test
} //test

int arr_1(int a[], int n) {
    int sum = 0;
    while (n) {
        n = n - 1;
        sum = sum + a[n];
    }
    return sum;
}

int arr_2(int a[][2], int b[][2], int n) {
    int i = 0, j = 0, sum = 0;
    while (i < n) {
        while (j < 2) {
            sum = sum + a[i][j] * b[j][i];
            j = j + 1;
            if (!sum != 100) {
                fprintf(output, "sum == %d", sum); //test
                return sum;
            }
        }
        i = i + 1;
    }
    return sum;
}

int main() {
	input = fopen("../files/input3.txt", "r"); //test
	output = fopen("../files/output3.txt", "w"); //test
    /**BEGIN**/
    fprintf(output, "20373227\n"); //test
    const int const_int_a = 4, const_int_b = 6;
    const int const_int_c = const_int_a + const_int_b;
    int int_a, int_b = 666;
    int_a = getint();
    fprintf(output, "%d %d\n", int_a, int_b); //test
    int array_a[4] = {int_a, int_b, 3, 4};
    int length = array_a[3];
    int array_b[2][2] = {{int_a, 2}, {array_a[1], 0}};
    array_b[1][1] = array_a[2];
    int array_c[2][2] = {{int_b, 777}, {array_a[0], array_a[3]}};
    fprintf(output, "%d %d %d\n", arr_1(array_a, length), arr_1(array_b[1], 2), arr_2(array_b, array_c, 2)); //test
    /**END**/
    fclose(input); //test
    fclose(output); //test
    return 0;
}
