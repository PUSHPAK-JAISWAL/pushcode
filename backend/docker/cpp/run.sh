#!/bin/sh

mkdir -p /tmp/code
cd /tmp/code

printf "%s" "$CODE" > main.cpp

g++ main.cpp -o main.out

exec ./main.out