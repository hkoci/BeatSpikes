package com.henrikoci.henos.beatspikes.gameInternals;

/**
 * Created by Henri on 22/01/2018.
 */

import android.graphics.Bitmap;


public class Player extends GameObject{
    //private Bitmap spritesheet;
    public Bitmap playerImage;
    private int score;
    private int gravity = 10;
    private int groundForce = 0;
    private int upForce;
    private boolean up;
    private boolean playing;
    private boolean jumped = false;
   // private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames, int xPos) {
        x = xPos*w;
        y = GameViewPort.HEIGHT / 2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        playerImage = res;
        /*
        //Bitmap[] image = new Bitmap[numFrames];
        //spritesheet = res;

        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
*/
    }

    public void setUp(boolean b){
        up = b;
    }

    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed>100)
        {
            score++;
            startTime = System.nanoTime();
        }
       // animation.update();
        //System.out.println("" + up);
        if(up && !jumped) {
            upForce = 20;
            jumped = true;
        }

        if(upForce > 0){
            upForce--;
        }

        if(groundForce == 10){
            jumped = false;
        }

        //test if insercting with block or floor
        //if true
        //groundForce = gravity
/*
        if(y-(GameViewPort.HEIGHT - height) > 0){
            groundForce = gravity;
        }else{
            groundForce = 0;
        }
*/

        //System.out.println(groundForce +  " " +  gravity + " " + height);

        //if intersect wioth ground
         //   groundForce = gravity
        //else
         //   groundForce = 0

        y = y - upForce + gravity - groundForce;

    }

    public int returnPlayerY(){
        System.out.println("Y is returned" + y);
        return y;
    }

    public int returnPlayerX(){
        System.out.println("X is returned:" + x);
        return x;
    }

    public void setGroundForceOn(){
        groundForce = gravity;
        //System.out.println("Groundforce is on");
    }

    public void setGroundForceOff(){
        groundForce = 0;
        //System.out.println("Groundforce is off");
    }




   // public void draw(Canvas canvas)
   // {
   //     canvas.drawBitmap(animation.getImage(),x,y,null);
   // }

    public Bitmap getImage(){
        //return animation.getImage();
        return playerImage;
    }
    public int getScore(){return score;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean b){playing = b;}
    public void resetDY(){dy = 0;}
    public void resetScore(){score = 0;}
}