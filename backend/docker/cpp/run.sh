#!/bin/sh

echo "$CODE" | base64 -d > /tmp/main.cpp

# Compile: 2>&1 merges stderr into stdout for the terminal to see
g++ /tmp/main.cpp -o /tmp/main.out 2>&1

if [ $? -ne 0 ]; then
    exit 1
fi

exec /tmp/main.out