package knot;

import java.util.*;
import java.io.*;

// The following java program runs an experiement to determine which knots, up to 10 crossings
// are colourable with 3, 5 and 7 colours.
// Each knot's Gauss code is read in from a file, where it is in the form
//
// -1, 3, -2, 1, -3, 2
//
// -- this being the trefoil.
//
// The cpu time taken and node count for each colouring for each knot is also given, regardless of success.

public class KnotColouring
{
    private static boolean verbose = false;
	private Scanner sc;
	
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

	
	public static void main(String[] args) throws IOException
	{
        boolean verbose = false;

        if (args.length > 2)
        {
            verbose = true;
        }

		KnotColouring catk = new KnotColouring(args[0], Integer.parseInt(args[1]), verbose);
    }

}
