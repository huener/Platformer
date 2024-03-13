#!/usr/bin/env bash
set -u -e
clear
echo ""
echo "Compiling Project..."
echo ""
javac Game.java Controller.java View.java Model.java Pipe.java Mario.java Json.java Sprite.java Goomba.java Fireball.java 
echo ""
echo "Compilation Successful!"
echo "Running Project..."
echo ""
echo "-----"
java Game
