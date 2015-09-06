# masters-project-submission

This software was created by Craig Reilly, as part of his third semester project in fulfillment of his Masters in Software Development at the University of Glasgow.  The software makes use of the existing Choco constraint programming toolkit, and the KnotTheory` Mathematica package.  Both are included in this repo for ease of installation.  Use of the drawing tools in the require Mathematica to be installed on the user's system.  The software was tested on Ubuntu 12.10 and 14.10 and 15.04, but should run on any UNIX system.  It will not run without modification on Windows.

The purpose of the software was to automatically generate knots.  The approach taken is to generate Gauss codes which represent classical knots (and to only generate one Gauss code per equivalence class of Gauss codes).

The software also includes an constraint programming implementation of knot colourability mod p, as an exploration into using constraint programming in determining knot invariants.

## How to install the software

Obtain the files in this repo by cloning the repo

```
git clone http://www.github.com/craigfreilly/masters-project-submission
```

or by some other method.

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
###Using the GUI

The GUI allows access to much of the functionality of the command line tools (described below).  

The majority of the GUI is split into two panes, one for drawing and the for colouring.  The use of these aspects of the GUI is self evident.

The Generation menu allows access to the generation tool, and clicking on the options in the menu will bring up a dialog box asking how many crossings the Gauss code is to be generated for.  If one code is requested, the generated shadow Gauss code is then loaded into the Gauss code text field.  This code can be given a sign sequence by hand.  If all codes are requested then the output is written to a file.

The File menu allows for a Gauss code included in a file to be loaded into the GUI.

The Save menu allows for the Gauss code in the Gauss code text field to be save, and for the picture displayed by the GUI to be saved.

### Running and using the generation command line interface

The command line interfaces for generation is given with two different constraint programming models and can be run by typing

```
java -cp .:../resources/choco-solver-2.1.5.jar gaussCodeGenerator.NaiveShadowGaussGenerator <number> <option1> <option2> 
```

or

```
java -cp .:../resources/choco-solver-2.1.5.jar gaussCodeGenerator.BinaryModelShadowGaussGenerator <number> <option1> <option2> 
```

where number is the crossing number for the Gauss code(s)
 
where option1 is given as either:
   0 to generate a random code,
   1 to generate all codes,
   2 to generate a random prime codes,
   3 to generate all prime codes.
 
option2 is given as 'verbose' to include information about the solver, or omitted to leave this information out by default

####An example
Running

```
java -cp .:../resources/choco-solver-2.1.5.jar gaussCodeGenerator.BinaryModelShadowGaussGenerator 3 3 verbose 
```

gives the following output

```
1, 2, 3, 1, 2, 3, 
feasible: true
nbSol: 4
nodes: 9   cpu: 7
Solution count: 1
```

Where the Gauss code is give, the problem is determined to have solutions, the number of solutions before the post search filtering is 4, the number of nodes used by search is 9, the runtime is 7ms and the number of solutions after the post search filtering is 1.

### Running the colouring command line interface

The command line colouring interface is run by typing 

```
java -cp .:../resources/choco-solver-2.1.5.jar knot.KnotColouring <file> <number>
```

where file is a file containing one or more Gauss codes, each on its own line (with no trailling empty lines).  Such files can be found in resources/Rolfsen-table/ and resources/R-H-T-table/ 

where number is the number (which should be prime) by which the knot(s) in the file at to be coloured.

####An example

Running

```
java -cp .:../resources/choco-solver-2.1.5.jar knot.KnotColouring ../resources/Rolfsen-table/3_1.txt 3 verbose
```

gives as output 

```
arc 0 colour 1
arc 1 colour 1
arc 2 colour 0
arc 3 colour 0
arc 4 colour 2
arc 5 colour 2

true 3 23
```

Where arc 0 is the arc which leaves crossing 1 with over orientation.  So in the Gauss code the colouring starts from the positive 1.  True says that the colouring is feasible, 3 denotes that it took 3 nodes during search, and 23 dentores that it took 23ms to complete search.

### What's included in the resources directory

The resources directory contains the JAR files needed by the software.  These are 

```
choco-solver-2.1.5.jar
hamcrest-core-1.3.jar  
junit-4.12.jar
```

The resources directory also contains the Rolfsen and Hoste-Thistlethwaite tables.  Rolfsen-table/ contains txt files for each knot in the table, the contents of which are the knot represented as a Gauss code.  R-H-T-table/ contains similar files, but this time for all knots up to 11 crossings.

Finally, the resources directory contains a directory called temp/ which is used to house temporary resources required by the GUI.
