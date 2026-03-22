#!/bin/sh

#create temp working dir
mkdir -p /tmp/code
cd /tmp/code

#write code safely
printf "%s" "$CODE" > main.py

#execute
exec python3 main.py