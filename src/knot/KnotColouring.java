package knot;

import java.util.*;
import java.io.*;


    /**
    * <h1>The command line interface for knot colouring</h1>
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */

public class KnotColouring
{
    private static boolean verbose = false;
	private Scanner sc;
	
    /**
    * The constructor for KnotColouring objects. 
    * @param fname a file name (which should be a text file containing Gauss code(s) 
    * @param modP the number of colours which it is to be coloured by
    * @param verbose true if full solutions are to be printed, false if just success 
    */
	public KnotColouring(String fname, int modP, boolean verbose) throws IOException 
	{
		sc = new Scanner(new File(fname));

		String guassString;

		while (sc.hasNextLine())
		{
			guassString = sc.nextLine();

     		Colourist colourist = new Colourist(guassString, modP);

     		colourist.isColourable(verbose);
    	}
	}

	 /**
    * The constructor for KnotColouring objects. 
    * @param fname a file name (which should be a text file containing Gauss code(s) 
    * @param modP the number of colours which it is to be coloured by
    * @param verbose true if full solutions are to be printed, false if just success 
    */
	public static void main(String[] args) throws IOException
	{
        boolean verbose = false;

        if (args.length < 2)
        {
            System.out.println("\nInput to this program is of the form 'java KnotColouring <file> <number> <option1>'"
                + "\n \nwhere <file> is the name of a text file containing Gauss code(s)"
                + "\n \nwhere <number> is the number by which the Gauss code(s) should be coloured"
                + "\n \n<option1> is given as 'verbose' to include information about the solution, or omitted to leave this information out by defult \n");
        }
        else
        {
	        if (args.length > 2)
	        {
	            verbose = true;
	        }

			KnotColouring catk = new KnotColouring(args[0], Integer.parseInt(args[1]), verbose);
		}
    }

}
