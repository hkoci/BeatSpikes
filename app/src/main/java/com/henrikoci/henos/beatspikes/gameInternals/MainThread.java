package com.henrikoci.henos.beatspikes.gameInternals;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Henri on 22/01/2018.
 */

public class MainThread extends Thread
{
    private int FPS = 30; //The desired frames per second to be processed in a loop iteration.
    private double averageFPS; //To provide diagnostic information about the actual average frames shown per a second.
    private SurfaceHolder surfaceHolder; //Used to set surfaceHolder to the gameViewPort to be locked for canvas editing and then unlocked when the canvas pixel changes are processed.
    private GameViewPort GameViewPort; //Used to call the relevant update and draw methods so that the canvas and relevant classes can be updated when the loop increments.
    private boolean running; //This flag is used to stop the thread loop when the variable is set to false.
    // If the game requires to be paused or if the game has been quit, then the running state should be false to stop any updates to the game.

    public static Canvas canvas;
    /* Canvas canvas
     The canvas is referenced in the thread loop as it requires to be locked to allow the editing of the canvas and redrawn following the update
     of other methods used in the gameViewPort. The update routine in the gameViewPort performs adjustments to the classes requiring updates
     that are not drawn on the canvas (for example, the background’s x coordinates and the player’s y coordinates,
     through the method call of background.update and player.update).
     */

    //Sets the SurfaceHolder and GameViewPort variables in this class to the respective ones called by the method
    public MainThread(SurfaceHolder surfaceHolder, GameViewPort GameViewPort)
    {
        super(); //Initialise the parent class before initialising this class
        this.surfaceHolder = surfaceHolder; //set surfaceHolder to parent GameViewPort surfaceHolder through ref value passed
        this.GameViewPort = GameViewPort; //set GameViewPort to parent GameViewPort through ref value passed
    }

    //The loop functionality is present in this method and runs on every loop iteration
    @Override
    public void run()
    {
        long startTime; // time since loop started
        long timeMillis; // gets time spent in milliseconds, does this by substracting current time by start time and dividing by 1000000 (as time is in nanoseconds)
        long waitTime; // the time to wait is the time is target time minus the time spent (timeMillis)

        long totalTime = 0;
        /* Total time is required to measure the time spent since the thread has started to determine the average FPS.
        This is because the (totalTime/frameCount) would provide the average frame since the thread started.       */

        int frameCount =0; //counter that measures the current frame count during each loop increment
        long targetTime = 1000/FPS; // target time for loop to be in duration

        //while loop, when the thread has been called to start, the thread should start the game loop
        while(running) {
            startTime = System.nanoTime(); //set startTime to the time of the method call in nanoseconds
            canvas = null; // set canvas blank

            //try locking the canvas for pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas(); //lock canvas for editing
                // perform synchronized mutually-exclusive access between multiple threads for the canvas (incase of some other thread drawing)
                synchronized (surfaceHolder) {
                    this.GameViewPort.update(); //call update of the coordinate changes by calling relevant methods in the GameViewPort.update()
                    this.GameViewPort.draw(canvas); //draw bitmaps in draw method
                }
            } catch (Exception e) { //catch error here
            }
            finally{
                //if performed with no errors, try finally.

                //Precaution, assuming canvas contains some form of drawing
                if(canvas!=null)
                {
                    //try to unlock the canvas
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    //if not possible, debug stack in logcat
                    catch(Exception e){e.printStackTrace();}
                }
            }

            //Gets time spent in current loop (units in milliseconds), does this by subtracting current time by start time and dividing by 1000000 (as time is in nanoseconds).
            timeMillis = (System.nanoTime() - startTime) / 1000000;

            //The time to wait before the next loop is started is the time is target time minus the time spent (timeMillis).
            waitTime = targetTime-timeMillis;

            //try to sleep thread with waitTime
            try{
                this.sleep(waitTime);
            }catch(Exception e){} //not possible, trace the error in debug.

            /* !!! Section of code with the average frame per second count !!! */

            totalTime += System.nanoTime()-startTime;
            /* totalTime
             Total time is required to measure the time spent since the thread has started to determine the average FPS.
             This is because the (totalTime/frameCount) would provide the average frame since the thread started.
             */

            frameCount++; //A counter that measures the current frame count during each loop increment.

            if(frameCount == FPS) //when the actual frameCount counter reaches the desired FPS
            {
                averageFPS = 1000/((totalTime/frameCount)/1000000); //work out the time it took upto the actual frame drawn
                frameCount = 0; //reset the frameCount for next average check
                totalTime = 0; //reset the totalTime taken for the next avg. check
                System.out.println(averageFPS); //print out the FPS in logcat!
            }
        }
    }

    /* setRunning method
     Sets the running flag to a true/false state which can cause the desired effect
     to the loop (the loop has an if statement to detect if this flag is true to run)
     */
    public void setRunning(boolean b)
    {
        running=b; //set running state to boolean value passed
    }
}

