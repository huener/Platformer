 #	Gael Brice	      			         Assignment VIII : Luigi Sidescroll				        	  CSCE 3193
 #	12-8-2022				        		         Game.py						                  Dr. Streeter
 #
 # 	This is a python implementation of the simplistic Mario-style sidescroller developed in previous assignments.
 #  It maintains the Object-Oriented style enhanced throughout the semester and does not use the pygame library
 #	to its fullest potential.
 #

import pygame
import time

from pygame.locals import*
from time import sleep

class Model():
		def __init__(self):
			# create list of sprites & set ground level
			self.sprites = []
			self.ground_level = 400

			# create and add Luigi to sprite list
			self.sprites.append(Mario(100, 332))
			self.luigi = self.sprites[0]
			self.sprites.append(Pipe(1669, 161))
			self.sprites.append(Goomba(-342, 363))

		def update(self):
			# update all sprites ahead of checks so collision is accurate
			for i in range(len(self.sprites)):
				self.sprites[i].update(self.ground_level)						
			
			# reiterate through list to check vital collisions
			i = 0
			while i < len(self.sprites):
				j = i + 1
				(i, j, len(self.sprites))

				if (self.sprites[i].isEnemy() == True) or (self.sprites[i].isProjectile() == True):
					if self.sprites[i].isAlive == False:
						self.killSprite(i)
						if j >= len(self.sprites):			# fix the comparator loop counter if a sprite was deleted
							j = 0
						if i >= len(self.sprites):
							i -= 1
				if j >= len(self.sprites):					# prepare the comparator loop counter in case of sprite comparison
					j = 0

					
				# if current a sprite is a player or enemy, check for collisions with other sprites
				if (self.sprites[i].isPlayer() == True) or (self.sprites[i].isEnemy() == True):
					a = self.sprites[i]

					# loop through sprites and restart at the beginning of the arraylist to catch them all
					while j != i:
						if self.sprites[j].isPipe():
							b = self.sprites[j]

							# handle player/enemy pipe collision
							if self.collisionCheck(a, b):
								a.correction(b)
								if a.abovePipe(b):
									a.update(b.y)

						# handle enemy / projectile collision
						if (self.sprites[j].isProjectile() == True) and (self.sprites[i].isEnemy() == True):
							b = self.sprites[j]
							if self.collisionCheck(a, b) == True:
								self.killSprite(i)
								self.killSprite(j)

						# handle luigi / enemy collision (TODO)
						
						# increment j based on size of arraylist
						if j + 1 >= len(self.sprites):
							j = 0
						else:
							j += 1
				i += 1
		
		def newProjectile(self):
			f = Fireball(self.luigi.x, self.luigi.y, self.luigi.direction)
			self.sprites.append(f)
		
		def killSprite(self, q):
			if q >= len(self.sprites):
				q = 0
			s = self.sprites[q]

			if s.isEnemy():
				if s.isAlive == True:
					s.isAlive = False
				s.death_frame_count += 1
				s.changeFrame(False)
				s.update(self.ground_level)
				if s.death_frame_count >= 75:
					self.sprites.remove(s)

			if s.isProjectile():
				if s.isAlive == True:
					s.isAlive = False
				if ((s.x > View.scroll + 750) or (s.x < self.luigi.x - 750) or not(s.isAlive == True)):
					self.sprites.remove(s)

		def collisionCheck(self, a, b):
			if(a.x + a.w <= b.x):		# sprite is left of object
				return False
			if(a.x >= b.x + b.w):		# sprite is right of object
				return False
			if(a.y + a.h <= b.y):		# sprite is above object
				return False
			if(a.y >= b.y + b.h):		# sprite is below object
				return False
			return True					# sprite is inside object: collision detected

class Controller():
		def __init__(self, model):
			self.model = model
			self.keep_going = True
			self.prevDir = False

		def update(self):
			# set previous positions
			self.model.luigi.prev_x = self.model.luigi.x
			self.model.luigi.prev_y = self.model.luigi.y
			View.prev_scroll = View.scroll

			# handle key down events
			for event in pygame.event.get():
				if event.type == QUIT:
					self.keep_going = False
				elif event.type == KEYDOWN:
					if event.key == K_ESCAPE:
						self.keep_going = False
					if event.key == K_SPACE:
						self.model.luigi.canJump = False
					if event.key == K_LCTRL:
						self.model.newProjectile()

			# handle key press events
			keys = pygame.key.get_pressed()
			if keys[K_LEFT]:
				self.model.luigi.x -= 10
				self.model.luigi.changeFrame(True)
				self.prevDir = False
				View.scroll -= 10
			if keys[K_RIGHT]:
				self.model.luigi.x += 10
				self.model.luigi.changeFrame(False)
				self.prevDir = True
				View.scroll += 10
			if keys[K_SPACE]:
				if (self.model.luigi.jump_count <= 4) and (self.model.luigi.canJump == True):
					self.model.luigi.vert_velocity -= 18
					self.model.luigi.jump_count += 1
			if (not(keys[K_LEFT]) and not(keys[K_RIGHT])):
					self.model.luigi.idle(self.prevDir)

