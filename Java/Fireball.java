/*	Gael Brice	      			    Assignment V : Luigi Sidescroll		       	  CSCE 3193
 *	10-31-2022				        	 Fireball.java						      Dr. Streeter
 *
 * 	The Fireball.java class is an abstract implementation of the sprite class, each instance of which denotes a speecific
 *  fireball thrown by Luigi when the player hits 'ctrl'
 */

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.ArrayList;

public class Fireball extends Sprite 
{
    static ArrayList<BufferedImage> animation;
    static BufferedImage currentFrame;

    // physics and frame animation variables
    double vert_velocity;
    int bounces;
    boolean direction;
    int iterator;
    boolean isAlive;
    int deathFrameCount;

    public Fireball(int x, int y, boolean d)
    {
        this.x = x;
        this.y = y;
        this.direction = d;


        if (Fireball.animation == null) 
        {
            Fireball.animation = new ArrayList<BufferedImage>(4);
            Fireball.animation.add(View.loadImage("f0.png"));
            Fireball.animation.add(View.loadImage("f1.png"));
            Fireball.animation.add(View.loadImage("f2.png"));
            Fireball.animation.add(View.loadImage("f3.png"));

            iterator = 0;
            currentFrame = Fireball.animation.get(0);
        }

        this.w = Fireball.currentFrame.getWidth();
        this.h = Fireball.currentFrame.getHeight();
        this.isAlive = true;
        this.deathFrameCount = 0;
    }



    // alters which sprite to show based on a pattern and the passed in motion of luigi
    // true = right facing      false = left facing
    public void changeFrame(boolean direction)
    {
        this.direction = direction;       // used by view to display sprite facing in the correct direction
        
        if (iterator >= animation.size())
            iterator = 0;
        currentFrame = animation.get(iterator);


        w = Fireball.currentFrame.getWidth();
        h = Fireball.currentFrame.getHeight();
        iterator++;
    }

    void draw(Graphics g, int scroll)
    {
        if (direction == true)
            g.drawImage(Fireball.currentFrame, x - scroll, y, null);
        else
            g.drawImage(Fireball.currentFrame, x - scroll + w, y, - w, h, null);
  
    }
    
    void update(int groundLevel)
    {
        prev_x = x;
        prev_y = y;
        changeFrame(direction);

        if (direction == true)
            x += 20;
        else
            x -= 20;


        vert_velocity += 10;

        // velocity increase, within bounds..
        if (vert_velocity > 90)
            vert_velocity = 90;
        if (vert_velocity < -90)
            vert_velocity = -90;

        y += (int)vert_velocity;

        // adjust to keep Fireball from phasing into ground.
        if ((y + h >= groundLevel))
        {
            if (bounces <= 8)
            {
                vert_velocity -= 11 * ( 8 - bounces);
                bounces++;
            }
            else
                vert_velocity = 0;
            y = groundLevel - h;
        }
    }

    @Override
    boolean isProjectile()   {   return true;    }



    @Override 
    public String toString()
    {
      return "Fireball (x,y) = (" + x + "," + y + "), vertical velocity = " + vert_velocity + ", currentFrame (w,h) = (" + w + "," + h + "), ";
    }
}
