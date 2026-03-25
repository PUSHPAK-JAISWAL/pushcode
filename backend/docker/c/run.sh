#!/bin/sh

mkdir -p /tmp/code
cd /tmp/code

printf "%s" "$CODE" > main.c

gcc main.c -o main.out 2> compile_error.txt

if [ $? -ne 0 ]; then
    cat compile_error.txt
    exit 1
fi

exec ./main.out