class View():
		scroll = 0
		prev_scroll = 0

		def __init__(self, model, controller):
			# Set up game window
			resolution = (750, 500)
			self.screen = pygame.display.set_mode(resolution, 32)
			pygame.display.set_caption("Luigi Time")
			pygame.mouse.set_visible(False)

			# Set up ties to rest of game model
			self.model = model
			self.controller = controller

		def update(self):
			# Blue sky background
			self.screen.fill([36, 141, 191])
			# draw sprite list
			for sprite in self.model.sprites:
				sprite.draw(self.screen)
			# Orange ground plane
			self.screen.fill([255, 159, 66],  (0, 400, 750, 500))
			# push update to screen
			pygame.display.flip()



class Sprite():
		def __init__(self, x, y):
			# prepare frames
			self.animation = []

			# prepare sprite mapping box
			self.direction = False
			self.x = x
			self.y = y
			self.w = 0
			self.h = 0
			self.prev_x = 0
			self.prev_y = 0
			self.rect = pygame.Rect(self.x, self.y, self.w, self.h)

		def draw(self, screen):
			v = (self.rect[0] - View.scroll, self.rect[1], self.rect[2], self.rect[3])
			if self.direction == False:
				screen.blit(self.currentFrame, v)
			else:
				screen.blit(pygame.transform.flip(self.currentFrame, True, False), v)

		# gravity and collision methods: default to Luigi & Goomba mode. Override to add/remove motion
		def update(self, ground_level):
			self.vert_velocity += 10

			# velocity increase, within bounds..
			if self.vert_velocity > 90:
				self.vert_velocity = 90
			if self.vert_velocity < -90:
				self.vert_velocity = -90

			self.y += self.vert_velocity

			# adjust to keep sprite from phasing into ground.
			if (self.y + self.h >= ground_level):
				self.vert_velocity = 0
				self.y = ground_level - self.h
				if self.isPlayer() == True:
					self.canJump = True
					self.jump_count = 0

			# set sprite mapping box to the correct coordinates
			self.rect = (self.x, self.y, self.w, self.h)

		def correction(self, p):
			# x collision from left
			if (self.x + self.w >= p.x) and (self.prev_x + self.w <= p.x):
				if self.isPlayer() == True:
					View.scroll = View.prev_scroll
					self.x = self.prev_x
				else:
					self.x = p.x - self.w
				if self.isEnemy() == True:
					self.direction = not(self.direction)

			# x collision from right
			elif (self.x <= p.x + p.w) and (self.prev_x >= p.x + p.w):
				if self.isPlayer() == True:
					View.scroll = View.prev_scroll
					self.x = self.prev_x
				else:
					self.x = p.x + p.w
				if self.isEnemy() == True:
					self.direction = not(self.direction)

			# y collision from above
			# this statement can be reversed if bottom pipe collision is a necessity at some point.
			else:
				self.y = p.y - self.h
				self.vert_velocity = 0
				if self.isPlayer() == True:
					self.canJump = True
					self.jump_count = 0
		
		def abovePipe(self, p):
			if (self.prev_y + self.h >= p.y):
				return False
			if ((self.x + self.w >= p.x) and (self.x + (2 * self.w) <= p.x + p.w)):
				return True
			return False
		
		# grouping: reimplement via pygame?
		def isPlayer(self):
			return False

		def isPipe(self):
			return False

		def isEnemy(self):
			return False

		def isProjectile(self):
			return False

class Mario(Sprite):
		def __init__(self, x, y):
			super().__init__(x, y)

			#set up animation
			for i in range(8):
				frame = pygame.image.load("R" + str(i) + ".png")
				self.animation.append(frame)
			self.currentFrame = self.animation[0]

			# set up sprite frame
			self.w = pygame.Surface.get_width(self.currentFrame)
			self.h = pygame.Surface.get_height(self.currentFrame)
			self.iterator = 0
			self.rect = (self.x, self.y, self.w, self.h)

			# physics variables
			self.canJump = True
			self.jump_count = 0
			self.vert_velocity = 0

		def changeFrame(self, direction):
			self.direction = direction;       # used by view to display sprite facing in the correct direction

			if self.iterator >= 7:
				self.iterator = 0
			else:
				self.iterator += 1
			
			self.currentFrame = self.animation[self.iterator]
			self.w = pygame.Surface.get_width(self.currentFrame)
			self.h = pygame.Surface.get_height(self.currentFrame)

		def idle(self, prev_dir):
			self.currentFrame = self.animation[0]

		def isPlayer(self):
			return True

