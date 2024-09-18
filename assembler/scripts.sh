#!/bin/bash

compile() {
    javac *.java    # Compile all .java files in the current directory
}

run0() {    #runs inputTest.txt
    cd ..   #changes to correct directory

    java assembler/assembler test-programs/inputTest.txt    # runs the program
}

run1() {    #runs pdfExample.txt
    cd ..   #changes to correct directory

    java assembler/assembler test-programs/pdfExample.txt    # runs the program
}

run2() {    #runs sampleTest.txt
    cd ..   #changes to correct directory

    java assembler/assembler test-programs/sampleTest.txt    # runs the program
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
    echo "2. run0 (runs inputTest.txt)"
    echo "3. run1 (runs pdfExample.txt)"
    echo "4. run2 (runs sampleTest.txt)"
    echo ""
fi
