import java.util.*;
import java.io.*;

public class TableMaker
{
	public static void main(String[] args) throws IOException
	{
		Scanner sc = new Scanner(new File(args[0]));
		PrintWriter writer; // = new PrintWriter("the-file-name.txt", "UTF-8");

		String fname = "";
		String guassString = "";
		String input = "";

		while (sc.hasNextLine())
		{
			input = sc.nextLine();

			int count = 0;
			for (String s: input.split("[ ]+"))
			{
				if (count == 0)
				{
					fname = s.trim() + ".txt";
					count++;
				}
				else
				{
					guassString = guassString + s + " ";
				}
     		}
     		writer = new PrintWriter(fname, "UTF-8");

     		writer.print(guassString);
     		writer.close();
     		guassString = "";
     	}
	}
}