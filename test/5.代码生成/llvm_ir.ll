declare void @memset(i32*, i32, i32)
declare i32 @printf(i8*, ...)
declare i32 @getint()

@a_c_num_1 = constant i32 1
@a_c_num_2 = constant i32 2
@a_c_num_3 = constant i32 3
@a_c_num_4 = constant i32 4
@a_c_num_5 = constant i32 5
@a_c_num_6 = constant i32 6
@a_v_num = global i32 0
@a_v_num_1 = global i32 1
@a_v_num_2 = global i32 2
@void1876158986void = constant [10 x i8] c"20373260\0A\00"
@void1000423786void = constant [14 x i8] c"a_c_num_1:%d\0A\00"
@void1323512516void = constant [28 x i8] c"a_c_num_2:%d, a_v_num_2:%d\0A\00"

define void @non_param_func() #0 {
  br label %basicBlock1

basicBlock1:
  br label %basicBlock2

basicBlock2:
  ret void
  ret void
}

define i32 @one_param_func(i32 %a) #0 {
  %1 = alloca i32
  %2 = alloca i32
  %3 = alloca i32
  %4 = alloca i32
  store i32 %a, i32* %4
  store i32 1, i32* %2
  store i32 2, i32* %1
  %5 = load i32, i32* %4
  %6 = add i32 %5, 1
  ret i32 %6
}

define i32 @two_param_func(i32 %a1, i32 %a2) #0 {
  %1 = alloca i32
  %2 = alloca i32
  store i32 %a1, i32* %2
  store i32 %a2, i32* %1
  %3 = load i32, i32* %2
  %4 = load i32, i32* %1
  %5 = add i32 %3, %4
  ret i32 %5
}

define i32 @more_than_two_param_func(i32 %a1, i32 %a2, i32 %a3) #0 {
  %1 = alloca i32
  %2 = alloca i32
  %3 = alloca i32
  store i32 %a1, i32* %3
  store i32 %a2, i32* %2
  store i32 %a3, i32* %1
  %4 = load i32, i32* %3
  %5 = load i32, i32* %2
  %6 = add i32 %4, %5
  %7 = load i32, i32* %1
  %8 = sub i32 %6, %7
  ret i32 %8
}

define i32 @main() #0 {
  %1 = alloca i32
  %2 = alloca i32
  %3 = alloca i32
  %4 = alloca i32
  %5 = alloca i32
  %6 = alloca i32
  %7 = alloca i32
  store i32 1, i32* %7
  store i32 3, i32* %4
  store i32 4, i32* %3
  store i32 5, i32* %2
  store i32 2, i32* %5
  br label %basicBlock1

basicBlock1:
  call void @non_param_func()
  %8 = load i32, i32* %4
  %9 = load i32, i32* %3
  %10 = load i32, i32* %2
  %11 = call i32 @more_than_two_param_func(i32 %8, i32 %9, i32 %10)
  br label %basicBlock2

basicBlock2:
  %12 = icmp ne i32 1, 0
  br i1 %12, label %basicBlock3, label %basicBlock16

basicBlock3:
  %13 = load i32, i32* %5
  %14 = add i32 %13, 1
  store i32 %14, i32* %1
  %15 = load i32, i32* %1
  %16 = call i32 @two_param_func(i32 1, i32 %15)
  store i32 %16, i32* %6
  %17 = load i32, i32* %6
  %18 = mul i32 %17, 1
  %19 = load i32, i32* %1
  %20 = mul i32 %19, 1
  %21 = add i32 1, %20
  %22 = icmp eq i32 %18, %21
  br i1 %22, label %basicBlock4, label %basicBlock5

basicBlock4:
  call void @non_param_func()
  br label %basicBlock15

basicBlock5:
  store i32 1, i32* @a_v_num
  br label %basicBlock6

basicBlock6:
  %23 = load i32, i32* @a_v_num
  %24 = icmp sle i32 %23, 3
  br i1 %24, label %basicBlock7, label %basicBlock14

basicBlock7:
  %25 = load i32, i32* @a_v_num
  %26 = call i32 @one_param_func(i32 %25)
  store i32 %26, i32* @a_v_num
  %27 = load i32, i32* @a_v_num
  %28 = icmp eq i32 %27, 3
  %29 = icmp and i1 1, %28
  br i1 %29, label %basicBlock8, label %basicBlock10

basicBlock8:
  br label %basicBlock14

basicBlock9:
  br label %basicBlock10

basicBlock10:
  %30 = icmp eq i32 0, 0
  br i1 %30, label %basicBlock11, label %basicBlock13

basicBlock11:
  br label %basicBlock6

basicBlock12:
  br label %basicBlock13

basicBlock13:
  br label %basicBlock6

basicBlock14:
  br label %basicBlock15

basicBlock15:
  br label %basicBlock16

basicBlock16:
  %31 = call i32 @one_param_func(i32 1)
  %32 = mul i32 1, %31
  %33 = sdiv i32 %32, 4
  %34 = srem i32 %33, -3
  %35 = sub i32 %34, 2
  %36 = add i32 %35, -3
  store i32 %36, i32* %6
  %37 = call i32 @getint()
  store i32 %37, i32* %6
  %38 = call i32 (i8*, ...) @printf(i8* getelementptr([10 x i8], [10 x i8]* @void1876158986void, i32 0, i32 0))
  %39 = call i32 (i8*, ...) @printf(i8* getelementptr([14 x i8], [14 x i8]* @void1000423786void, i32 0, i32 0), i32 1)
  %40 = load i32, i32* @a_v_num_2
  %41 = call i32 (i8*, ...) @printf(i8* getelementptr([28 x i8], [28 x i8]* @void1323512516void, i32 0, i32 0), i32 2, i32 %40)
  ret i32 0
}

