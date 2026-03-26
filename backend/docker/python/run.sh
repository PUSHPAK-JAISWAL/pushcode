#!/bin/sh

echo "$CODE" | base64 -d > /tmp/main.py

# -u forces stdin, stdout, and stderr to be totally unbuffered
exec python3 -u /tmp/main.py