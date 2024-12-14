# Text Tool README
=====================

## Overview
-----------

This Java application is a text manipulation tool that takes a file as input and performs various operations on its contents based on command-line arguments. The tool supports several operations, including:

* Replacing text
* Prefixing lines with a string
* Encoding text using a Caesar cipher
* Duplicating lines
* Outputting the modified text to a file or the console

## Command-Line Arguments
-------------------------

The application accepts the following command-line arguments:

* `-f`: Overwrite the input file with the modified text
* `-o output_file_name`: Output the modified text to a file
* `-i`: Perform case-insensitive text replacement
* `-r old new`: Replace `old` with `new` in the text
* `-p prefix`: Prefix each line with `prefix`
* `-c n`: Encode the text using a Caesar cipher with shift `n`
* `-d n`: Duplicate each line `n` times

## Error Handling
-----------------

The application performs error checking on the input file and command-line arguments. If any errors are detected, it prints an error message and exits. The error checks include:

* Checking if the input file exists
* Verifying that the command-line arguments are valid and consistent
* Ensuring that the input file ends with a newline character

## Operations
--------------

The application performs the following operations on the input text:

* `replace`: Replaces `old` with `new` in the text, optionally performing a case-insensitive search
* `prefix`: Prefixes each line with `prefix`
* `encode`: Encodes the text using a Caesar cipher with shift `n`
* `duplicateLines`: Duplicates each line `n` times

## Example Usage
----------------

Here are some examples of how to use the application:

* `java Main -r old new input.txt`: Replace `old` with `new` in `input.txt` and output the modified text to the console
* `java Main -o output.txt -p prefix input.txt`: Prefix each line with `prefix` and output the modified text to `output.txt`
* `java Main -c 3 input.txt`: Encode the text in `input.txt` using a Caesar cipher with shift 3 and output the modified text to the console
* `java Main -d 2 input.txt`: Duplicate each line in `input.txt` twice and output the modified text to the console

## Compiling and Running
-------------------------

To compile and run the application, follow these steps:

1. Compile the `Main.java` file using `javac Main.java`
2. Run the application using `java Main` followed by the desired command-line arguments

## Notes
-------

* The application assumes that the input file exists and is readable.
* The application uses the `java.nio` package for file I/O operations.
* The application uses the `java.util` package for string manipulation and regular expression matching.