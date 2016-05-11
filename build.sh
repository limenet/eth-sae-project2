#!/bin/bash

JAVA_HOME=/opt/java-latest
APRON_HOME=/home/sae/apron/japron

base=$(pwd)
export CLASSPATH=.:$base/soot-2.5.0.jar:$APRON_HOME/apron.jar:$APRON_HOME/gmp.jar
export LD_LIBRARY_PATH=$base/

mkdir -p bin
$JAVA_HOME/bin/javac -d bin src/*.java
$JAVA_HOME/bin/javac -d bin src/ch/ethz/sae/*.java




