package knot;

import java.util.*;

public class KnotFromGaussCode
{
	public Knot toKnot(String guassString)
	{
		LinkedList<Integer> gaussList = new LinkedList<Integer>();
		Knot knot; 
		int num = 0;
		int size;

		for (String s: guassString.split("[, ]+"))
		{
			try
            {
                num = Integer.parseInt(s.trim());
            }
            catch(NumberFormatException e) 
            {
                // PrintWriter writer = new PrintWriter(fname, "UTF-8");
                // writer.print("Problem");
                // writer.close();
                // System.exit(0);
            }

			// num = zeroBase(num);

			gaussList.addLast(num);

			// System.out.print("" + num + " ");
 		}

 		knot = new AdjSetKnot();
		size = gaussList.size();

 		for (int i = 0; i < size; i++)
 		{
 			int n = gaussList.get(i);
		}

 		for (int i = 0; i < (size / 2); i++)
 		{
			knot.addCrossing("" + i);
 		}

 		for (int i = 0 ; i < size; i++ ) 
 		{
 			int n = gaussList.get(i);
 			int m;
 			if (i == (size -1))
 			{
 				m = gaussList.get(0);
 			}
 			else
 			{
 				m = gaussList.get(i + 1);
 			}
 			
 			Knot.Crossing source = knot.getByOrderAdded(Math.abs(n));
 			Knot.Crossing target = knot.getByOrderAdded(Math.abs(m));

 			knot.addArc(source, target, orient(n), orient(m));
 		}

 		return knot;

	}

	public int orient(int n)
    {
    	int orient;

    	if (n < 0)
    	{
    		orient = Knot.UNDER;
    	}
    	else
    	{
    		orient = Knot.OVER;
    	}

    	return orient;
    }

}