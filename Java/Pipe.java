/*	Gael Brice	      			    Assignment V : Luigi Sidescroll				        	  CSCE 3193
 *	10-31-2022				        		       Pipe.java						                    Dr. Streeter
 *
 * 	The Pipe.java class is a container class, each instance of which denotes a speecific
 *  pipe in the map editor.
 */
 import java.awt.image.BufferedImage;
 import java.awt.Graphics;

 class Pipe extends Sprite
 {
    // global variaples which hold pipe position in model.
    static BufferedImage pipePNG;

    public Pipe(int x, int y)
    {
        this.x = x;
        this.y = y;

        if(pipePNG == null)
          pipePNG = View.loadImage("pipe.png");
        
        w = pipePNG.getWidth();
        h = 400 - y;
    }

    Pipe(Json ob)
    {
        x = (int)ob.getLong("x");
        y = (int)ob.getLong("y");

        if(pipePNG == null)
          pipePNG = View.loadImage("pipe.png");

        w = pipePNG.getWidth();
        h = 400 - y;
    }

    Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }


    // Sprite methods
    void update(int q){ }        // pipes are static
    void correction(Pipe p){  }  // pipes are static
    void draw(Graphics g, int scroll)
    {
      g.drawImage(Pipe.pipePNG, x - scroll, y, null);
    }
    @Override
    boolean isPipe()
    {
      return true;
    }
    
    
    @Override 
    public String toString()
    {
      return "Pipe (x,y) = (" + x + ", " + y + "), width = " + w + ", height = " + h +")";
    }

 }
