package gui;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.util.*;
import java.io.*;

import javax.imageio.ImageIO;
// import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import java.util.concurrent.TimeUnit;

import knot.*;
import gaussCodeGenerator.*;

	/**
	* <h1>A GUI to access the generation and colouring functionality</h1>
	* The KnotGUI class makes avaliable functionality from the gaussCodeGenerator.NaiveShadowGaussGenerator and knot.Colourist class, without the need to work on the command line.
	*
	* @author  Craig Reilly
	* @version 0.1
	* @since   2015-09-07
	*/

public class KnotGUI extends JFrame implements ActionListener {
	private JLabel picture = null;
	// private JLabel pictureA = null;
	private final int KNOT_DIAGRAM = 0;
	private final int ARC_PRESENTATION = 1;
	private int[] primes = new int[]{3, 5, 7, 11, 13, 17, 23, 29};

	private int picToBeDrawn = 0;

	private JButton planarPicButton, arcPicButton;
	private JButton genShadowButton, genAltButton, genAltPrimeButton;
	private JButton colouringStartButton;
	private JTextField gaussInputField;
	private JLabel colouringLabel;
	
	private PictureDraw picDraw;
	private GaussCodeGenTask gen;
	private ColouringTask colour;

	private int crossings = 0;
	private int generationOptions = 0;

	private String gaussCode = "";

