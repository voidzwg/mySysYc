const int my=20373778,ra=123456;
void test1(){
    printf("%d\n",my);
    return;
}

void test2(int x){
    printf("%d\n",x+my);
}
int main(){
    test1();
    test2(ra);
    int x = 0;
    if(0==1){
        x = x + 1;
    }
    if(0==0){
        x = x + 3;
    }
    printf("%d\n",x);
    if(0<=1){
        x = x + 1;
    }
    if(0<=0){
        x = x + 3;
    }
    printf("%d\n",x);
    if(0>=1){
        x = x + 1;
    }
    if(0>=0){
        x = x + 3;
    }
    printf("%d\n",x);
    if(0!=1){
        x = x + 1;
    }
    if(0!=0){
        x = x + 3;
    }
    printf("%d\n",x);
    if(0<1){
        x = x + 1;
    }
    if(0<0){
        x = x + 3;
    }
    printf("%d\n",x);
    if(0>1){
        x = x + 1;
    }
    if(0>0){
        x = x + 3;
    }
    printf("%d\n",x);

    printf("%d\n",5%3);
    while(0);
    {}
    printf("end\n");

    return 0;
}