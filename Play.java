/**
 * Java Frog Memory Game
 * This is a java app, not an applet. Make sure to run as a java app.
 * This class contains all the methods that together allow the user to play the game.
 * It contains the methods for the audio commands, scoreboard, mousePressed() and frog animation
 *
 * How the game works:
 * The computer will speak a sequence of colors. Each color corresponds to a color on the four different frogs.
 * Press the pattern of colors the computer speaks. Make sure to press the mouse down fully and do not double press.
 * The length to memorize is all mixed together and the longest is 8 colors so make sure to listen carefully.
 * 
 *
 * @author Jill Xu
 * @version 1.00, 10 Dec 2016
 */

// import processing library 
import processing.core.*;
// import util library
import java.util.*;

// import libraries for sound and file i/o
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class Play extends PApplet {
	// declare arrays, fonts and variables
	private Frog[] frogs = new Frog[4];
	private File[] commands;
	private boolean waitForUser = false;
	private boolean isLandingPage = true;
	private boolean isFinalPage = false;
	private int currentStep = 0;
	private String userInput = "";
	private int score = 0;
	PFont font;

	public void setup() {
		// initialize the setup including a hand cursor, the backscreen and font
		// to be used
		cursor(HAND);
		size(430, 520);
		background(255);
		//choose the font
		font = createFont("HanziPen SC", 16, true);

		// initialize pink frog at top left corner
		int x = 0, y = 0;
		initFrog(0, "pink", x, y);
		// initialize yellow frog at top right corner
		x = width / 2;
		initFrog(1, "yellow", x, y);
		// initialize blue frog at bottom left corner
		x = 0;
		y = height / 2;
		initFrog(2, "blue", x, y);
		// initialize green frog at bottom right corner
		x = width / 2;
		initFrog(3, "green", x, y);

		// call the loadAudioFiles() method to store the files into the array
		// 'command'
		loadAudioFiles();

		// call the welcome screen
		welcome();

	}

	private void showScore() {
		PImage ending = loadImage("Ending.png");
		// resize the image to better fit the screen
		ending.resize(430, 530);
		// print the image
		image(ending, 0, 0);
		// use the font declared earlier
		textFont(font, 30);
		// print the score
		text("Your score is : " + score + " out of " + commands.length + ".", 70, height / 2);
		text("Click anywhere to exit.", 80, height / 2 + 40);
	}

	private void welcome() {
		// Welcome screen image
		PImage welcome = loadImage("Frog_homepage.png");
		// resize the image to better fit the screen
		welcome.resize(430, 530);
		// print to screen
		image(welcome, 0, 0);
	}

	private void loadAudioFiles() {
		// create a new FilenameFilter object
		FilenameFilter filter = new FilenameFilter() {
			// look through file names but only accept if the name ends with
			// ".wav"
			public boolean accept(File dir, String name) {
				return name.endsWith(".wav");
			}
		};
		// from the folder data, import into array commands
		File folder = new File("src/data");
		commands = folder.listFiles(filter);
		// randomize the recordings
		Collections.shuffle(Arrays.asList(commands));

	}

	private void playSound(File file) {
		try {
			// get the audio info from the opened file
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
			// clip here is like an audio player
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
			Thread.sleep(clip.getMicrosecondLength() / 1000);

			// if there is an exception print the exception
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void draw() {
		// check if welcome page should be showing
		if (isLandingPage) {
			return;
		}
		
		//only print score when it's the final
		if (currentStep == commands.length) {
            if (!isFinalPage) {
                isFinalPage = true;
                // print the score
                showScore();
            }
			return;
		}

		// if the game reaches the end
		if (currentStep == commands.length) {
			// print the score
			showScore();
			return;
		}

		// wait for user input after computer has played
		if (waitForUser) {
			return;
		}

		// play the computer voice that recites the colors
		playSound(commands[currentStep]);
		waitForUser = true;
	}

	private void initFrog(int index, String color, int x, int y) {
		// this is a method to initialize the frog
		// check to make sure index is 0-4 or else it's invalid
		if (index < 0 || index >= frogs.length) {
			return;
		}
		// create a new frog
		frogs[index] = new Frog(new PVector(x, y), color, this);
	}

	public void mousePressed() {
		// find which frog was clicked
		int frogId = locateFrog();
		// did not click a frog
		if (frogId == -1) {
			return;
		}
		
		if (currentStep >= commands.length) {
			// if array is finished, exit
			System.exit(0);
			return;
		}

		// for the frog pressed, replace photo with singing frog photo
		frogs[frogId].wipeout(frogId);

	}

	public void mouseReleased() {
		// if on welcome page and user clicks, on release move to actual game
		if (isLandingPage) {
			isLandingPage = false;
			setupGameBoard();
			return;
		}

		// find which frog was released
		int frogId = locateFrog();
		// did not release a frog
		if (frogId == -1) {
			return;
		}
		// switch singing frog released with normal frog image
		frogs[frogId].wipeout();
		frogs[frogId].draw();
		// delay the singing frog image so it sings for a bit longer
		delay(250);

		// get string of frogIds that user inputted
		userInput += frogId;

		String currentCode = getCurrentCommandCode();
		System.out.println("currentCode = " + currentCode);
		System.out.println("userInput = " + userInput);

		// as long as user has not finished inputting, keep going
		// statement also check if frog entered was right
		if (userInput.length() < currentCode.length()) {
			return;
		}

		if (userInput.length() == currentCode.length() && currentCode.equals(userInput)) {
			// if input is right, give a point
			score++;
		}

		// reset the game board
		userInput = "";
		waitForUser = false;
		currentStep++;

	}

	private String getCurrentCommandCode() {
		String filename = commands[currentStep].getName();
		// remove .wav file extension
		return filename.substring(0, filename.length() - 4);
	}

	private int locateFrog() {
		if (mouseX > width / 2 && mouseY < height / 2) {
			// yellow frog is clicked
			return 1;
		} else if (mouseX < width / 2 && mouseY < height / 2) {
			// pink frog is clicked
			return 0;
		} else if (mouseX < width / 2 && mouseY > height / 2) {
			// blue frog is clicked
			return 2;
		} else if (mouseX > width / 2 && mouseY > height / 2) {
			// green frog is clicked
			return 3;
		}

		return -1;
	}

	// setup the gameboard after welcome screen
	private void setupGameBoard() {
		// make a white background
		fill(255);
		rect(0, 0, width, height);

		// draw all the frogs out
		for (int i = 0; i < frogs.length; i++) {
			frogs[i].draw();
		}
	}
}
