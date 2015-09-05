JFLAGS = -g 
SP = -sourcepath src
CP = -cp .:resources/choco-solver-2.1.5.jar:resources/hamcrest-core-1.3.jar:resources/junit-4.12.jar:src/knot/:src/gui/:src/gaussCodeGenerator/
#CHOCOFLAG = -cp :.
#TESTFLAG = -cp /home/craig/.jcommon-1.0.23.jar:/home/craig/.hamcrest-core-1.3.jar:/home/craig/.junit-4.12.jar:.
JC = javac
JVM = java
SHELL := /bin/bash

.SUFFIXES: .java .class
.java.class:
	$(JC) $(SP) $(JFLAGS) $(CP) $*.java

CLASSES = \
	src/gaussCodeGenerator/DuallyPairedTest.java \
	src/gaussCodeGenerator/NaiveShadowGaussGenerator.java  \
	src/gaussCodeGenerator/BinaryModelShadowGaussGenerator.java \
	src/knot/Walk.java \
	src/knot/Knot.java \
	src/knot/AdjSetKnot.java \
	src/knot/ColouringList.java \
	src/knot/Colourist.java \
	src/knot/KnotColouring.java \
	src/gui/MathematicaAdapter.java \
	src/gui/KnotGUI.java
	# src/gaussCodeGenerator/DuallyPairTest.java \
	# src/gaussCodeGenerator/PrimeOutputTest.java \
	# src/gaussCodeGenerator/TestRunner.java \


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) src/gui/*.class
	$(RM) src/gaussCodeGenerator/*.class
	$(RM) src/knot/*.class

	$(RM) src/test/tests.txt

tests.txt:
	$(RM) tests.txt
	$(JVM) $(CP) TestRunner > tests.txt	
	cat tests.txt

test: tests.txt