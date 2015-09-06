package knot;

import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

    /**
    * <h1>An implementation of the knot colouring mod p using Choco</h1>
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */

public class Colourist
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

	private Knot knot; 
	private Model model;  
    private Solver solver;
    private int pColours; //colouring is done mod p
    private int numOfArcs;
    private int numOfCrossings;
    private Knot.Arc[] arcAtPosition;
    private ColouringList colouringPositions; 	
    private IntegerVariable[] arc; //arc[i] is an integer variable with domain [0, p - 1]
    private IntegerVariable[] notAllSame;

    /**
    * The constructor for ColouringList objects. The constructor takes the knot and describes it as a ColouringList
    * @param code the Gauss code of the knot which is to be coloured
    * @param colours the number of colours which it is to be coloured by
    */
    public Colourist(String code, int colours)
    {
        KnotFromGaussCode knotToGC = new KnotFromGaussCode();

    	this.knot = knotToGC.toKnot(code);
    	this.numOfCrossings = knot.size();
    	this.numOfArcs = (this.numOfCrossings) * 2;
    	this.pColours = colours;

    	// create an array of arc objects of length numOfCrossings * 2 , each arc is placed in the array
    	// at the index corresponding to its poistion in the walk.  And example for the trefoil is given below
    	// where the walk starts on the overarc at the top left hand crossing
    	//                           
    	//							---
    	//						 0 /   \
    	//                        /  3  \
    	//                    ---| ----------        
    	//                   /    \     /    \
    	//                   |     \5  /1    |
    	//                    \2    \ /     /
    	//					   \	 /	   /4
    	//						\---/ \---/

    	this.arcAtPosition = new Knot.Arc[numOfArcs];

    	arc = makeIntVarArray("arc ", numOfArcs, 0, pColours - 1);
    	notAllSame = makeIntVarArray("notAllSame", numOfArcs - 1, 0, 1);

    	model = new CPModel();
        solver = new CPSolver();

    }

    /**
    * isColourable() is just a proxy for isColourable(false)
    * @return true if the knot is colourable mod p ,false otherwise
    */
    public boolean isColourable()
    {
        return isColourable(false);
    }

    /**
    * isColourable() is just a proxy for isColourable(false)
    * @param print true if full solution to be reported, false if just success to be reported
    * @return true if the knot is colourable mod p ,false otherwise
    */
    public boolean isColourable(boolean print)
    {
        boolean verbose = print;
    	Knot.WalkIterator walk = knot.walk();

        //counter for how many arcs we've seen
    	int i = 0; 

    	Knot.Crossing crossing;
    	Knot.Crossing target;
    	int crossingNum;
    	int incomingOrient;
    	int targetNum;
    	int targetOrient;
    	Knot.Arc[] outArcs = new Knot.Arc[2]; 
    	colouringPositions = new ColouringList(knot);

    	// get the positions of the arcs in the walk associated with the crossings and add them to the stacks associated
    	// with the crossings in the colouringPosistions
    	while(walk.hasNext())
    	{
    		crossing = (Knot.Crossing) walk.next();
    		outArcs = crossing.getOutArcs();
    		crossingNum = crossing.getOrderAdded();
    		incomingOrient = walk.getIncomingArcOrient();

    		//if the incoming orientation of the ith arc is is over then add i to the over stack for this crossing
    		if (incomingOrient == Knot.OVER)
    		{
    			colouringPositions.pushOver(crossingNum, i);
    		}
    		else // if the incoming orientation is under, then add i to the under stack for this crossing
    		{
    			colouringPositions.pushUnder(crossingNum, i);
    		}

    		// retrieve the target crossing of this arc, the number associated with the target
    		// and the arc's orientation at the target
    		target = outArcs[incomingOrient].getTarget();
    		targetNum = target.getOrderAdded();
    		targetOrient = outArcs[incomingOrient].getTargetOrientation();

    		// if the target orientation of the ith arc is over then add i to the over stack of the target crossing
    		if (targetOrient == Knot.OVER) 
    		{
    			colouringPositions.pushOver(targetNum, i);
    		}
    		else // if the target orientation of the arc is under then add i to the under stack of the target crossing
    		{
    			colouringPositions.pushUnder(targetNum, i);
    		}

    		i++;
     	}

    	////////////////////////////////////////////

     	//retrieve the arcs involved in each crossing, then apply constraints
     	for (int j = 0; j < colouringPositions.size(); j++)
     	{
     		int over1, over2;
     		int under1, under2;

     		over1 = colouringPositions.popOver(j);
     		over2 = colouringPositions.popOver(j);
     		under1 = colouringPositions.popUnder(j);
     		under2 = colouringPositions.popUnder(j);

     		// overarcs at a crossing must take the same value
     		model.addConstraint(eq(arc[over1], arc[over2]));

     		// labels on arcs have to conform at crossings to the equation
     		//		2x - y - z = 0 mod p
     		//
     		// where x is an over crossing and y and z are the undercrossings
     		// WLOG we can choose either overcrossing
     		Constraint negP = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), ((-1) * pColours));
     		Constraint zero = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), 0);
     		Constraint p = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), pColours);
     		model.addConstraint(or(negP, zero, p));
     	}

        ///////////////////////////////////////////

     	// we must also set the constraint that some arc value is not the same as the rest
     	for (int k = 0; k < numOfArcs - 1; k++)
     	{
     		model.addConstraint(ifOnlyIf(eq(arc[k], arc[k + 1]), eq(notAllSame[k], 0)));
     	}

     	model.addConstraint(geq(sum(notAllSame), 1));

     	solver.read(model);

        // if there is a solution, success = true, false otherwise
    	boolean success = solver.solve();

    	int solution = -1;
    	String colour;

        // the colouring only works up to 7 colours, fix this in future
    	if (success && verbose)
    	{
	    	for (int k = 0; k < numOfArcs; k++)
	    	{
	    		solution = solver.getVar(arc[k]).getVal();

	    		switch (solution)
	    		{
	    			case 0: colour = ANSI_RED;
	    					break;
	    			case 1: colour = ANSI_GREEN;
	    					break;
	    			case 2: colour = ANSI_BLUE;
	    					break;
	    			case 3: colour = ANSI_YELLOW;
	    					break;
	    			case 4: colour = ANSI_CYAN;
	    					break;
	    			case 5: colour = ANSI_PURPLE;
	    					break;
	    			default: colour = ANSI_WHITE;
	    					break;
	    		}

				System.out.println(colour + "arc " + k + " colour " + solution + ANSI_RESET);

	    	}
            System.out.println();
    	}

        //feasible -- nodes -- cpu
    	System.out.println("" + solver.isFeasible() + " " + solver.getNodeCount() + " " + solver.getTimeCount());

    	return success;
    }
}

