#!/bin/bash
set -e

echo "Removing old .class files..."
rm -rf Client.class Server.class recTest.txt
echo "Compile Server.java"
javac Server.java
echo "Compile Client.java"
javac Client.java
clear