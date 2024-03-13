/*	Gael Brice	      			         Assignment V : Luigi Sidescroll				        	  CSCE 3193
 *	10-31-2022				        		       Pipe.java						                  Dr. Streeter
 *
 * 	The Goomba.java class is a container class, each instance of which denotes a speecific goomba in the map editor.
 */

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.ArrayList;

public class Goomba extends Sprite 
{
    static ArrayList<BufferedImage> animation;
    BufferedImage currentFrame;

    // physics and frame animation variables
    boolean isAlive;
    int deathFrameCount;
    double vert_velocity;
    boolean direction;
    boolean iterator;

    public Goomba(int x, int y)
    {
        this.x = x;
        this.y = y;


        if (Goomba.animation == null) 
        {
        Goomba.animation = new ArrayList<BufferedImage>(4);
        Goomba.animation.add(View.loadImage("G0.png"));
        Goomba.animation.add(View.loadImage("G1.png"));
        Goomba.animation.add(View.loadImage("G2.png"));
        Goomba.animation.add(View.loadImage("G3.png"));


        iterator = false;
        }

        currentFrame = Goomba.animation.get(0);
        this.w = currentFrame.getWidth();
        this.h = currentFrame.getHeight();
        this.isAlive = true;
    }

    Goomba(Json ob)
    {
        this.x = (int)ob.getLong("x");
        this.y = (int)ob.getLong("y");

        if (Goomba.animation == null) 
        {
            Goomba.animation = new ArrayList<BufferedImage>(4);
            Goomba.animation.add(View.loadImage("G0.png"));
            Goomba.animation.add(View.loadImage("G1.png"));
            Goomba.animation.add(View.loadImage("G2.png"));
            Goomba.animation.add(View.loadImage("G3.png"));

            iterator = false;
        }

        currentFrame = Goomba.animation.get(0);
        this.w = currentFrame.getWidth();
        this.h = currentFrame.getHeight();
        this.isAlive = true;
    }

    Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }



    // alters which sprite to show based on a pattern and the passed in motion of Goomba
    // true = right facing      false = left facing
    public void changeFrame(boolean direction)
    {
        this.direction = direction;       // used by view to display sprite facing in the correct direction
        
        if (isAlive)
        {
            if (iterator == false)
                currentFrame = animation.get(0);
            else
                currentFrame = animation.get(1);
        }
        else
        {
            if (iterator == false)
                currentFrame = animation.get(2);
            else
                currentFrame = animation.get(3);
        }

        w = currentFrame.getWidth();
        h = currentFrame.getHeight();
        iterator = !iterator;
    }

    void draw(Graphics g, int scroll)
    {
        if (direction == true)
            g.drawImage(currentFrame, x - scroll, y, null);
        else
            g.drawImage(currentFrame, x - scroll + w, y, - w, h, null);
  
    }
    
    void update(int groundLevel)
    {
        prev_x = x;
        prev_y = y;
        changeFrame(direction);

        if (isAlive)
        {
            if (direction == false)
                x += 5;
            else
                x -= 5;
        }
        else
            deathFrameCount += 1;

        vert_velocity += 10;

        // velocity increase, within bounds..
        if (vert_velocity > 90)
        vert_velocity = 90;
        if (vert_velocity < -90)
        vert_velocity = -90;

        y += (int)vert_velocity;

        // adjust to keep Goomba from phasing into ground.
        if ((y + h >= groundLevel))
        {
        vert_velocity = 0;
        y = groundLevel - h;
        }
    }

    void correction(Pipe p)
    {
        // x collision from left
        if ((x + w >= p.x) && (prev_x + w <= p.x))
        {
            x = p.x - w - 1;
            direction = !direction;
        }

        // x collision from right
        else if((x <= p.x + p.w) && (prev_x >= p.x + p.w))
        {
            x = p.x + p.w + 1;
            direction = !direction;
        }

        // y collision from above
        else if ((y + h >= p.y) && (prev_y + h <= p.y))
        {
            y = p.y - h;
        }
    }

    @Override
    boolean isEnemy()   {   return true;    }



    @Override 
    public String toString()
    {
      return "Goomba (x,y) = (" + x + "," + y + "), vertical velocity = " + vert_velocity + ", currentFrame (w,h) = (" + currentFrame.getWidth() + "," + currentFrame.getHeight() + "), ";
    }
}
