package gaussCodeGenerator;

import java.util.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;

    /**
    * <h1>An implementation of the 01/1 model for Gauss code generation</h1>
    * The BinaryModelShadowGaussGenerator class uses the Choco toolkit to implement the 0/1 model for Gauss code generation. 
    * <p>
    * It can be used as a command line tool by calling the main method as follows:
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */
public class BinaryModelShadowGaussGenerator
{
	private Model model;  
    private Solver solver;
    private int crossings;
    private int twiceCrossings;

    // letter[i][j] is the ith letter in the Gauss code with value j, if letter[i][j] = 1
    private IntegerVariable[][] letter;
    private IntegerVariable[][] letterTranspose;
    private IntegerVariable[] flatLetter;
    private IntegerVariable[][] oddLetter;
    private IntegerVariable[][] evenLetter;
    private IntegerVariable[][] oddLetterTranspose;
    private IntegerVariable[][] evenLetterTranspose;

    private String output = "";
    private Set<List<Integer>> codes = null;

    private final int RANDOM = 0;
    private final int ALL = 1;
    private final int RANDOM_PRIME = 2;  
    private final int ALL_PRIME = 3;

    /**
    * The constructor for NaiveShadowGaussGenerator objects.
    * @param numOfCrossings The number of crossings
    * @param option option = 0 generates a random code, option = 1 generates all codes, option = 2 generates a random prime code, option = 3 generates all prime codes 
    * @param verbose verbose = false reports success, nodes and run time in milliseconds, verbose = true reports this as well as the full solution
    */
    public BinaryModelShadowGaussGenerator(int numOfCrossings, int option, boolean verbose)
    {
    	crossings = numOfCrossings;
    	twiceCrossings = crossings * 2;
    	model = new CPModel();
    	solver = new CPSolver();

    	letter = makeIntVarArray("letter", twiceCrossings, crossings, 0, 1);
    	letterTranspose = new IntegerVariable[crossings][twiceCrossings];
    	flatLetter = new IntegerVariable[crossings * twiceCrossings];

    	oddLetter = new IntegerVariable[crossings][crossings];
    	evenLetter = new IntegerVariable[crossings][crossings];

    	oddLetterTranspose = new IntegerVariable[crossings][crossings];
    	evenLetterTranspose = new IntegerVariable[crossings][crossings];

    	// set up the auxiliary arrays, used just to help make setting constraints easier
    	int k = 0;
    	int indexInEvens = 0;
    	int indexInOdds = 0;

    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		for (int j = 0; j < crossings; j++)
    		{
    			letterTranspose[j][i] = letter[i][j];
    			
    			if (i % 2 == 0)
    			{
    				indexInEvens = i / 2;
    				evenLetter[indexInEvens][j] = letter[i][j];
    				evenLetterTranspose[j][indexInEvens] = letter[i][j];
    			}
    			else
    			{
    				indexInOdds = i / 2;
    				oddLetter[indexInOdds][j] = letter[i][j];
    				oddLetterTranspose[j][indexInOdds] = letter[i][j]; 
    			}

    			flatLetter[k] = letter[i][j];
    			k++;
    		}
    	}

