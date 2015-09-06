package knot;

	/**
	* <h1>Interface with the contract for a Walk iterator</h1>
	*
	* @author  Craig Reilly
	* @version 0.1
	* @since   2015-09-07
	*/

import java.util.Iterator;

public interface Walk extends Iterator
{
	public int getIncomingArcOrient();
}