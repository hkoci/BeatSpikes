package com.henrikoci.henos.beatspikes.gameInternals;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Henri on 22/01/2018.
 */

public class MainThread extends Thread
{
    private int FPS = 60;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GameViewPort GameViewPort;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameViewPort GameViewPort)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.GameViewPort = GameViewPort;
    }
    @Override
    public void run()
    {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            //try locking the canvas for pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.GameViewPort.update();
                    this.GameViewPort.draw(canvas);
                }
            } catch (Exception e) {
            }
            finally{
                if(canvas!=null)
                {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){e.printStackTrace();}
                }
            }




            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime-timeMillis;

            try{
                sleep(waitTime);
            }catch(Exception e){}

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if(frameCount == FPS)
            {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount =0;
                totalTime = 0;
                //System.out.println(averageFPS);
            }
        }
    }
    public void setRunning(boolean b)
    {
        running=b;
    }
}