package gaussCodeGenerator;

import java.util.*;
    
    /**
    * <h1>An implementation of Kauffman's dually paired condition on shadow Gauss codes</h1>
    * The DuallyPairTest class contains methods to create w* for a Gauss code w and the conflicts graph fo w*.
    * It then uses an implemention of a bipartite graph checking algorithm to determine is the conflicts graph
    * of w* is bipartite.  If the conflicts graph ic bipartite then w* is dually paired.
    *
    * @author  Craig Reilly
    * @version 0.1
    * @since   2015-09-07
    */

public class DuallyPairedTest
{
	private int[] gCode;

	/**
    * The constructor for DuallyPairedTest objects.  Thw w* construction is applied withing the constructor
    * @param g a gaussCode expressed as an array of integers
    */
	public DuallyPairedTest(int[] g)
	{
		gCode = new int[g.length];

		for (int i = 0; i < gCode.length; i++)
		{
			gCode[i] = g[i];
		}

		gCode = gcStar(gCode);
	}

	/**
    * Takes a Gauss code w and returns its w* construction
    * @param gc a gaussCode expressed as an array of integers
    * @return the w* construction for the input Gauss code
    */
	public int[] gcStar(int[] gc)
	{
		int[] star = gc;

		// for each letter in gc, change the order of all other letters inbetween its occurences
		// for example, 1, 2, 3, 1, 4, 3, 2, 4 becomes 1, 3, 2, 1, 4, 3, 2, 4 becomes 
		// 1, 3, 2, 3, 4, 1, 2, 4 becomes 1, 3, 4, 1, 2, 3, 2, 4 becomes 1, 3, 4, 2, 3, 2, 1, 4

		int first = -1;
		int second = -1;
		Stack<Integer> stack = new Stack<Integer>();

		// for each letter
		for (int i = 1; i < star.length/2 + 1 ; i++)
		{	
			first = -1;
			second = -1;
			for (int j = 0; j < star.length; j++)
			{
				if (star[j] == i && first == -1)
				{
					first = j;
				}
				else if (star[j] == i && first != -1)
				{
					second = j;
				}
			}
		
			if (!(second - first == 1))
			{
				int start = first + 1;
				for (int j = start; j < second; j++)
				{
					stack.push(star[j]);
				}

				for (int j = start; j < second; j++)
				{
					star[j] = stack.pop();
				}
			}

			stack.clear();
		}
		return star;
	}

