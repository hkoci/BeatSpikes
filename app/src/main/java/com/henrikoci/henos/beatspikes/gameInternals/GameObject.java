package com.henrikoci.henos.beatspikes.gameInternals;

/**
 * Created by Henri on 22/01/2018.
 */

//setup class as 'abstract' to be superclass to allow inheritance variables
public abstract class GameObject {
    protected int x; //This variable defines the position of the player in the x-coordinate
    protected int y; //This variable defines the position of the player in the y-coordinate
    protected int dy; //This variable defines the rate of movement of the player in the y axis
    protected int dx; //This variable defines the rate of movement of the player in the x axis
    protected int width; //This variable defines the width of the player bitmap
    protected int height; //This variable defines the height of the player bitmap


    public void setX(int x)
    {
        this.x = x;
    }
    //This setter is used to set the value of the x coordinate through the call of this method following a suitable parameter for the x coordinate
    public void setY(int y)
    {
        this.y = y;
    }
    //This setter is used to set the value of the y coordinate through the call of this method following a suitable parameter for the y coordinate
    public int getX()
    {
        return x;
    }
    //This getter is used to get the value of the x coordinate in this super class.
    public int getY()
    {
        return y;
    }
    //This getter is used to get the value of the y coordinate in this super class.
    public int getHeight()
    {
        return height;
    }
    //This getter is used to get the value of the height of the player in this super class.
    public int getWidth()
    {
        return width;
    }
    //This getter is used to get the value of the width of the player in this super class.
}

