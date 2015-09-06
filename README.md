# masters-project-submission

## How to install the software

Obtain the files in this repo

```
git clone http://www.github.com/craigfreilly/masters-project-submission
```

by cloning the repo or by some other method.

The java files can then be compiled by running the following commands

```
cd path/to/masters-project-submission/
make
```

This compiles all of the java files in the src/ directory.

## How to run the software

The software currently is just run by running the classes containing main methods on the command line.  Since the project is at an early stage of development this is deemed to be more appropriate than creating JAR files after each change is made to the source code.  Further, creating JAR files for the command line tools makes no sense.

Running each of the tools requires that you are in the src/ directory, so type

```
cd src
```

### Running the GUI

The GUI can be run by typing the following command 

```
java -cp .:../resources/choco-solver-2.1.5.jar gui.KnotGUI 
```

### Running the generation command line interface

The command line interfaces for generation is given with two different constraint programming models and can be run by typing

```
java -cp .:../resources/choco-solver-2.1.5.jar gaussCodeGenerator.NaiveShadowGaussGenerator <number> <option1> <option2> 
```

or

```
java -cp .:../resources/choco-solver-2.1.5.jar gaussCodeGenerator.BinaryModelShadowGaussGenerator <number> <option1> <option2> 
```

### Running the colouring command line interface

### What's included in the resources directory