	/**
    * The constructor for KnotGUI objects.  Creates and lays out the GUI
    */
	public KnotGUI()
	{
		super("KnotGUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		createMenuBar();
		

		layoutTop();

		layoutCenter();

		pack();
		setVisible(true);
	}

	/**
    * Creates the menu bar,and adds anonymous listeners to the menu options
    */
	public void createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		// create the high level menu list
        JMenu file = new JMenu("File");
        JMenu generate = new JMenu("Generate");
        JMenu save = new JMenu("Save");
        
        // create the file menu options 
        JMenuItem openGC = new JMenuItem("Open Gauss code from file");
        JMenuItem exit = new JMenuItem("Exit");

        openGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("openGC pressed");
            }
       	});

        exit.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        file.add(openGC);
        file.add(exit);

        // create the genertion menu options
        JMenuItem generateRandomGC = new JMenuItem("Generate random GC");
        JMenuItem genearteRandomPrimeGC = new JMenuItem("Generate random prime GC");
        JMenuItem generateAllGC = new JMenuItem("Generate all Gauss codes of size n");

        generateRandomGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateRandomGC pressed");
            	generationOptions = 0;
            	crossings = Integer.parseInt(JOptionPane.showInputDialog("Enter crossing number"));
            	(gen = new GaussCodeGenTask()).execute();

            }
        });

        genearteRandomPrimeGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateRandomPrimeGC pressed");
            	generationOptions = 2;
            	crossings = Integer.parseInt(JOptionPane.showInputDialog("Enter crossing number"));
            	(gen = new GaussCodeGenTask()).execute();
            }
        });

       	generateAllGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("generateAllGC pressed");
            }
        });

       	generate.add(generateRandomGC);
       	generate.add(genearteRandomPrimeGC);
       	generate.add(generateAllGC);

       	// create the save menu optiongs
       	JMenuItem saveGC = new JMenuItem("Save Gauss code");
       	JMenuItem savePic = new JMenuItem("Save knot picture");

       	saveGC.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("saveGC pressed");
            }
        });

       	savePic.addActionListener(new ActionListener()
       	{
            @Override
            public void actionPerformed(ActionEvent event) {
            	System.out.println("savePic pressed");
            }
        });

       	save.add(saveGC);
       	save.add(savePic);

       	menuBar.add(file);
       	menuBar.add(generate);
       	menuBar.add(save);

        setJMenuBar(menuBar);
	}

	/**
    * Constructs the label which will house the knot picture
    * @return a JLabel object
    * @throws IOException if the picture isn't in the resources/temp/ directory
    */
	public JLabel makePic() throws IOException
	{
		// JLabel picLabel = new JLabel();
		// setPreferredSize(

		// BufferedImage myPicture = ImageIO.read(new File("temp/planarPic.jpg"));
		JLabel picLabel = new JLabel();//new JLabel(new ImageIcon(myPicture));
		picLabel.setIcon( new ImageIcon(ImageIO.read( new File("../resources/temp/planarPic.jpg") ) ) );
		add(picLabel, BorderLayout.SOUTH);
		return picLabel;
	}

	/**
    * Constructs textfield objects
    * @return a textfiled object
    */
	private JTextField makeText()
	{
		JTextField a = new JTextField(30);
		a.setText("-1, 2, -3, 1, -2, 3");
		// add(a, BorderLayout.NORTH);
		return a;
	}

	/**
    * Constructs JButton objects
    * @param label the text to be placed on the button
    * @return a JButton object
    */
	private JButton makeButton(String label)
	{
		JButton a = new JButton(label); 
		return a;
	}

	/**
    * Constructs the top of the GUI, where the Gauss code text field is found
    */
	public void layoutTop() {
		JPanel top = new JPanel();
		JLabel gaussLabel = new JLabel();
		gaussLabel.setText("Gauss code: ");
		gaussInputField = makeText();
		top.add(gaussLabel);
		top.add(gaussInputField);
		add(top, BorderLayout.NORTH);
	}

	/**
	 * Constructs the center of the GUI, where the panes are found
	 */
	public void layoutCenter() 
	{
		// instantiate panel for bottom of display
		JTabbedPane jTabs = new JTabbedPane();

		JPanel d = layoutDrawingPane();
		JPanel invar = layoutInvariantsPane();

		jTabs.add("Drawing", d);
		jTabs.add("Invariants", invar);

		add(jTabs);
		
	}

	/**
	 * Constructs the drawing pane, where the knots are drawn
	 * @return drawing JPanel
	 */
	public JPanel layoutDrawingPane()
	{
		JPanel drawing = new JPanel(new BorderLayout());

		JPanel drawingButtons = new JPanel(new GridBagLayout());
		// add upper label, text field and button
		planarPicButton = makeButton("Draw knot diagram");
		planarPicButton.addActionListener(this);

		arcPicButton = makeButton("Draw arc diagram");
		arcPicButton.addActionListener(this);

		drawingButtons.add(planarPicButton);
		drawingButtons.add(arcPicButton);

		try
		{
			picture = makePic();
		}
		catch(IOException e)
		{
			System.out.println("Image file not found");
		}

		drawing.add(drawingButtons, BorderLayout.NORTH);
		drawing.add(picture, BorderLayout.CENTER);

		return drawing;
	}

	/**
	 * Constructs the colouring invariants pane, where the colouring information is found
	 * @return colouring JPanel
	 */
	public JPanel layoutInvariantsPane()
	{
		JPanel invariants = new JPanel(new BorderLayout());
		colouringLabel = new JLabel("This knot is colourable mod:");

		colouringStartButton = makeButton("Start colouring");
		colouringStartButton.addActionListener(this);

		invariants.add(colouringStartButton, BorderLayout.NORTH);
		invariants.add(colouringLabel, BorderLayout.CENTER);

		return invariants;
	}

	/**
	 * Deals with button presses on the GUI.
	 * <p>
	 * If the drawing buttons are pressed an PictureDraw task is started on the event dispatch thread.  However, due to short comings in the drawing scripts, these tasks are joined
	 * to the main thread, so that they can timed out after 15 seconds.  
	 * <p>
	 * If the colouring button is pressed a ColouringTask is started on the event dispatch thread.  This task colours the knot with all primes up less than 30.
	 * @param ae An actionevent corresponding to some button on the main panel of the GUI
	 */
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == planarPicButton)
		{
			picToBeDrawn = KNOT_DIAGRAM;

			(picDraw = new PictureDraw()).execute();
			try
			{
				picDraw.get(15, TimeUnit.SECONDS); 
			}
			catch(Exception e)
			{
				System.out.println("That took too long");
				// imposing a timeout on picDraw doesn't impose a timeout on the athematica script that it calls
				// here we kill the WolframKernel to stop the Mathematica script
				try 
				{
					System.out.println("In the try");
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("pkill -9 WolframKernel");
					System.out.println("Killed the WolframKernel");
				}
				catch(Exception ex)
				{
					System.out.println("There was an error in killing WolframKernel");
				}
				JOptionPane.showMessageDialog(this,
   					"The Mathematica script used to draw knot\ndiagrams often finds nonprime knots difficult, \nso we've set a 15 second timeout.",
    				"Drawing Timeout",
    				JOptionPane.WARNING_MESSAGE);
			}
		}
		else if (ae.getSource() == arcPicButton)
		{
			picToBeDrawn = ARC_PRESENTATION;
			
			(picDraw = new PictureDraw()).execute();
			try
			{
				picDraw.get(60, TimeUnit.SECONDS); 
			}
			catch(Exception e)
			{
				System.out.println("That took too long");
				try 
				{
					System.out.println("In the try");
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("pkill -9 WolframKernel");
					System.out.println("Killed the WolframKernel");
				}
				catch(Exception ex)
				{
					System.out.println("There was an error in killing WolframKernel");
				}
				JOptionPane.showMessageDialog(this,
    				"The Mathematica script used to draw knot\ndiagrams often finds nonprime knots difficult, \nso we've set a 15 second timeout.",
    				"Drawing Timeout",
    				JOptionPane.WARNING_MESSAGE);

			}
		}
		else if (ae.getSource() == colouringStartButton)
		{
			// reset the colouring label each time the colouring is called
			colouringLabel.setText("This knot is colourable mod:");
			(colour = new ColouringTask()).execute();
		}
	}
	

   	private class PictureDraw extends SwingWorker<Void, Void> {
       	@Override
       	public Void doInBackground() 
       	{
			MathematicaAdapter ma = new MathematicaAdapter();

			String gaussString = gaussInputField.getText();

			try
			{
				if (picToBeDrawn == KNOT_DIAGRAM)
				{
					System.out.println("Called planar diagram script");
					ma.drawPlanarDiagram(gaussString);
				}
				else if (picToBeDrawn == ARC_PRESENTATION)
				{
					ma.drawArcPresentation(gaussString);
				}
			}
			catch (Exception e)
			{
				System.out.println("Knot diagram picture not found");
			}

       	    return null;
       	}	

      	@Override
       	protected void done() {
        		try 
        		{
	        		System.out.println("Got in the done method");
	        		// ImageIcon icon = new ImageIcon("/temp/planarPic.jpg");
	        		// icon.getImage().flush();
	          //      	picture.setIcon(icon);
	        		// resetPicture();
	        		picture.setIcon(null);
	        		if (picToBeDrawn == KNOT_DIAGRAM)
	        		{
						picture.setIcon( new ImageIcon(ImageIO.read( new File("../resources/temp/planarPic.jpg") ) ) );
	        		}
	        		else if (picToBeDrawn == ARC_PRESENTATION)
	        		{
	        			picture.setIcon( new ImageIcon(ImageIO.read( new File("../resources/temp/arcPic.jpg") ) ) );
	        		}
					picture.revalidate();
					System.out.println("We've reset the picture");
           		} 
           		catch (Exception ignore) 
           		{
           			//do nothing
           		}
       	}
   	}

   	private class GaussCodeGenTask extends SwingWorker<Void, Void> {
       	@Override
       	public Void doInBackground() 
       	{
       		// we don't want verbose output --- that's why false
            NaiveShadowGaussGenerator sGG = new NaiveShadowGaussGenerator(crossings, generationOptions, false);

            gaussCode = sGG.solutionToString();

       	    return null;
       	}	

      	@Override
       	protected void done() 
       	{
       		gaussInputField.setText(gaussCode);
       	}
   	}

   	private class ColouringTask extends SwingWorker<Void, Integer> {
	    @Override
	    public Void doInBackground() 
	    {
	    	System.out.println("In do in doInBackground");

			gaussCode = gaussInputField.getText();

     		for (int i = 0; i < primes.length; i++)
     		{
     			System.out.println("colouring mod " + primes[i]);
     			Colourist colourist = new Colourist(gaussCode, primes[i]);

     			if (colourist.isColourable())
     			{
     				publish(new Integer(primes[i]));
     			}
     		}
	   	    return null;
	   	}	

	   	// Every now and then the event dispatch thread will call process
		// In this example, I get it to set the count text value
		protected void process(List<Integer> colourable) {
			int lastVal = colourable.get(colourable.size()-1);
			String alreadyOnLabel = colouringLabel.getText();
			colouringLabel.setText(alreadyOnLabel + " " + lastVal);
		}

	  	@Override
	   	protected void done() 
	   	{
	   		if (colouringLabel.getText().equals("This knot is colourable mod:"))
	   		{
	   			colouringLabel.setText("This knot isn't colourable mod 3, 5, or 7");
	   		}
	   	}

	   // 	public int orient(int n)
    // 	{
    // 		int orient;

    // 		if (n < 0)
    // 		{
    // 		orient = Knot.UNDER;
    // 		}
    // 		else
    // 	{
    // 		orient = Knot.OVER;
    // 	}

    // 	return orient;
    // }

   	}

	/**
	 * Resets the picture to the picture in 
	 * <p>
	 * If the drawing buttons are pressed an PictureDraw task is started on the event dispatch thread.  However, due to short comings in the drawing scripts, these tasks are joined
	 * to the main thread, so that they can timed out after 15 seconds.  
	 * <p>
	 * <p>
	 * If the colouring button is pressed a ColouringTask is started on the event dispatch thread.  This task colours the knot with all primes up less than 30.
	 * @param ae An actionevent corresponding to some button on the main panel of the GUI
	 */
   	// public void resetPicture() throws IOException
   	// {
   	// 	picture.setIcon( new ImageIcon(ImageIO.read( new File("temp/planarPic.jpg") ) ) );		
   	// }

	/**
	 * The main method which starts the GUI
	 * @param args Does not take command line arguements
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new KnotGUI();
			}
		});
	}
}