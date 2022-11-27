; ModuleID = '.\testfile.c'
source_filename = ".\\testfile.c"
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.29.30038"

$"??_C@_03PMGGPEJJ@?$CFd?6?$AA@" = comdat any

$"??_C@_06CIFJMGCF@a?5?$DO?50?6?$AA@" = comdat any

$"??_C@_07BNJOKAOK@a?5?$DM?$DN?50?6?$AA@" = comdat any

$"??_C@_06LFANMJDA@?6?6?6?6?6?6?$AA@" = comdat any

@_ = dso_local constant i32 20373117, align 4
@value1 = dso_local constant i32 1, align 4
@value10 = dso_local global i32 10, align 4
@value11 = dso_local global i32 11, align 4
@value12 = dso_local global i32 12, align 4
@value13 = dso_local global i32 0, align 4
@"??_C@_03PMGGPEJJ@?$CFd?6?$AA@" = linkonce_odr dso_local unnamed_addr constant [4 x i8] c"%d\0A\00", comdat, align 1
@"??_C@_06CIFJMGCF@a?5?$DO?50?6?$AA@" = linkonce_odr dso_local unnamed_addr constant [7 x i8] c"a > 0\0A\00", comdat, align 1
@"??_C@_07BNJOKAOK@a?5?$DM?$DN?50?6?$AA@" = linkonce_odr dso_local unnamed_addr constant [8 x i8] c"a <= 0\0A\00", comdat, align 1
@"??_C@_06LFANMJDA@?6?6?6?6?6?6?$AA@" = linkonce_odr dso_local unnamed_addr constant [7 x i8] c"\0A\0A\0A\0A\0A\0A\00", comdat, align 1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca i32, align 4
  store i32 0, ptr %1, align 4
  store i32 13, ptr @value13, align 4
  store i32 14, ptr %2, align 4
  store i32 15, ptr %3, align 4
  %8 = call i32 (ptr, ...) @printf(ptr noundef @"??_C@_03PMGGPEJJ@?$CFd?6?$AA@", i32 noundef 20373117)
  %9 = call i32 @getint()
  store i32 %9, ptr %6, align 4
  %10 = load i32, ptr %6, align 4
  %11 = icmp sgt i32 %10, 0
  br i1 %11, label %12, label %14

12:                                               ; preds = %0
  %13 = call i32 (ptr, ...) @printf(ptr noundef @"??_C@_06CIFJMGCF@a?5?$DO?50?6?$AA@")
  br label %16

14:                                               ; preds = %0
  %15 = call i32 (ptr, ...) @printf(ptr noundef @"??_C@_07BNJOKAOK@a?5?$DM?$DN?50?6?$AA@")
  br label %16

16:                                               ; preds = %14, %12
  store i32 3, ptr %7, align 4
  %17 = call i32 (ptr, ...) @printf(ptr noundef @"??_C@_06LFANMJDA@?6?6?6?6?6?6?$AA@")
  ret i32 0
}

declare dso_local i32 @printf(ptr noundef, ...) #1

declare dso_local i32 @getint(...) #1

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="none" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { "frame-pointer"="none" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }

!llvm.module.flags = !{!0, !1, !2}
!llvm.ident = !{!3}

!0 = !{i32 1, !"wchar_size", i32 2}
!1 = !{i32 7, !"PIC Level", i32 2}
!2 = !{i32 7, !"uwtable", i32 2}
!3 = !{!"clang version 15.0.2"}
