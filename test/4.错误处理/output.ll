@a = constant [3 x i32] [i32 1, i32 2, i32 33]
@aa = constant [3 x [2 x i32]] [[2 x i32] [i32 99, i32 88], [2 x i32] [i32 77, i32 66], [2 x i32] [i32 55, i32 44]]
@bb = global [2 x [3 x i32]] [[3 x i32] [i32 3, i32 6, i32 8], [3 x i32] [i32 7, i32 6, i32 -1]]
@b = global [5 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5]
@ccc = constant i32 114514
@njnj = constant i32 777777
@c = global [2 x i32] zeroinitializer
@cc = global [5 x [8 x i32]] zeroinitializer
@dddddd = global i32 0

define i32 @main() #0 {
  %1 = alloca i32
  %2 = alloca i32
  %3 = alloca [9 x i32]
  %4 = alloca [2 x [5 x i32]]
  %5 = alloca [2 x [3 x i32]]
  %6 = alloca [4 x i32]
  %7 = alloca i32
  %8 = alloca i32
  %9 = alloca i32
  %10 = alloca i32
  %11 = alloca i32
  store i32 114514, ptr %11
  store i32 777777, ptr %11
  store i32 777777, ptr %10
  %12 = load i32, ptr %11
  store i32 %12, ptr %10
  store i32 32, ptr %9
  %13 = load i32, ptr %9
  store i32 %13, ptr %8
  %14 = getelementptr [4 x i32], [4 x i32]* %6, i32 0, i32 0
  store i32 33, ptr %14
  %15 = getelementptr [4 x i32], [4 x i32]* %6, i32 0, i32 1
  store i32 44, ptr %15
  %16 = getelementptr [4 x i32], [4 x i32]* %6, i32 0, i32 2
  store i32 55, ptr %16
  %17 = getelementptr [4 x i32], [4 x i32]* %6, i32 0, i32 3
  store i32 66, ptr %17
  %18 = getelementptr [2 x [3 x i32]], [2 x [3 x i32]]* %5, i32 0, i32 0
  %19 = getelementptr [3 x i32], [3 x i32]* %18, i32 0, i32 0
  store i32 22, ptr %19
  %20 = getelementptr [3 x i32], [3 x i32]* %18, i32 0, i32 1
  store i32 33, ptr %20
  %21 = getelementptr [3 x i32], [3 x i32]* %18, i32 0, i32 2
  store i32 44, ptr %21
  %22 = getelementptr [2 x [3 x i32]], [2 x [3 x i32]]* %5, i32 0, i32 1
  %23 = getelementptr [3 x i32], [3 x i32]* %22, i32 0, i32 0
  store i32 77, ptr %23
  %24 = getelementptr [3 x i32], [3 x i32]* %22, i32 0, i32 1
  store i32 88, ptr %24
  %25 = getelementptr [3 x i32], [3 x i32]* %22, i32 0, i32 2
  store i32 99, ptr %25
  %26 = getelementptr [9 x i32], [9 x i32]* %3, i32 0, i32 0
  store i32 114514, ptr %26
  %27 = getelementptr [9 x i32], [9 x i32]* %3, i32 0, i32 2
  %28 = load i32, ptr %9
  store i32 %28, ptr %27
  %29 = getelementptr [2 x [5 x i32]], [2 x [5 x i32]]* %4, i32 0, i32 1, i32 3
  %30 = getelementptr [4 x i32], [4 x i32]* %6, i32 0, i32 2
  %31 = load i32, ptr %30
  store i32 %31, ptr %29
  %32 = getelementptr [2 x [5 x i32]], [2 x [5 x i32]]* %4, i32 0, i32 0, i32 2
  %33 = getelementptr i32, i32* @b, i32 0, i32 2
  %34 = load i32, ptr %33
  store i32 %34, ptr %32
  store i32 1919810, ptr %2
  %35 = load i32, ptr %2
  store i32 %35, ptr %1
  ret i32 0
}

