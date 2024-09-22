#!/bin/bash

compile() {
    rm -rf out/*
    #javac -d out assembler/*.java    # Compile all .java files in the current directory
    javac -d out -sourcepath . assembler/*.java
    jar cfm jar/assembler.jar MANIFEST.MF -C out .
}

run0() {    # changes the input file and runs inputTest.txt
    cp "test-programs/inputTest.txt" "jar/input.txt"
    cd jar
    java -jar assembler.jar
}

run1() {    # changes the input file and runs pdfExample.txt
    cp "test-programs/pdfExample.txt" "jar/input.txt"
    cd jar
    java -jar assembler.jar
}

run2() {    # changes the input file and runs sampleTest.txt
    cp "test-programs/sampleTest.txt" "jar/input.txt"
    cd jar
    java -jar assembler.jar
}



# Main script execution
if [ "$1" == "compile" ]; then
    compile
elif [ "$1" == "run0" ]; then
    run0
elif [ "$1" == "run1" ]; then
    run1
elif [ "$1" == "run2" ]; then
    run2
else
    echo ""
    echo "use the format \"./scripts.sh <option>\" to run scripts"
    echo ""
    echo "options:"
    echo "1. compile"
    echo "1. run0 (runs inputTest.txt)"
    echo "2. run1 (runs pdfExample.txt)"
    echo "3. run2 (runs sampleTest.txt)"
    echo ""
fi