	/**
    * Determines if the w* construction of the Gauss code is dually paired.
    * @return true if w* is dually paired, false if not
    */
	public boolean isDuallyPaired()
	{
		int[] star = this.gCode;
		// set up the adj matrix
		int size = star.length/2;
		// if there's an arc between nodes i and j then graph[i][j] = 1, 0 otherwise
		int[][] graph = new int[size][size];

		int PINK = 1;
		int BLUE = 0;
		// colour[i] = 0 means blue, colour[i] = 1 means pink
		int[] colour = new int[size];

		//add the nodes of the graph to a list of not yet visited nodes, to be popped when visited
		ArrayDeque<Integer> notVisited = new ArrayDeque<Integer>(size);
		for (int i = 0 ; i < size; i++ ) 
		{
			//add i to the ith index, since we can't remove an int from an array list because java thinks we're giving it an index 
			notVisited.push((Integer) i);
		}

		int first;
		int second;
		// clear all the colours
		for (int i = 0; i < size; i++)
		{
			colour[i] = -1;
		}

		for (int i = 1; i < size + 1 ; i++)
		{	
			// System.out.print("i = " + i + " ... ");
			first = -1;
			second = -1;
			for (int j = 0; j < size*2; j++)
			{
				if (star[j] == i && first == -1)
				{
					// System.out.println("got in if");
					first = j;
					// System.out.println("first = " + first);
				}
				else if (star[j] == i && first != -1) //star[j] = i after we've set first
				{
					// System.out.println("got in else");
					second = j;
					// System.out.println("second = " + second);

				}
			}
			
			if (!(second - first == 1))
			{
				int start = first + 1;
				int appearancesBetweenLetter[] = new int[size];

				for (int j = start; j < second; j++)
				{
					appearancesBetweenLetter[star[j] -1]++;
				}

				// finally we get round to setting up the adj matrix
				for (int j = 0; j < size; j++)
				{
					if (appearancesBetweenLetter[j] == 1)
					{
						graph[i-1][j] = 1;
						graph[j][i-1] = 1;
					}
				}
			}
		}

		// print the graph
			// for (int i = 0; i < size; i++)
			// {
			// 	for (int j = 0; j < size; j++)
			// 	{
			// 		System.out.print(graph[i][j] + " ");
			// 	}
			// 	System.out.println();
			// }

		// just start from [0][0]
		colour[0] = 0;

		ArrayDeque<Integer> queue = new ArrayDeque<Integer>();

		queue.add(0);

		// for (int i = 0; i < size ; i++)
		// {
		// 	System.out.print(" " + colour[i]);
		// }

		// System.out.println();


		int index = 0;
		while (queue.peek() != null || !notVisited.isEmpty())
		{
			//take things from notVisited if the queue becomes empty before all nodes have been visited
			if (queue.peek() == null)
			{
				queue.add(notVisited.removeFirst());
			}
			Integer u = queue.removeFirst();

			notVisited.remove(u);
		// 	// System.out.println("u = " + u);


			for (int j = 0; j < size; j++)
			{
				if (graph[u][j] == 1 && colour[j] == -1)
				{
					// System.out.println("got in big if");
					// System.out.println("colour u = " + colour[u]);
					if (colour[u] == 0)
					{
						colour[j] = 1;
					}
					else if (colour[u] == 1)
					{
						colour[j] = 0;
						queue.add(j);
					}
					else //we get in here if the opening choice isn't connected
					{
						colour[j] = 0;
						// System.out.println("Something has gone terribly wrong");
					}
				}
				else if (graph[u][j] == 1 && colour[j] == colour[u])
				{
					// System.out.println(" j = " + j + " colour " + colour[j]);
					return false;
				}
			}
		}

		// isBipartite(graph,0, size);
			
		// if we got through all that then things are cooooooel
		return true;
	}

// 	public boolean isBipartite(int adjacencyMatrix[][], int source, int size)
//     {

//     	Queue<Integer> queue = new LinkedList<Integer>();
// ;
//     	int numberOfVertices = size -1;
	    
// 		int NO_COLOR = 0;
// 	    int RED = 1;
// 	    int BLUE = 2;

//         int[] colored = new int[numberOfVertices +  1];
//         for (int vertex = 1; vertex <= numberOfVertices; vertex++)
//         {
//             colored[vertex] = NO_COLOR;
//         }
//         colored[source] = RED;
//         queue.add(source);
 
//         int element, neighbour;
//         while (!queue.isEmpty())
//         {
//             element = queue.remove();
//             neighbour = 1;
//             while (neighbour <= numberOfVertices)
//             { 
//                 if (adjacencyMatrix[element][neighbour] == 1 && colored[element]== colored[neighbour])
//                 {
//                     return false;
//                 }
//                 if (adjacencyMatrix[element][neighbour] == 1 && colored[neighbour]== NO_COLOR)
//                 {
//                     colored[neighbour] = (colored[element] == RED ) ? BLUE :RED;
//                     queue.add(neighbour);
//                 }
//                 neighbour++;
//             }
//         }
//         return true;
//     }
}




	// public static void main(String[] args) 
	// {
	// 	// james' test case that is paired
	// 	// int[] thing = new int[] {1, 1, 2, 2, 3, 4, 5, 3, 4, 6, 6, 5};
	// 	//something that isn't paired
	// 	// int[] thing = new int[] {1, 3, 4, 2, 1, 5, 2, 3, 5, 4};
	// 	int[] thing = new int[] {1, 2, 3, 1, 4, 3, 2, 4};
	// 	int[] wStar = gcStar(thing);
	// 	if (isDuallyPaired(wStar))
	// 	{
	// 		System.out.println("dually paired");
	// 	}
	// 	else
	// 	{
	// 		System.out.println("not dualy paired");
	// 	}
	// }