package com.henrikoci.henos.beatspikes.gameInternals;

// Required
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.henrikoci.henos.beatspikes.R;

public class GameViewPort extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 500; //width of ViewPort
    public static final int HEIGHT = 300; //height of ViewPort
    private MainThread thread; // instantiate MainThread class of program as 'thread'
    private Player player; // instantiate player class as 'player'
    private int SpriteSizeWidth = 25;
    private int SpriteSizeHeight = 25;
    private int songLength = 5000;
    private int levelWidth = songLength/SpriteSizeWidth;
    private int levelheight = HEIGHT/SpriteSizeHeight;
    private char[][] levelMap = new char[levelheight][levelWidth];
    private final int playerDrawX = WIDTH/(2*SpriteSizeWidth);

    private int topLeftViewPortScroller = 0;
    private int playerXInArray = topLeftViewPortScroller + playerDrawX;

    private Paint paintSky, paintFloor, paintSpike;

    private boolean gameState = true;


    public GameViewPort(Context context)
    {

        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make GameViewPort focusable so it can handle events
        setFocusable(true);

    }

    public void setSongLength(int length){
        songLength = length;
    }

    public void addToLevelMap(int y, int x, char C){
        levelMap[y][x] = C;
    }

    public int getLevelHeight(){
        return this.levelheight;
    }

    public int getLevelWidth(){
        return this.levelWidth;
    }

    public char[][] getLevelMap(){
        return levelMap;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder){

       // bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1)); // instantiate background with background image
       // bg.setVector(MOVESPEED); // set vector movement with MOVESPEED variable defined at top of code
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.box25), SpriteSizeWidth, SpriteSizeHeight, 1, playerDrawX); // instantiate player with player image

        initPaints();

        // Initialise levelMap to blank by adding "_" to each element in the Array
        // Key, "_" = Sky , "F" = Floor and "S" = Spike.
/*
        for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {
            for ( int widthLoop = 0; widthLoop <levelWidth; widthLoop++) {


                if(heightLoop == levelheight-1 ) {
                    levelMap[heightLoop][widthLoop] = 'F'; // initialise default floor for each element on the first row

                }
                else{
                    levelMap[heightLoop][widthLoop] = '_'; // initialise default sky for each element
                }
            }
        }*/

        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) //when the screen has been touched, start the following
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && gameState)
            {
                player.setPlaying(true);
            }
            else
            {
                //prevent double jumps when not on floor by only allowing jumping if player is on floor.
                int playerYcoordinate = player.y;
                int checkCoordinate = (playerYcoordinate/SpriteSizeHeight)+1;
                playerXInArray = (topLeftViewPortScroller/SpriteSizeWidth) + playerDrawX ;
                int checkCoordinateNoOffset = (playerYcoordinate/SpriteSizeHeight);
                if(levelMap[checkCoordinateNoOffset][playerXInArray] == '_' && levelMap[checkCoordinate][playerXInArray] == 'F'){
                    player.setUp(true);
                }
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            gameState = false;
        }

        return super.onTouchEvent(event);
    }

    public void update() {
        if(player.getPlaying()) {

            player.update();

            topLeftViewPortScroller = topLeftViewPortScroller + 15;

            int playerYcoordinate = player.y;
            int checkCoordinate = (playerYcoordinate/SpriteSizeHeight)+1;
            playerXInArray = (topLeftViewPortScroller/SpriteSizeWidth) + playerDrawX ;
            int checkCoordinateNoOffset = (playerYcoordinate/SpriteSizeHeight);

            if(levelMap[checkCoordinate][playerXInArray]=='F'){
                //System.out.println("Gravity is on");
                player.setGroundForceOn();
            }
            else{
                //System.out.println("Gravity is off");
                player.setGroundForceOff();
            }

            if(levelMap[checkCoordinateNoOffset][playerXInArray] == 'S'){
                player.setPlaying(false); //stops game scroller at position
                player.setUp(false); //disallow any jumps to the player position
            }

        }

    }

    private void initPaints(){
        //set paint fill for sky
        paintSky = new Paint();
        paintSky.setColor(Color.GREEN);
        paintSky.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSky.setStrokeWidth(10);

        //set paint fill for floor
        paintFloor = new Paint();
        paintFloor.setColor(Color.RED);
        paintFloor.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFloor.setStrokeWidth(10);

        //set paint fill for spikes
        paintSpike = new Paint();
        paintSpike.setColor(Color.YELLOW);
        paintSpike.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSpike.setStrokeWidth(10);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);

        if (canvas != null) {

            canvas.scale(scaleFactorX, scaleFactorY);

            int drawX = topLeftViewPortScroller/SpriteSizeWidth;
            for ( int widthLoop = drawX; widthLoop <levelWidth+drawX; widthLoop++) {
                for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {

                    int rectLeftEdge = (widthLoop-drawX)*SpriteSizeWidth;
                    int rectTopEdge = heightLoop*SpriteSizeHeight;

                    switch(levelMap[heightLoop][widthLoop]){
                        case 'S': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintSpike);break;
                        case '_': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintSky);break;
                        case 'F': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintFloor);
                    }

                canvas.drawBitmap(player.playerImage,player.x,player.y,null);

                }
            }
        }
    }

}