class Pipe(Sprite):
		pipe_PNG = pygame.image.load("pipe.png")

		def __init__(self, x, y):
			super().__init__(x, y)
			
			# set up animation - pipes are static
			self.currentFrame = Pipe.pipe_PNG

			# set up sprite frame
			self.w = 55
			self.h = 400 - self.y
			self.rect = (self.x, self.y, self.w, 400)

		def draw(self, screen):
			# this function is virtually the same as the inherited one, however the draw y value of the static pipes need to be altered slightly on python. unsure why. 
			v = (self.rect[0] - View.scroll, self.rect[1] + 10, self.rect[2], self.rect[3])
			if self.direction == False:
				screen.blit(self.currentFrame, v)
			else:
				screen.blit(pygame.transform.flip(self.currentFrame, True, False), v)

		def update(self, ground_level):
			pass

		def correction(self, p):
			pass
		
		def abovePipe(self, p):
			pass

		def isPipe(self):
			return True

class Goomba(Sprite):
		def __init__(self, x, y):
			super().__init__(x, y)

			#set up animation
			for i in range(4):
				frame = pygame.image.load("G" + str(i) + ".png")
				self.animation.append(frame)
			self.currentFrame = self.animation[0]
			
			# set up sprite frame
			self.w = pygame.Surface.get_width(self.currentFrame)
			self.h = pygame.Surface.get_height(self.currentFrame)
			self.rect = (self.x, self.y, self.w, self.h)
			self.death_frame_count = 0
			self.iterator = False

			# physics variables
			self.vert_velocity = 0
			self.isAlive = True

		def update(self, ground_level):
			self.prev_x = self.x
			self.prev_y = self.y
			self.changeFrame(self.direction)

			if self.isAlive:
				if self.direction == True:
					self.x += 5
				else:
					self.x -= 5
			else:
				self.death_frame_count += 1

			super().update(ground_level)

		def changeFrame(self, direction):
			self.direction = direction;       # used by view to display sprite facing in the correct direction

			if self.isAlive == True:
				if self.iterator == False:
					self.currentFrame = self.animation[0]
				else:
					self.currentFrame = self.animation[1]
			else:
				if self.iterator == False:
					self.currentFrame = self.animation[2]
				else:
					self.currentFrame = self.animation[3]

			self.w = pygame.Surface.get_width(self.currentFrame)
			self.h = pygame.Surface.get_height(self.currentFrame)
			self.iterator = not self.iterator

		def isEnemy(self):
			return True

class Fireball(Sprite):
		def __init__(self, x, y, direction):
			super().__init__(x, y)
			self.direction = direction

			#set up animation
			for i in range(4):
				frame = pygame.image.load("f" + str(i) + ".png")
				self.animation.append(frame)
			self.currentFrame = self.animation[0]
			
			# set up sprite frame
			self.w = pygame.Surface.get_width(self.currentFrame)
			self.h = pygame.Surface.get_height(self.currentFrame)
			self.rect = (self.x, self.y, self.w, self.h)
			self.iterator = 0
			self.death_frame_count = 0

			# physics variables
			self.vert_velocity = 0
			self.bounces = 0
			self.isAlive = True

		def changeFrame(self, direction):
			self.direction = direction;       # used by view to display sprite facing in the correct direction
            
			if self.iterator >= len(self.animation):
				self.iterator = 0
			self.currentFrame = self.animation[self.iterator]

			self.iterator += 1
		
		def update(self, ground_level):
			
			self.prev_x = self.x
			self.prev_y = self.y
			self.changeFrame(self.direction)

			if self.direction == False:
				self.x += 20
			else:
				self.x -= 20

			self.vert_velocity += 10

            # velocity increase, within bounds..
			if (self.vert_velocity > 90):
				self.vert_velocity = 90
			if (self.vert_velocity < -90):
				self.vert_velocity = -90
				
			self.y += self.vert_velocity

            # adjust to keep Fireball from phasing into ground.
			if self.y + self.h >= ground_level:
				if self.bounces <= 8:
					self.vert_velocity -= 11 * (8 - self.bounces)
					self.bounces += 1
				else:
					self.vert_velocity = 0
				self.y = ground_level - self.h

			# set sprite mapping box to the correct coordinates
			self.rect = (self.x, self.y, self.w, self.h)

		def isProjectile(self):
			return True



## main script ##
print("Welcome to Mushroom Kingdom! Use the left / right arrow keys to move.")
print("Space: Jump")
print("Ctrl: Fireball")

m = Model()
c = Controller(m)
v = View(m, c)

while c.keep_going:
	c.update()
	m.update()
	v.update()
	sleep(0.04)
