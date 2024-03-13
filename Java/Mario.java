/*	Gael Brice			            Assignment V : Luigi Sidescroll					          CSCE 3193
 *	10-31-2022					      	        Mario.java						                    Dr. Streeter
 *
 * 	The Mario.java class is an abstract sprite implementation, of which there should only be one
 *  instance of. Represents the player and their actions. 
 */

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class Mario extends Sprite
{
  int jump_count;
  static ArrayList<BufferedImage> animation;
  static BufferedImage currentFrame;

  // physics and frame animation variables
  double vert_velocity;
  boolean direction;
  boolean canJump;
  Iterator<BufferedImage> iterator;

  public Mario(int x, int y)
  {
    this.x = x;
    this.y = y;
    jump_count = 0;

    if (animation == null) 
    {
      animation = new ArrayList<BufferedImage>(8);
      animation.add(View.loadImage("R0.png"));
      animation.add(View.loadImage("R1.png"));
      animation.add(View.loadImage("R2.png"));
      animation.add(View.loadImage("R3.png"));
      animation.add(View.loadImage("R4.png"));
      animation.add(View.loadImage("R5.png"));
      animation.add(View.loadImage("R6.png"));
      animation.add(View.loadImage("R7.png"));

      iterator = animation.iterator();
      currentFrame = animation.get(0);
    }

    this.w = currentFrame.getWidth();
    this.h = currentFrame.getHeight();
  }

  // alters which sprite to show based on a pattern and the passed in motion of luigi
  // true = right facing      false = left facing
  public void changeFrame(boolean direction)
  {
    this.direction = direction;       // used by view to display sprite facing in the correct direction

    if (iterator.hasNext())
      currentFrame = iterator.next();
    else
    {
      iterator = animation.iterator();
      currentFrame = iterator.next();
    }

    this.w = currentFrame.getWidth();
    this.h = currentFrame.getHeight();
  }
  
  public void idle(boolean direction)
  {
      currentFrame = animation.get(0);
  }



  //
  // Sprite functions
  void draw(Graphics g, int scroll)
  {
    if (direction == true)
      g.drawImage(Mario.currentFrame, x - scroll, y, null);
    else
      g.drawImage(Mario.currentFrame, x - scroll + w, y, - w, h, null);
  }
  
  @Override
  boolean isPlayer()  { return true;  }

  // Luigi y physics for gravity
  void update(int groundLevel)
  {
    vert_velocity += 10;

    // velocity increase, within bounds..
    if (vert_velocity > 90)
      vert_velocity = 90;
    if (vert_velocity < -90)
      vert_velocity = -90;

    y += (int)vert_velocity;

    // adjust to keep Mario from phasing into ground.
    if ((y + h >= groundLevel))
    {
      jump_count = 0;
      vert_velocity = 0;
      canJump = true;
      y = groundLevel - h;
    }
  }

  // collision & gravity helper functions
  @Override
  public void correction(Pipe p)
  {
    // x collision from left
    if ((x + w >= p.x) && (prev_x + w <= p.x))
    {
      View.scroll = View.prev_scroll;
      x = prev_x;
    }

    // x collision from right
    else if((x <= p.x + p.w) && (prev_x >= p.x + p.w))
    {
      View.scroll = View.prev_scroll;
      x = prev_x;
    }

    // y collision from above
    else
    {    
      jump_count = 0;
      vert_velocity = 0;
      canJump = true;
      y = p.y - h;
    }
  }



  @Override 
  public String toString()
  {
    return "Luigi (x,y) = (" + x + "," + y + "), vertical velocity = " + vert_velocity + ", currentFrame (w,h) = (" + currentFrame.getWidth() + "," + currentFrame.getHeight() + "), ";
  }
}
