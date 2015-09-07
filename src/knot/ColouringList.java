package knot;

import java.util.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

    /**
    * <h1>An implementation of the ColouringList abstract data type using an array</h1>
    *
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */

public class ColouringList
{
	private ColouringList.Node[] list;
	private Knot knot;
	private int size;

    /**
    * The constructor for ColouringList objects. The constructor takes the knot and describes it as a ColouringList
    * @param k the knot which is to be represented by the ColouringList object
    */
	public ColouringList(Knot k)
	{
		this.knot = k;
		size = knot.size();
		this.list = new Node[size];

		//add all the crossings from the knot to the list
		addCrossings();
	}

	/**
    * Adds all crossings from the knot into the list
    */
	public void addCrossings()
	{
		Knot.WalkIterator walk = knot.walk();

		while (walk.hasNext())
		{
			Knot.Crossing crossing = (Knot.Crossing) walk.next();

			addCrossing(crossing.getOrderAdded());
		}
	}

	/**
    * Add crossing from the knot into the list, called by addCrossings()
    * @param cross the int which corresponds to the order by which the crossing was added to the knot
    */
	public void addCrossing(int cross)
	{
		boolean alreadyInList = false;
		boolean success = false;

		int crossing;

		for(int i = 0; i < size; i++) 
		{
			if (list[i] == null)
			{
					list[i] = new Node();
			}

			crossing = list[i].getCrossing();

			if (crossing == cross)
			{
				alreadyInList = true;
				i = size;
			}
		}

		int c = cross;

		if (!alreadyInList)
		{
			for (int i = 0; i < size; i++)
			{
				crossing = list[i].getCrossing();

				if (crossing == -1 && !success)
				{
					list[i].setCrossing(c);
					success = true;
				}
			}
		}
	}

	/**
    * Adds under arcs to the ColouringList
    * @param cross the int which corresponds to the order by which the crossing was added to the knot
    * @param walkPos  the position of the arc in the walk
    */
	public void pushUnder(int cross, int walkPos)
	{
		int crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();

			if (cross == crossingFromList)
			{
				list[i].addUnder(walkPos);
				i = size;
			}
		}
	}

	/**
    * Over under arcs to the ColouringList
    * @param cross the int which corresponds to the order by which the crossing was added to the knot
    * @param walkPos  the position of the arc in the walk
    */	
    public void pushOver(int cross, int walkPos)
	{
		int crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();

			if (cross == crossingFromList)
			{
				list[i].addOver(walkPos);
				i = size;
			}
		}
	}

	/**
    * Removes under arcs from the list
    * @param i the int which corresponds to the order by which the crossing was added to the knot
    * @return the position in a walk around the knot which the arc was found 
    */
	public int popUnder(int i)
	{
		if ((i < 0) || i > size)
		{
			throw new NoSuchElementException();
		}

		return list[i].popUnder();
	}

	/**
    * Removes over arcs from the list
    * @param i the int which corresponds to the order by which the crossing was added to the knot
    * @return the position in a walk around the knot which the arc was found 
    */
	public int popOver(int i)
	{
		if ((i < 0) || i > size)
		{
			throw new NoSuchElementException();
		}

		return list[i].popOver();
	}

	/**
    * Getter method for the sice of the ColouringList
    * @return the size of the ColouringList 
    */
	public int size()
	{
		return size;
	}

	private class Node
	{
		private int crossing;
		private Stack<Integer> unders;
		private Stack<Integer> overs;

		public Node()
		{
			this.crossing = -1;
			unders = new Stack<Integer>();
			overs = new Stack<Integer>();
		}

		public void setCrossing(int cross)
		{
			this.crossing = cross;
		}

		public void addUnder(int walkPos)
		{
			unders.push(walkPos);
		}

		public void addOver(int walkPos)
		{
			overs.push(walkPos);
		}

		public int popUnder()
		{
			return unders.pop();
		}

		public int popOver()
		{
			return overs.pop();
		}

		public int getCrossing()
		{
			return this.crossing;
		}
	}
}
