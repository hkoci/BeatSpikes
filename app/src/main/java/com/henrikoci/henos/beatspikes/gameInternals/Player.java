package com.henrikoci.henos.beatspikes.gameInternals;

/**
 * Created by Henri on 22/01/2018.
 */

import android.graphics.Bitmap;


public class Player extends GameObject{
    //bitmap containing the character image
    public Bitmap playerImage;

    //score variable (incremental on time, reset on exit of Player class)
    private int score;

    // The force applied to the character to decrease the y coordinate of the player when the character requires to be falling.
    private int gravity = 10;

    // The force applied to the character when the floor has been hit, this is set to substract the gravity force so that the forces are equalised.
    private int groundForce = 0;

    // The force applied when the character jumps.
    private int upForce;

    //up state is check if player has tapped screen
    private boolean up;

    //playing state (is the game playing?)
    private boolean playing;

    //flag if player is current in the jumped state.
    private boolean jumped = false;

    //start time of elapsed duration
    private long startTime;

    /* Player Method
     Defines the parameters of the positioning, sizing and the number of frames required for the bitmap to be drawn.
     When it is called, the parameters are used to initialise the image array and insert the bitmap image frames to that array.
    */
    public Player(Bitmap res, int w, int h, int numFrames, int xPos) {
        x = xPos*w; //The x position of the player
        y = GameViewPort.HEIGHT / 2; //The y position of the player
        dy = 0; // rate of change in y axis
        score = 0; //score of elapsed time
        height = h; //height of player sprite
        width = w; //width of player sprite

        playerImage = res; //set the bitmap image of the player to the parameter res

    }

    //method to set the jumped state (up) to true/false through it's parameter
    public void setUp(boolean b){
        up = b;
    }

    /* Update Method
     The update method is called when the GameViewPort update method is called.
     This allows the player’s y coordinates to be updated with respective to the jumped state.
     The class also updates the animation of the character through the animation class.
     */
    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        //The duration of time the Player class has running since it was started, there is a check using this variable to increment the score

        //If the duration of time spent between the current system time and the start time is greater than 100
        if(elapsed>100)
        {
            score++;
            startTime = System.nanoTime();
        }

        //if the screen is tapped but is not currently in a jumped state
        if(up && !jumped) {
            upForce = 20; //set the upForce to 22 (this value seemed to give enough force) to allow character to jump
            jumped = true; //set jumped flag to true to prevent further mid-air jumps
        }

        //on every update draw, lower the upForce amount until there is no upForce force.
        if(upForce > 0){
            upForce--;
        }

        //if the groundForce is equal to gravity, reset the jumped state
        if(groundForce == 10){
            jumped = false;
        }

        //set y coordinate to the forces applied to the character
        y = y - upForce + gravity - groundForce;

    }

    //returns the y co-ordinate of the player
    public int returnPlayerY(){
        System.out.println("Y is returned" + y);
        return y;
    }


    //sets groundForce on, by equaling groundForce gravity
    public void setGroundForceOn(){
        groundForce = gravity;
        //System.out.println("Groundforce is on");
    }

    //Reset groundForce to zero
    public void setGroundForceOff(){
        groundForce = 0;
        //System.out.println("Groundforce is off");
    }

    //returns the current bitmap image in this class
    public Bitmap getImage(){
        return playerImage;
    }

    //Reusability, returns score
    public int getScore(){return score;}

    //Retruns state if the game is currently active
    public boolean getPlaying(){return playing;}

    //Sets state if game requires to be paused or played
    public void setPlaying(boolean b){playing = b;}

    //Reusability feature, to reset dy
    public void resetDY(){dy = 0;}

    //Reusability feature, to reset score
    public void resetScore(){score = 0;}
}