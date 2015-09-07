package gaussCodeGenerator;

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
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;

    /**
    * <h1>An implementation of the naive model for Gauss code generation</h1>
    * The NaiveShadowGaussGenerator class uses the Choco toolkit to implement the naive model for Gauss code generation. 
    * <p>
    * It can be used as a command line tool by calling the main method as follows:
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */
public class NaiveShadowGaussGenerator
{
    private Model model;  
    private Solver solver;
    private int numOfCrossings;
    private IntegerVariable[] letter; //letter[i] is an letter in the Gauss code with value [0, numOfCrossings - 1]
    private IntegerVariable[] notAllSame;
    private final int RANDOM = 0;
    private final int ALL = 1;
    private final int RANDOM_PRIME = 2;  
    private final int ALL_PRIME = 3;
    private String output = "";
    private Set<List<Integer>> codes;

    /**
    * The constructor for NaiveShadowGaussGenerator objects.
    * @param crossings The number of crossings
    * @param option option = 0 generates a random code, option = 1 generates all codes, option = 2 generates a random prime code, option = 3 generates all prime codes 
    * @param verbose verbose = false reports success, nodes and run time in milliseconds, verbose = true reports this as well as the full solution
    */
    public NaiveShadowGaussGenerator(int crossings, int option, boolean verbose)
    {
        model = new CPModel();
        solver = new CPSolver();

        numOfCrossings = crossings;

        letter = makeIntVarArray("arc ", (2 * numOfCrossings), 1, numOfCrossings);

        // add the constraint that each letter can only appear twice
        for (int k = 1; k <= numOfCrossings; k++)
        {
            model.addConstraint(occurrence(2, letter, k));
        }

        // add a constraint that sets the first letter to be 1
        model.addConstraint(eq(letter[0], 1));

        // add a constraint that does evenly spaced, to do this add the constraint that 
        //  
        //      x[i] = k with i odd implies that x[j] != k with j odd, similarly for even
        //
        // with the added constraint that the second 1 can't appear after the nth position in the code
        // any code with a 1 later then the nth position isn't the lexicographically minimum code 
        // representing the equivelence class of codes to which it belongs
        for (int i = 0; i < (2 * numOfCrossings); i++)
        {
            for (int j = i; j < (2 * numOfCrossings); j ++)
            {
                // set up both above constraints for 1
                if (i == 0)
                {
                    if (j % 2 == 0 && j != i && j <= numOfCrossings)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                    else if (j > numOfCrossings)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else if (i % 2 == 0 && i != 0)
                {
                    if (j % 2 == 0 && j != i)
                    {   
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else
                {
                    if (j % 2 == 1 && j != i)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
            }    
        }

        ////////////////////////////// IMPOSE LEXIMIN ORDERING!!! //////////////////////////////

        //each number has to be already given or the least of those that aren't
        //
        //      x_n <= max(x_0, ... , x_n-1) + 1
        //

        IntegerVariable[] maxSoFar = makeIntVarArray("maxSoFar", ((2 * numOfCrossings) - 1), 0, numOfCrossings, "cp:no_decision");

        for (int i = 1; i < (2 * numOfCrossings - 1); i ++)
        {
            model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
            model.addConstraint(leq(letter[i], plus(maxSoFar[i], constant(1))));
        }

        solver.read(model);

        if (option == RANDOM || option == RANDOM_PRIME)
        {
            solver.setValIntSelector(new RandomIntValSelector());
        }

        solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(letter)));


        // set up a set into which we'll add the lexicographically minimum Gauss codes
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

        // actually solve stuff!

        // if a first solution exists (it does)
        if (solver.solve().booleanValue())
        {
            do
            {
                // Generate Gauss codes
                int[] gaussCode = new int[2*numOfCrossings];

                for (int i = 0; i < (2*numOfCrossings); i++)
                {
                    gaussCode[i] = solver.getVar(letter[i]).getVal();
                }

                // submit the gauss code to the dually paired testing class
                DuallyPairedTest dp = new DuallyPairedTest(gaussCode);

                // only consider the lexmin reordering of each code
                gaussCode = lexRenumber(gaussCode, numOfCrossings);

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
            }
            while (solver.nextSolution().booleanValue());
        }

        // add each Gauss code to the output string, on its own line
        for (List<Integer> c : codes) 
        {
            output = output + codeToString(c) + "\n";
        }


        if (verbose) 
        {   
            output = output + "feasible: " + solver.isFeasible() + "\n";
            output = output + "nbSol: " + solver.getNbSolutions() + "\n";
            output = output + "nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount() + "\n";
            output = output + "Solution count: " + numberOfSolutions() + "\n";
        }
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
            System.out.println("\nInput to this program is of the form 'java NaiveShadowGaussGenerator <number> <option1> <option2>'"
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


            NaiveShadowGaussGenerator sGG = new NaiveShadowGaussGenerator(crossings, opt, v);
            System.out.println(sGG.solutionToString());
        }
    }
}

