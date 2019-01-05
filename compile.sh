#!/bin/bash
set -e

echo "Removing old .class files..."
rm -rf Client.class Server.class recTest.txt
echo "Compile ServerTest.java"
javac Server.java
echo "Compile ClientTest.java"
javac Client.java
clear