/**
 * Java Frog Memory Game
 * This is a java app, not an applet. Make sure to run as a java app.
 * This class defines the frog object.
 *
 * How the game works:
 * The computer will speak a sequence of colors. Each color corresponds to a color on the four different frogs.
 * Press the pattern of colors the computer speaks.
 *
 *
 * @author Jill Xu
 * @version 1.00, 10 Dec 2016
 */

import processing.core.*;

import java.awt.Image;
import java.lang.*;

public class Frog {
	
	//define variables and objects
    private PVector point;
    PImage img, change;
    PApplet canvas;

    //frog constructor
    public Frog(PVector point, String color, PApplet canvas){
        this.canvas = canvas;
        this.point = point;
        //get image of the colored frog that is called
        img = canvas.loadImage(color + "-frog.png");
        //resize the image to better fit the screen
        img.resize(210, 250);
    }
    
    //getter for PVector
    public PVector getPoint(){
        return point;
    }

    //draw function to draw things to the canvas
    public void draw() {
        canvas.image(img, point.x, point.y);
    }

    public void wipeout(int ID){
    	//change the image of the frog to an image of the dancing frog
    	String color = "";
        canvas.noStroke();
        if (ID == 0)
        	color = "pink";
        else if (ID == 1)
        	color = "yellow";
        else if (ID == 2)
        	color = "blue";
        else if (ID == 3)
        	color = "green";
        change = canvas.loadImage(color + "-change.png");
        //resize the image
        change.resize(210, 250);
        //print to screen
        canvas.image(change, point.x, point.y);
    }
    
    public void wipeout(){
    	//method overloading
    	//clear the frog of question
    	canvas.noStroke();
    	canvas.rect(point.x, point.y, canvas.width / 2, canvas.height / 2);
    }
}
