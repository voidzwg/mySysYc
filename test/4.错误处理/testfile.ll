; ModuleID = '.\testfile.c'
source_filename = ".\\testfile.c"
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.29.30038"

@a = dso_local constant [3 x i32] [i32 1, i32 2, i32 33], align 4
@aa = dso_local constant [3 x [2 x i32]] [[2 x i32] [i32 99, i32 88], [2 x i32] [i32 77, i32 66], [2 x i32] [i32 55, i32 44]], align 16
@bb = dso_local global [2 x [3 x i32]] [[3 x i32] [i32 3, i32 6, i32 8], [3 x i32] [i32 7, i32 6, i32 -1]], align 16
@b = dso_local global [5 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5], align 16
@ccc = dso_local constant i32 114514, align 4
@njnj = dso_local constant i32 777777, align 4
@__const.main.dxdx = private unnamed_addr constant [4 x i32] [i32 33, i32 44, i32 55, i32 66], align 16
@__const.main.xxx = private unnamed_addr constant [2 x [3 x i32]] [[3 x i32] [i32 22, i32 33, i32 44], [3 x i32] [i32 77, i32 88, i32 99]], align 16
@c = dso_local global [2 x i32] zeroinitializer, align 4
@cc = dso_local global [5 x [8 x i32]] zeroinitializer, align 16
@dddddd = dso_local global i32 0, align 4

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca [4 x i32], align 16
  %8 = alloca [2 x [3 x i32]], align 16
  %9 = alloca [2 x [5 x i32]], align 16
  %10 = alloca [9 x i32], align 16
  %11 = alloca i32, align 4
  %12 = alloca i32, align 4
  store i32 0, ptr %1, align 4
  store i32 114514, ptr %2, align 4
  store i32 777777, ptr %2, align 4
  store i32 777777, ptr %3, align 4
  %13 = load i32, ptr %2, align 4
  store i32 %13, ptr %3, align 4
  store i32 32, ptr %4, align 4
  %14 = load i32, ptr %4, align 4
  store i32 %14, ptr %5, align 4
  call void @llvm.memcpy.p0.p0.i64(ptr align 16 %7, ptr align 16 @__const.main.dxdx, i64 16, i1 false)
  call void @llvm.memcpy.p0.p0.i64(ptr align 16 %8, ptr align 16 @__const.main.xxx, i64 24, i1 false)
  %15 = getelementptr inbounds [9 x i32], ptr %10, i64 0, i64 0
  store i32 114514, ptr %15, align 16
  %16 = load i32, ptr %4, align 4
  %17 = getelementptr inbounds [9 x i32], ptr %10, i64 0, i64 2
  store i32 %16, ptr %17, align 8
  %18 = getelementptr inbounds [4 x i32], ptr %7, i64 0, i64 2
  %19 = load i32, ptr %18, align 8
  %20 = getelementptr inbounds [2 x [5 x i32]], ptr %9, i64 0, i64 1
  %21 = getelementptr inbounds [5 x i32], ptr %20, i64 0, i64 3
  store i32 %19, ptr %21, align 4
  %22 = load i32, ptr getelementptr inbounds ([5 x i32], ptr @b, i64 0, i64 2), align 8
  %23 = getelementptr inbounds [2 x [5 x i32]], ptr %9, i64 0, i64 0
  %24 = getelementptr inbounds [5 x i32], ptr %23, i64 0, i64 2
  store i32 %22, ptr %24, align 8
  store i32 1919810, ptr %11, align 4
  store i32 1919810, ptr %12, align 4
  ret i32 0
}

; Function Attrs: argmemonly nocallback nofree nounwind willreturn
declare void @llvm.memcpy.p0.p0.i64(ptr noalias nocapture writeonly, ptr noalias nocapture readonly, i64, i1 immarg) #1

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="none" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { argmemonly nocallback nofree nounwind willreturn }

!llvm.module.flags = !{!0, !1, !2}
!llvm.ident = !{!3}

!0 = !{i32 1, !"wchar_size", i32 2}
!1 = !{i32 7, !"PIC Level", i32 2}
!2 = !{i32 7, !"uwtable", i32 2}
!3 = !{!"clang version 15.0.2"}
