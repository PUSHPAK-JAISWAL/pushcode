#!/bin/sh

echo "$CODE" | base64 -d > /tmp/Main.java

# Compile: 2>&1 captures javac errors like "missing semicolon"
javac /tmp/Main.java 2>&1

if [ $? -ne 0 ]; then
    exit 1
fi

# Run: -cp /tmp is required because the compiled .class is in /tmp
exec java -cp /tmp Main