#!/bin/sh

# Decode the code into /tmp
echo "$CODE" | base64 -d > /tmp/main.c

# Compile: 2>&1 sends compiler errors directly to your WebSocket
gcc /tmp/main.c -o /tmp/main.out 2>&1

if [ $? -ne 0 ]; then
    # Exit if compilation failed (errors already sent to stdout)
    exit 1
fi

# Execute the binary
exec stdbuf -o0 -e0 /tmp/main.out