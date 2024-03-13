/*	Gael Brice						Assignment V : Luigi Sidescroll				  CSCE 3193
 *	10-31-2022						     	  Game.java						      Dr. Streeter
 *
 * 	Pulls together View, Controller, and Model classes to create a constantly updating
 * 	window using JFrame. 
 */

//  import libraries
	import javax.swing.JFrame;
	import java.awt.Toolkit;

// implement Game class
public class Game extends JFrame
{
	// Member Variables
	Model model;
	Controller controller;
	View view;

	// Constructor for Game
	//
	public Game()
	{
		// Declare new objects for member variables
		model = Model.loadModel();
		controller = new Controller(model);
		view = new View(controller, model);

		// Set game window up
		this.setTitle("Assignment V: Paragon of Awesomeness");
		this.setSize(1000, 500);
		this.setResizable(false);
		this.setFocusable(true);
		// Set up View class inside game window
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		// Set up Controller
		view.addMouseListener(controller);
		this.addKeyListener(controller);

	}

	// run script: loops until program is closed, runs update and repaint methods in
	// View, Controller, Model.
	public void run()
	{
		while(true)
		{
			controller.update();
			model = controller.model;			// Updates the model to be the one initialized by the controller if map.json was loaded
			model.update();
			view.repaint(); 					// Indirectly calls View.paintComponent
			Toolkit.getDefaultToolkit().sync(); // Updates screen

			// Go to sleep for 40 milliseconds: sets framerate to 25fps
			try
			{
				Thread.sleep(40);
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	// main function to start a new Game window and use the run script class.
	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}
