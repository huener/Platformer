/*	Gael Brice			 			Assignment V : Luigi Sidescroll				  CSCE 3193
 *	10-31-2022						    Controller.java					      	  Dr. Streeter
 *
 * 	This .java file listens to keyboard / mouse inputs. Functions according to key press
 */

//  import libraries
	import java.awt.event.MouseListener;
	import java.awt.event.MouseEvent;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
	import java.awt.event.KeyListener;
	import java.awt.event.KeyEvent;

// implement Controller class
class Controller implements ActionListener, MouseListener, KeyListener
{
	// member variables
		//classes
		View view;
		Model model;

		//for keyboard controls
		boolean keyLeft;
		boolean keyRight;
		boolean keyUp;
		boolean keyDown;
		boolean keySpace;

		//for determining change state
		boolean edit;
		boolean goombaMode;
		boolean prevDir;

	// initialize controller. Passed in Model class so that the Controller can update
	// where non-static objects go via either mouse or key input
	Controller(Model m)
	{
		model = m;
		edit = false;
	}

	void setModel(Model m)
	{
		model = m;
	}

	// allow controller to also be used for JPanel operations in view
	//
	void setView(View v)
	{
		view = v;
	}



	// mouse events:
	public void mousePressed(MouseEvent e)
	{
		if (edit)
			model.editSprite(e.getX(), e.getY(), View.scroll, goombaMode);
	}
	public void mouseReleased(MouseEvent e) {	}
	public void mouseEntered(MouseEvent e) 	{	}
	public void mouseExited(MouseEvent e) 	{	}
	public void mouseClicked(MouseEvent e) 	
	{
	}
	public void actionPerformed(ActionEvent e)	{	}



	// keyboard events:
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())	// detect which arrow keys are currently being pressed. Allows multi-input.
		{
			case KeyEvent.VK_RIGHT: 		keyRight = true; break;
			case KeyEvent.VK_LEFT:  		keyLeft = true; break;
			case KeyEvent.VK_SPACE:			keySpace = true; break;
		}
	}
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())	// detect which keys are no longer being pressed. Allows multiple inputs.
		{
			case KeyEvent.VK_RIGHT: 		keyRight = false; break;
			case KeyEvent.VK_LEFT: 			keyLeft = false; break;
			case KeyEvent.VK_SPACE:			keySpace = false; model.luigi.canJump = false; break;
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_Q:				System.exit(0); break;
			case KeyEvent.VK_BACK_QUOTE:
			case KeyEvent.VK_E:					
			{
				edit = !edit;
				if(edit==true)
					System.out.println("Editing Map...\n");
				else
					System.out.println("Running Game...\n");
				break;
			}
			case KeyEvent.VK_S:			
			{
				model.marshal(); 
				break;
			}
			case KeyEvent.VK_L:			
			{
				model = Model.loadModel();
				view.setModel(model);
				break;
			}
			case KeyEvent.VK_G:
			{
				if (edit)
					goombaMode = !goombaMode;
				else
					System.out.println("Edit mode not activated! Hit 'E' or '~' to edit map.");

				if (goombaMode)
					System.out.println("Goomba Mode: click anywhere to add/remove a goomba.");
				else
					System.out.println("Pipe Mode: click anywhere to add/remove a pipe.");
				break;
			}
			case KeyEvent.VK_CONTROL:
			{
				if(!edit)
					model.newProjectile();
				break;
			}
		}
	}
	public void keyTyped(KeyEvent e)	{	}



	// Update the map based on what is being pressed.
	void update()
	{
		// set previous positions
		model.luigi.prev_x = model.luigi.x;
		model.luigi.prev_y = model.luigi.y;

		View.prev_scroll = View.scroll;

		if(edit == true)
		{
			if(keyRight) 	View.scroll += 20;
			if(keyLeft) 	View.scroll -= 20;
		}
		else
		{
			if(keyRight)	
			{
				View.scroll += 10;
				try
				{
				model.luigi.changeFrame(true);
				}
				catch(Exception e)
				{
				}
				model.luigi.x += 10;
				prevDir = true;
			}
			if(keyLeft)
			{
				View.scroll -= 10;
				try
				{
				model.luigi.changeFrame(false);
				}
				catch(Exception e)
				{
				}
				model.luigi.x -= 10;
				prevDir = false;
			}
			if(keySpace)
			{	
				if ((model.luigi.jump_count <= 4) && model.luigi.canJump)
				{
					model.luigi.vert_velocity -= 18;
					model.luigi.jump_count++;
				}
				// System.out.println("jump frame counter: " + model.luigi.jump_count);
			}
			if (!keyLeft && !keyRight && !keySpace)
			{
				model.luigi.idle(prevDir);
			}
		}

	}
}
