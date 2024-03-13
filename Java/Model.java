/*	Gael Brice						Assignment V : Luigi Sidescroll				  CSCE 3193
 *	10-31-2022						       	Model.java						      Dr. Streeter
 *
 * 	The Model.java class updates the position of objects from input via the controller
 *  class. Does *not* draw objects at the new coordinates, only figures out where to
 *  draw. The update method is passed to the View method for this purpose.
 */

// initialize Model class
import java.util.ArrayList;

class Model
{
    // member variables
	ArrayList<Sprite> sprites;
	Mario luigi;
	int groundLevel = 400;

	Model()
	{
		sprites = new ArrayList<Sprite> ();
		luigi = new Mario(100,200);
		sprites.add(luigi);
	}

	// helper function to try loading map file
	static Model loadModel()
	{
		//load map.json if available, otherwise create a new map.
		try		
		{
			return new Model(Json.load("map.json"));
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			System.out.println("Map file is missing or corrupted!\nYou can toggle build mode to create a new one with 'E' or '~'");
			return new Model();
		}
	}

	// Json methods for sprites ArrayList save/load
	Model(Json ob)
	{
		sprites = new ArrayList<Sprite> ();
		luigi = new Mario(100, 200);
		sprites.add(luigi);

		Json tmpList = ob.get("pipes");
		for (int i = 0; i < tmpList.size(); i++)
			sprites.add(new Pipe(tmpList.get(i)));
		tmpList = ob.get("goombas");
		for (int i = 0; i < tmpList.size(); i++)
			sprites.add(new Goomba(tmpList.get(i)));
		
	}

	Json marshal()
    {
        Json ob = Json.newObject();
        Json tmpListPipes = Json.newList();
		Json tmpListGoombas = Json.newList();

        ob.add("pipes", tmpListPipes);
		ob.add("goombas", tmpListGoombas);
        for(int i = 0; i < sprites.size(); i++)
		{
			if (sprites.get(i).isPipe())
            	tmpListPipes.add(((Pipe)sprites.get(i)).marshal());
			if (sprites.get(i).isEnemy())
				tmpListGoombas.add(((Goomba)sprites.get(i)).marshal());
		}
		ob.save("map.json");
        return ob;
    }


	//
	//	Object Manipulation:
	//
	// helper function for editSprite: removes sprite if a click is on an already existing, relevant sprite. goombaMode passin could be 
	// altered and structure could become a switch case statement if more editing options become available
	public boolean clickOnSprite(int x, int y, boolean goombaMode)
	{
		for (int i = 0; i < sprites.size(); i++)		// loop through sprites ArrayList.
		{
			// If click position is on current index's pipe, remove the pipe.
			if ((x >= sprites.get(i).x) && (x <= sprites.get(i).x + sprites.get(i).w) && (y >= sprites.get(i).y) && (y <= sprites.get(i).y + sprites.get(i).h))
			{
				if (sprites.get(i).isPipe() && !goombaMode)
				{
					sprites.remove(i);
					return true;
				}
				if (sprites.get(i).isEnemy() && goombaMode)
				{
					sprites.remove(i);
					return true;
				}
			}
		}
		// if the click position is not on any pipe, pass the false value to parent function: addPipe
		return false;
	}

	// calls on clickOnPipe to determine if click location is valid for new sprite location
	public void editSprite(int a, int b, int scroll, boolean goombaMode)
	{
		int x;
		int y;
		boolean click;

		x = a + scroll;
		y = b;
		click = clickOnSprite(x, y, goombaMode);
		
		if (click == false)								// if no relavant sprite was clicked
		{
			if (!goombaMode)							// and if pipe editing is enabled
			{
				if (y < 100)							// keeps pipe from floating
				{
					y = 100;
					System.out.println("Pipe placement out of pipe file bounds. Placed at highest y possible.");
				}
				Pipe p = new Pipe(x, y);				// create new pipe.
				sprites.add(p);
			}
			else										// adds a goomba if one wasn't previously clicked
			{
				Goomba g = new Goomba(x, y);
				sprites.add(g);
			}
		}
	}

	public void newProjectile()
	{
		Fireball f = new Fireball(luigi.x, luigi.y, luigi.direction);
		sprites.add(f);
	}

	public void killSprite(Sprite s)
	{
		if (s.isEnemy())
		{
			if (((Goomba)s).isAlive)
			{
				((Goomba)s).isAlive = false;
			}
			((Goomba)s).deathFrameCount++;
			((Goomba)s).changeFrame(false);
			s.update(groundLevel);
			if (((Goomba)s).deathFrameCount >= 75)
			{
				sprites.remove(s);
			}
		}
		if (s.isProjectile())
		{
			if (((Fireball)s).isAlive)
			{
				((Fireball)s).isAlive = false;
			}

			if ((s.x > View.scroll + 750) || (s.x < luigi.x - 750) || !((Fireball)s).isAlive) 
			{
				sprites.remove(s);
			}
		}

	}


	public void update()
	{
		// update all sprites ahead of checks so collision is accurate
		for (int i = 0; i < sprites.size(); i++)
			sprites.get(i).update(groundLevel);						
		
		// reiterate through list to check vital collisions
		for (int i = 0; i < sprites.size(); i++)					
		{
			Sprite a, b = null;					// initialize comparator sprites to null
			int j = i + 1;

			if (sprites.get(i).isEnemy())
			{
				if (!((Goomba)sprites.get(i)).isAlive)
				{
					killSprite(sprites.get(i));
					if ( j >= sprites.size())			// fix the comparator loop counter if a sprite was deleted
						j = 0;
					if ( i >= sprites.size())
						i = 0;
				}
			}
			if (sprites.get(i).isProjectile())
			{
				if (!((Fireball)sprites.get(i)).isAlive)
				{
					killSprite(sprites.get(i));
					if ( j >= sprites.size())			// fix the comparator loop counter if a sprite was deleted
						j = 0;
					if ( i >= sprites.size())
						i = 0;
				}
			}

			if ( j >= sprites.size())			// prepare the comparator loop counter in case of sprite comparison
				j = 0;

				
			// if current a sprite is a player or enemy, check for collisions with other sprites
			if (sprites.get(i).isPlayer() || sprites.get(i).isEnemy())
			{
				a = sprites.get(i);

				// loop through sprites and restart at the beginning of the arraylist to catch them all
				while (j != i)
				{
					if (sprites.get(j).isPipe())
					{
						b = sprites.get(j);

						// handle player/enemy pipe collision
						if (collisionCheck(a, b))
						{
							a.correction((Pipe)b);
							if (a.abovePipe((Pipe)b))
								a.update(b.y);
						}
					}

					// handle enemy / projectile collision
					if (sprites.get(j).isProjectile() && sprites.get(i).isEnemy())
					{
						b = sprites.get(j);
						if (collisionCheck(a, b))
						{
							killSprite(a);
							killSprite(b);
						}
					}
					// handle luigi / enemy collision
					
					// increment j based on size of arraylist
					if (j + 1 >= sprites.size())
						j = 0;
					else
						j++;
				}
			}
		}
}
				

	
	boolean collisionCheck(Sprite a, Sprite b)
	{
		if(a.x + a.w < b.x)		// luigi is left of object
			return false;
		if(a.x > b.x + b.w)		// luigi is right of object
			return false;
		if(a.y + a.h < b.y) 	// luigi is above object
			return false;
		if(a.y > b.y + b.h) 	// luigi is below object
			return false;
		return true;			// luigi is inside object: collision detected
	}
}
