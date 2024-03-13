/*	Gael Brice						Assignment V : Luigi Sidescroll				  CSCE 3193
 *	10-31-2022						       	Sprite.java						      Dr. Streeter
 *
 * 	The Sprite.java class is an abstract class for all sprite objects in the game to pull
 *  variables and methods from as a template. 
 * 
 */

import java.awt.Graphics;

public abstract class Sprite

{
    //
    // Abstract Variables
    int x, y, w, h, prev_x, prev_y;

    //
    // Abstract Methods
    abstract void draw(Graphics g, int scroll);
    abstract void update(int q);

    void correction(Pipe p)
    {
        // x collision from left
        if ((x + w >= p.x) && (prev_x + w <= p.x))
        {
            View.scroll = View.prev_scroll;
            x = p.x - w;
        }

        // x collision from right
        else if((x <= p.x + p.w) && (prev_x >= p.x + p.w))
        {
            View.scroll = View.prev_scroll;
            x = p.x + p.w;
        }

        // y collision from above
        else if ((y + h >= p.y) && (prev_y + h <= p.y))
        {    // this statement can be reversed if bottom pipe collision is a necessity at some point.
            y = p.y - h;
        }
    }

    boolean abovePipe(Pipe p)
    {
        if (prev_y + h >= p.y)
            return false;
        if ((x + w >= p.x) && (x + (2 * w) <= p.x + p.w))
            return true;
        return false;
    }

    boolean isPipe()
    {
        return false;
    }

    boolean isEnemy()
    {
        return false;
    }

    boolean isPlayer()
    {
        return false;
    }
    
    boolean isProjectile()
    {
        return false;
    }
}