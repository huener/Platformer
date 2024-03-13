/*	Gael Brice						Assignment V : Luigi Sidescroll				  CSCE 3193
 *	10-31-2022						     	 View.java						      Dr. Streeter
 *
 * 	The View.java class extends JFrame, and manages our screen. draws all elements inside the 
 *  Game class window.
 */

//  import libraries
	import javax.swing.JPanel;
	import java.awt.Graphics;
	import java.awt.Color;
	import java.awt.image.BufferedImage;
	import javax.imageio.ImageIO;
	import java.io.File;

//implement View class
class View extends JPanel
{
	// Member Variables
	Model model;
	public static int scroll;
	public static int prev_scroll;



	// View Constructor
	View(Controller c, Model m)
	{
		c.setView(this);
		model = m;
		scroll = 0;
	}

	void setModel(Model m)
	{
		model = m;
	}



	// Load sprites into memory, with error catch. Static so that it can be called by other classes without needing a View object declared in them
	static BufferedImage loadImage(String filename)
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File(filename));
		}
		catch(Exception e)
		{
			System.out.println("Could not locate image file " + filename);
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return img;
	}

	// Update the view in the game window via info from Model class
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(36,141,191));					 	 // Blue Sky
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for(int i = 0; i < model.sprites.size(); i++)					// Sprites
		{
			model.sprites.get(i).draw(g, scroll);				
		}

		g.setColor(new Color(255, 159, 66));					 // Ground plane
		g.fillRect(0,400,this.getWidth(), this.getHeight());

	}
}