    	// add the constraint that a crossing can only take one value
    	// this corresponds to each row in letter summing to 1
    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		model.addConstraint(eq(sum(letter[i]), constant(1)));
    	}

    	// add the constraint that each crossing number appears twice
    	// this corresponds to each column in letter summing to 2
    	// and thus, each row in letterTranspose summing to 2
    	for (int i = 0; i < crossings; i++)
    	{
    		model.addConstraint(eq(sum(letterTranspose[i]), constant(2)));
    	}

    	// add a constraint that means we start from the first crossing
    	// that is the first crossing takes the value 1
    	// this letter[0][0] = 1;
    	model.addConstraint(eq(letter[0][0], constant(1)));

    	// evenly spaced
    	for (int i = 0; i < crossings; i++)
    	{
    		model.addConstraint(eq(sum(evenLetterTranspose[i]), constant(1)));
    		model.addConstraint(eq(sum(oddLetterTranspose[i]), constant(1)));
    	}

       	// add a constraint that means that the next letter in the code must be one
    	// already used or the smallest of those not yet used. that is
    	//
    	// x_n <= max(x_0, ..., x_(n-1) ) + 1
    	//
    	// for our 0/1 model this means that letterTranspose[i] < letterTranspose[i + 1] 
    	for (int i = 0; i < crossings -1; i++)
    	{
    		model.addConstraint(lex(letterTranspose[i + 1], letterTranspose[i]));
    	}

        // add the symmetry breaking constraint that the second 1 can't come too late
        for (int i = crossings + 1; i < twiceCrossings; i++)
        {
            model.addConstraint(neq(letterTranspose[0][i], 1));
        }

    	// "let the solver see the model" - Paddy McGuinness, Presenter of Take Me Out
    	solver.read(model);

        if (option == RANDOM || option == RANDOM_PRIME)
        {
            // System.out.println("Setting random seed");
            // solver.setVarIntSelector(new RandomIntVarSelector(solver));
            solver.setValIntSelector(new RandomIntValSelector());
            // long number = r.nextLong();

            // RandomIntValSelector rando = new RandomIntValSelector(number);

            // solver.setIntValSelector(rando);
            // solver.set(rando);

        }
        else
        {
            // set flattLetter as the decision variables
            solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(flatLetter)));
        }


        codes = new TreeSet<List<Integer>>(new Comparator<List<Integer>>() 
        {
            public int compare(List<Integer> l1, List<Integer> l2) 
            {
                int sz = l1.size();
                for (int i=0; i<sz; i++) 
                {
                    if (l1.get(i) < l2.get(i)) 
                    {
                        return -1;
                    }
                    if (l1.get(i) > l2.get(i)) 
                    {
                        return 1;
                    }
                }
                return 0;

            }
        });

        // print all solutions
		if (solver.solve().booleanValue())
        {
            do
            {
                // Generate Gauss codes
                int[] gaussCode = new int[numOfCrossings*2];

				for (int i=0; i < twiceCrossings; i++)
				{
			    	for (int j=0; j< crossings;j++)
			    	{
			    		if (solver.getVar(letter[i][j]).getVal() == 1)
			    		{
			    			int codeLetter = j + 1;
                            gaussCode[i] = j + 1; // since j is zero based
			    			j = crossings;
			    		}
			    	}
    			}

                gaussCode = lexRenumber(gaussCode, numOfCrossings);

                DuallyPairedTest dp = new DuallyPairedTest(gaussCode);

                if(dp.isDuallyPaired())
                {
                    if (option == RANDOM_PRIME || option == ALL_PRIME)
                    {
                        if (isPrime(gaussCode))
                        {
                            codes.add(lexMinInClass(gaussCode, numOfCrossings));

                            // if we're generating just one random code we want to break after one if found
                            if (option == RANDOM_PRIME)
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        codes.add(lexMinInClass(gaussCode, numOfCrossings));
                        if (option == RANDOM)
                        {
                            break;
                        }
                    }
                }


                // System.out.print("\n");
            }
            while (solver.nextSolution().booleanValue());
        }

        for (List<Integer> c : codes) 
        {
            output = output + codeToString(c) + "\n";
        }

        if (verbose) 
        {   
            output = output + "feasible: " + solver.isFeasible() + "\n";
            // System.out.println("feasible: " + solver.isFeasible());
            output = output + "nbSol: " + solver.getNbSolutions() + "\n";
            // System.out.println("nbSol: " + solver.getNbSolutions());
            output = output + "nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount() + "\n";
            // System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
            output = output + "Solution count: " + codes.size() + "\n";
            // System.out.println("Solution count: " + codes.size());
        }


            // System.out.println("feasible: " + solver.isFeasible());
            // System.out.println("nbSol: " + solver.getNbSolutions());
            // System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
            // System.out.println("Solution count: " + codes.size());

    }

     /**
    * Getter method for the solution string.
    * @return String the output of the solver
    */
    public String solutionToString()
    {
        return output;
    }


    /**
    * Checks if a shadow Gauss code is prime.
    * @param gaussCode the shadow Gauss code expressed as an array of integers
    * @return boolean true if prime, false otherwise
    */
    public boolean isPrime(int[] gaussCode)
    {
        //for each even subset, check that it contains more entries than half its length
        //if a subset which contains the same number
        for (int i = 0; i < gaussCode.length; i++)
        {
            for (int j = i; j < gaussCode.length; j++)
            {
                // System.out.println("i = " + i + " j = " + j);
                int gap = j - i;
                // otherwise everything is false
                if (!(gap == gaussCode.length) && (gap % 2 == 0) && (gap > 0))
                {
                    int[] temp = Arrays.copyOfRange(gaussCode, i, j);

                    Set<Integer> tempSet = new TreeSet<Integer>();

                    for (int k = 0; k < temp.length; k++)
                    {
                        tempSet.add(temp[k]);
                    } 

                    if (tempSet.size() == (temp.length / 2))
                    {
                        // System.out.println("I'm returning false now");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
    * Renumbers a shadow Gauss code as its S_n minium representative.
    * @param gaussCode the shadow Gauss code expressed as an array of integers
    * @param n the length of the code
    * @return int[] the renumberd shadow Gauss code
    */
    public int[] lexRenumber(int[] gaussCode, int n) 
    {
        int[] retval = new int[n*2];
        int[] newNums = new int[n];
        int maxSoFar = 0;
        
        for (int i=0; i<n*2; i++) 
        {
            if (newNums[gaussCode[i]-1]==0) 
            {
                newNums[gaussCode[i]-1] = ++maxSoFar;
            }
            retval[i] = newNums[gaussCode[i]-1];
        }
        return retval;
    }


    /**
    * Renumbers a shadow Gauss code as its lexicographically minium representative.
    * @param gaussCode a shadow Gauss code expressed as an array of integers
    * @param n the length of the code
    * @return List the lex min shadow Gauss code
    */
    public List<Integer> lexMinInClass(int[] gaussCode, int n) 
    {
        int[] minInClass = gaussCode;
        int[] reversedCode = reversed(gaussCode);
    
        if (lexLt(lexRenumber(reversedCode, n), gaussCode))
        {
            minInClass = lexRenumber(reversedCode, n);
        }

        for (int i=1; i<n*2; i++) 
        {
            int[] rotatedCode = lexRenumber(rotated(gaussCode, i), n); 
            int[] rotatedReversedCode = lexRenumber(rotated(reversedCode, i), n);

            if (lexLt(rotatedCode, minInClass)) 
            {
                minInClass = rotatedCode;
            }

            if (lexLt(rotatedReversedCode, minInClass)) 
            {   
                minInClass = rotatedReversedCode;
            }
        }

        List<Integer> retval = new ArrayList<Integer>();

        for (int i=0; i<n*2; i++) 
        {
            retval.add(minInClass[i]);
        }

        return retval;
    }


    /**
    * Reverses a shadow Gauss code
    * @param arr the shadow Gauss code expressed as an array of integers
    * @return int[] the reversed shadow Gauss code
    */
    public int[] reversed(int[] arr) 
    {
        int len = arr.length;
        int[] retval = new int[len];
    
        for (int i=0; i<len; i++) 
        {
            retval[len-1-i] = arr[i];
        }
        
        return retval;
    }

    /**
    * Cyclically premutes (rotates) a shadow Gauss code
    * @param arr the shadow Gauss code expressed as an array of integers
    * @param rotateBy the number of positions to premute the code by
    * @return int[] the cyclically premuted shadow Gauss code
    */
    public int[] rotated(int[] arr, int rotateBy) 
    {
        int len = arr.length;
        int[] retval = new int[len];
    
        for (int i=0; i<len; i++) 
        {
            retval[(i+rotateBy)%len] = arr[i];
        }

        return retval;
    }

    /**
    * Checks if one shadow Gauss code is lexicographically less than another
    * @param arr1 a shadow Gauss code expressed as an array of integers
    * @param arr2 another shadow Gauss code expressed as an array of integers
    * @return boolean true if the first code is less than the second, false otherwise
    */
    public boolean lexLt(int[] arr1, int[] arr2) 
    {
        int len = arr1.length;

        for (int i=0; i<len; i++) {
            if (arr1[i] < arr2[i]) 
            {
                return true;
            }

            if (arr1[i] > arr2[i]) 
            {
                return false;
            }
        }

        return false;
    }


    /**
    * Prints a shadow Gauss code expressed as an array of integers
    * @param arr a shadow Gauss code expressed as an array of integers
    */
    public void print(int[] arr) 
    {
        for (int i=0; i<arr.length; i++) 
        {
            System.out.print(arr[i] + ", ");
        }

        System.out.println();
    }

    /**
    * Expresses a shadow Gauss code expressed as a list of integers as a string
    * @param list a shadow Gauss code expressed as a list of integers
    * @return String the code expressed as a string
    */    
    public String codeToString(List<Integer> list)
    {
        String s = "";
        for (Integer i : list) 
        {
            s = s + i + ", ";
        }
        return s;
    }

    /**
    * Returns the number of solutions found after the post search filtering
    * @return int the number of solutions found
    */ 
    public int numberOfSolutions()
    {
        return codes.size();
    }

    /**
    * Prints a shadow Gauss code expressed as a list of integers
    * @param list a shadow Gauss code expressed as a list of integers
    */    
    public void print(List<Integer> list) 
    {
        for (Integer i : list) 
        {
            System.out.print(i + ", ");
        }

        System.out.println();
    }

   /**
    * The main method.  This method allows the generation to be run though a command line interface.
    * @param args command line arguements
    */   
    public static void main(String[] args) 
    {
        // the option, choose random, probably not prime by default
        int opt = 0;
        int crossings = 0;

        // boolean flag for verbose output
        boolean v = false;

        if (args.length < 2)
        {
            System.out.println("\nInput to this program is of the form 'java BinaryModelShadowGaussGenerator <number> <option1> <option2>'"
                + "\n \nwhere <number> is the crossing number for the Gauss code(s)"
                + "\n \nwhere <option1> is given as either:"
                + "\n   0 to generate a random code,"
                + "\n   1 to generate all codes,"
                + "\n   2 to generate a random prime codes,"
                + "\n   3 to generate all prime codes."
                + "\n \n<option2> is given as 'verbose' to include information about the solver, or omitted to leave this information out by defult \n");
        }
        else
        {
            crossings = Integer.parseInt(args[0]);

            if (args[1].equals("1"))
            {
                opt = 1;
            }
            else if (args[1].equals("2"))
            {
                opt = 2;
            }
            else if (args[1].equals("3"))
            {
                opt = 3;
            }

            if (args.length > 2)
            {
                v = true;
            }


            BinaryModelShadowGaussGenerator sGG = new BinaryModelShadowGaussGenerator(crossings, opt, v);
            System.out.println(sGG.solutionToString());
        }
    }
}