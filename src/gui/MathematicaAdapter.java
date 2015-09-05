package gui;

import java.io.*;
import java.util.*;

	/**
	* <h1>An adaptor to acces the KnotTheory` package from within java!</h1>
	* The MathematicaAdapter class makes avaliable functionality from 
	* the KnotTheory` Mathematica library.
	* <p>
	* <b>Note:</b> since the methods in this class call Mathematica scripts
	* they can take quite some time to run in some cases.
	*
	* @author  Craig Reilly
	* @version 0.1
	* @since   2015-08-08
	*/

public class MathematicaAdapter
{
	private LinkedList<Integer> gaussList = new LinkedList<Integer>();
	private PrintWriter writer = null;


	/**
	* This method takes a Gauss code representation of a knot
	* and draws the corresponding planar diagram and saves it as a jpg.
	* The diagram is saved to a file.
	* @param gaussCode is Gauss code representation of a knot in the form "-1, 2, -3, 1, -2, 3"
	* @exception IOException on jpg file save, 
	* @exception InterruptedException thrown if the mathematica script is interrupted
	*/
	public void drawPlanarDiagram(String gaussCode) throws IOException, InterruptedException
	{
		gaussList = new LinkedList<Integer>();
			
		for (String s: gaussCode.split("[, ]+"))
		{
			int num = Integer.parseInt(s);

			gaussList.addLast(num);
 		}

     	int size = gaussList.size();

     	writer = new PrintWriter("../resources/temp/tempPlanar.txt");

    	String s;

     	for (int i = 0; i < size; i++)
     	{
     		s = "" + gaussList.get(i); 
     		writer.write(s + " ");
     	}

     	writer.close();

     	Runtime r = Runtime.getRuntime();
		Process p = r.exec("./planarDraw.m ../resources/temp/tempPlanar.txt");

		p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";

		while ((line = b.readLine()) != null) {
		  System.out.println(line);
		}

		b.close();
	}

	/**
	* This method takes a Gauss code representation of a knot
	* and draws the corresponding arc presentation and saves it as a jpg.
	* The diagram is saved to a file.
	* @param gaussCode is Gauss code representation of a knot in the form "-1, 2, -3, 1, -2, 3"
	* @exception IOException on jpg file save
	* @exception InterruptedException thrown if the mathematica script is interrupted
	*/
	public void drawArcPresentation(String gaussCode) throws IOException, InterruptedException
	{
		gaussList = new LinkedList<Integer>();
			
		for (String s: gaussCode.split("[, ]+"))
		{
			int num = Integer.parseInt(s);

			gaussList.addLast(num);
 		}

     	int size = gaussList.size();

     	writer = new PrintWriter("../resources/temp/tempArc.txt");

    	String s;

     	for (int i = 0; i < size; i++)
     	{
     		s = "" + gaussList.get(i); 
     		writer.write(s + " ");
     	}

     	writer.close();

     	Runtime r = Runtime.getRuntime();
		Process p = r.exec("./arcTest.m ../resources/temp/tempArc.txt");

		p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";

		while ((line = b.readLine()) != null) {
		  System.out.println(line);
		}

		b.close();

	}

}