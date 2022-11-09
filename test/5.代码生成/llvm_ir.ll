declare void @memset(i32*, i32, i32)
declare i32 @printf(i8*, ...)
declare i32 @getint()

@_ = constant i32 20373117
@value1 = constant i32 1
@value10 = global i32 10
@value11 = global i32 11
@value12 = global i32 12
@value13 = global i32 0
@void1295765102void = constant [4 x i8] c"%d\0A\00"
@void-235057988void = constant [7 x i8] c"\0A\0A\0A\0A\0A\0A\00"

define i32 @main() #0 {
  %1 = alloca i32
  %2 = alloca i32
  %3 = alloca i32
  %4 = alloca i32
  %5 = alloca i32
  store i32 13, i32* @value13
  store i32 14, i32* %5
  store i32 15, i32* %4
  %6 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @void1295765102void, i32 0, i32 0), i32 20373117)
  store i32 3, i32* %1
  %7 = call i32 (i8*, ...) @printf(i8* getelementptr([7 x i8], [7 x i8]* @void-235057988void, i32 0, i32 0))
  ret i32 0
}

