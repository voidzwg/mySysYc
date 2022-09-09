import sys

HOME = sys.argv[0]
HOME = HOME[:len(HOME) - 10]
# print(HOME)
test_name = "testfile"
c_tail = ".c"
text_tail = ".txt"
n = 6
i = 1

while i <= n:
    name = test_name + str(i)
    in_f = open(HOME + "C_tests\\" + name + c_tail, "r")
    out_f = open(HOME + "files\\" + name + text_tail, "w")
    # print(name + c_tail + ':\n', in_f.read())
    code = ''
    while True:
        s = in_f.readline()
        if s == '':
            print("OK")
            print(code)
            out_f.write(code)
            break
        s_filter = s.split(' ')
        if s_filter[len(s_filter) - 1] == "//test\n":
            j = 0
            while j < len(s_filter):
                ss = s_filter[j]
                if len(ss) > 7:
                    if ss == "fprintf(output,":
                        s_filter[j] = "printf("
                        break
                j += 1
            if j == len(s_filter):
                continue
            code += ' '.join(s_filter)
            continue
        code += s

    # print(name)
    i = i + 1
    in_f.close()
    out_f.close()


