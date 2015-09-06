package knot;

import java.util.*;

    /**
    * <h1>From Gauss codes to Knot objects</h1>
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */

public class KnotFromGaussCode
{
    /**
    * Creates a knot object, from an input Gauss code
    * @param gaussString a Gauss code
    * @return the Knot represented by the Gauss code
    */
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
            }

			gaussList.addLast(num);
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

        gaussList.clear();

 		return knot;

	}

    /**
    * Returns the orientation of an arc, given a letter from the Gauss code
    * @param n a letter from the Gauss code
    * @return Knot.Over if n > 0, Knot.Under otherwise
    */
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