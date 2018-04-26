package com.henrikoci.henos.beatspikes.gameInternals;

// Required

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.henrikoci.henos.beatspikes.R;
import com.henrikoci.henos.beatspikes.gameSoundVisualisation.gameActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

public class GameViewPort extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 500;
    /* WIDTH
This variable contains the integer value that width should be when defined by the programmer.
This variable is used in the scaling of the current Canvas width to the desired width
through the use of being the multiplier in the scaleFactorX variable.
 */
    public static final int HEIGHT = 300;
    /* HEIGHT
This variable contains the integer value that height should be when defined by the programmer.
This variable is used in the scaling of the current Canvas height to the desired height
through the use of being the multiplier in the scaleFactorY variable.
 */
    private MainThread thread;
    /* thread
The thread object is used to instantiate the MainThread class which contains the thread to loop the game.
This is used in the game class to set a flag to tell the thread that the game is running so that the thread can start.
 */
    private Player player;
    /*
This player object is used in the instantiation to set the Bitmap in the player class to the character image.
In addition to this, it also is used to: call the draw method onto the canvas within this game class, set the jumped state
and get these states.
 */
    private int SpriteSizeWidth = 25; //scaled sprite to size requirements of device
    private int SpriteSizeHeight = 25; //scaled sprite to size requirements of device
    private int levelWidth;// = songLength/SpriteSizeWidth; no longer needed, sent by txt file!
    private int levelheight = HEIGHT/SpriteSizeHeight; //scaled height of level array
    private char[][] levelMap; //level map
    private final int playerDrawX = WIDTH/(2*SpriteSizeWidth); //DrawX coordinate of player on screen!

    private int topLeftViewPortScroller = 0; //top scrolling left view port
    private int playerXInArray = topLeftViewPortScroller + playerDrawX; //X position of player in array

    private Paint paintSky, paintFloor, paintSpike; //Paint colours for blocks

    private boolean gameState = true; //game playing state on!

    public GameViewPort(Context context)
    {

        //Sets the context to the parent class, this is needed to initialise the parent class before initialising this class
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        //starts the mainthread process by setting the surfaceholder and GameViewPort references to this class
        thread = new MainThread(getHolder(), this);

        //make GameViewPort focusable so it can handle events
        setFocusable(true);

    }

    //songLength in player visualiser now!
    public void setSongLength(int length){
        //songLength = length;
    }

    //Adds coordinate to levelMap but no longer needed but kept for flexibility
    public void addToLevelMap(int y, int x, char C){
        levelMap[y][x] = C;
    }

    public int getLevelHeight(){ //gets level height
        return this.levelheight;
    }

    public int getLevelWidth(){
        return this.levelWidth; //gets level width
    }

    public int getSpriteSizeHeight(){
        return this.SpriteSizeHeight; //gets level height scaled sprite
    }

    public int getSpriteSizeWidth(){
        return this.SpriteSizeWidth; //gets level wdith scaled width
    }

    public char[][] getLevelMap(){
        return levelMap; //gets level map
    }


    //generate level map code
    public void generateLevelMap(Context context){

        try{
            InputStream inputStream = context.openFileInput("importMusicMap.beat"); //read file
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); //make new inputstream

            BufferedReader br = new BufferedReader(inputStreamReader); //buffered reader for reading line by line

            String currentLine=br.readLine(); //read first line
            String[] size = currentLine.split(" "); //split at whitespace
            levelWidth =  Integer.parseInt(size[1]); //last char is width
            levelheight = Integer.parseInt(size[0]); //first char is height

            levelMap = new char[levelheight][levelWidth]; //set levelmap to params
            int i = 0; //loop for reading whole txt.
            while((currentLine = br.readLine())!=null){
                //System.out.println(currentLine);
                levelMap[i] = currentLine.toCharArray();
                i++;
            }
        }catch(IOException e){
            // I/O error, reports error in console logcat
            Log.e(TAG, "generateLevelMap: ERROR 0x00000002", e);
        }finally{
            if(levelMap!=null){
                gameState = true;
                //System.out.println(Arrays.deepToString(levelMap));
            }
            else{
                Log.e(TAG, "generateLevelMap: ERROR 0x0000000D");
            }

        } //end try-catch-finally
    }

    //print level map
    public String printLevelMap() {
        for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {
            for (int widthLoop = 0; widthLoop < levelWidth; widthLoop++) {
                System.out.print(levelMap[heightLoop][widthLoop]); //loop each element individually and then print
            }
            System.out.println(); //format on new line when reach EOF
        }
        return "levelMap printed";
    }

    //play music file
    private void playMusicFile(String musicFile, int musicType) throws IOException {
        MediaPlayer mp = new MediaPlayer();
        mp.setDataSource(musicFile);
        mp.prepare();
        //musicType 1 = play
        if(musicType == 1){
            mp.start();
        }
        //musicType 0 = pause
        else if(musicType == 0){
            mp.stop();
        }
    }

    //Default method created by IDE due to use of SurfaceHolder, left for any future advancements.

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    //This method is run when the surface is destroyed (e.g. application terminates or activity is left)
    //This is called immediately after a surface is being destroyed.

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
                /* retry
          This flag determines if the thread has been successfully stopped or not.
          If the MainThread has had the flag for the running state to false, then this retry flag is also set to false
          to stop the while loop from continuing. This is to prevent further iterations which cause redundant calls
          if the thread has been paused before the counter increments to 1000
          for example, the MainThread, may have used a FPS of 30, 1000/30=33.33333) and this would have taken a much shorter time to pause.
          The worst case scenario of the thread iterating is when the FPS value is 1, where 1000/1=1000.
         */

        int counter = 0;
                /* counter
          This counter is used as incremental check in a loop to allow enough time for the thread to process the thread to stop running.
          The MainThread uses a ‘targetTime’ variable of 1000/FPS, with the maximum value being 1000, the counter will loop up to 1000 to try
          and set the running state flag to false so that the MainThread currently running will stop.
         */

        //pauses any music playing
        try {
            playMusicFile(String.valueOf(gameActivity.audioFile),0);
        } catch (IOException e) {
            Log.e(TAG, "playMusicFile (pause): ERROR 0x00000002");
            e.printStackTrace();
        }
        //checks if the retry is true and counter is below 1000, this is the condition that the thread is still running
        while(retry && counter<1000)
        {
            counter++; //increment the counter on each iteration
            try{thread.setRunning(false); //try to set the running state of the thread to false (this prevents the loop thread from running)
                thread.join(); //the thread.join method waits for this thread to die.
                retry = false; //sets the retry state to false, the thread has been killed!

            }catch(InterruptedException e){e.printStackTrace();}
            //catch method produces any debug stack traces incase there is an error with this functionality!
        }

    }

    //This is called immediately after the surface is first created.
    @Override
    public void surfaceCreated(SurfaceHolder holder){

        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.box25), SpriteSizeWidth, SpriteSizeHeight, 1, playerDrawX); // instantiate player with player image

        //This player object is used in the instantiation to set the Bitmap in the player class to the helicopter character image.
        //It contains the parameters of the width and height including the number of frames in that spritesheet bitmap

        initPaints();

        generateLevelMap(getContext());

        printLevelMap();

        //we can safely start the game loop as the images have been initialised and set!

        thread.setRunning(true);
        //sets the thread running state to true to allow the thread to start

        thread.start();
        //starts the thread!
        //...
        //Game on!!!

        //play the audio file from gameActivity (fileImport activity)
        try {
            playMusicFile(String.valueOf(gameActivity.audioFile),1);
        } catch (IOException e) {
            Log.e(TAG, "playMusicFile (play): ERROR 0x00000002");
            e.printStackTrace(); //log and trace if bug.
        }

    }

    /* onTouchEvent method (OVERVIEW)
 This method has been created to detect the presence of TOUCH input action from the user.
 It detects whether the user has tapped the screen to allow the game to start (setting player.setPlaying to true)
 and (setting player.setUp to true) to make the character jump. When the user has stopped touching the screen,
 the player.setUp is set to false to stop jumping.
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) //when the screen has been touched, start the following
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){ //if the finger has been pressed on the digitizer
            //Start the game
            if(!player.getPlaying() && gameState)
            {
                //If the game is not in its playing state, set it to the playing state!
                player.setPlaying(true);
            }
            else if(player.getPlaying() && gameState)
            {
                /*
                Collision detection for floor

                Sets players coordinates back up if on ground to allow jumping again
                */
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
        //if the finger is no longer present in the digitizer
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false); //stop jumping!
            return true; //need to return a value, therefore, return true for input
        }

        return super.onTouchEvent(event); //if the motionevent is neither Action_Up or Action_Down, then return the event.
    }

    /* update method (OVERVIEW)
 This update method calls the background and player classes to update, this allows the movement of these bitmaps on the screen
  to update when the update method is called.

  It is called by the MainThread when the game loop runs.
 */
    public void update() {
        // if the game is being played
        if(player.getPlaying()) {

            //update player coordinates!
            player.update();

            //let viewport move by 15
            topLeftViewPortScroller = topLeftViewPortScroller + 15;

            //Variables used to detect if player is in sprite collision
            int playerYcoordinate = player.y; //y coord
            int checkCoordinate = (playerYcoordinate/SpriteSizeHeight)+1; //if coord infront offset
            playerXInArray = (topLeftViewPortScroller/SpriteSizeWidth) + playerDrawX ; // x coord
            int checkCoordinateNoOffset = (playerYcoordinate/SpriteSizeHeight); //coord on with no offset

            if(levelMap[checkCoordinate][playerXInArray]=='F'){ //hits floor
                //System.out.println("Gravity is on");
                player.setGroundForceOn();
            }
            else{
                //System.out.println("Gravity is off");
                player.setGroundForceOff();
            }

            //Oops! Player has touched spike..
            if(levelMap[checkCoordinateNoOffset][playerXInArray] == 'S'){
                player.setPlaying(false); //stops game scroller at position
                player.setUp(false); //disallow any jumps to the player position
                gameState = false;
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
                /* scaleFactorX
         This variable contains the scaling value that the width of the canvas should be when defined by the programmer.
         This variable is used in the scaling of the current Canvas width to the desired width (second variable).
         This variable is then used to set the scaling of the canvas (in X axis) in the canvas.scale(scaleFactorX AND scaleFactorY)
         */
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
                /* scaleFactorY
         This variable contains the scaling value that the height of the canvas should be when defined by the programmer.
         This variable is used in the scaling of the current Canvas height to the desired height (first variable).
         This variable is then used to set the scaling of the canvas (in Y axis) in the canvas.scale(scaleFactorX AND scaleFactorY)
         */

        //If there is anything drawn on the canvas (validation check to try and verify that the canvas is present as it has been drawn atleast once!(
        if (canvas != null) {

            //Scales the canvas to the desired width and height using the converted scale factor of the programmers desired sizing parameters
            canvas.scale(scaleFactorX, scaleFactorY);

            //for drawable x region
            int drawX = topLeftViewPortScroller/SpriteSizeWidth;
            for ( int widthLoop = drawX; widthLoop <levelWidth+drawX; widthLoop++) {
                for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {

                    //params needed for drawing of rectangle using offset positioning
                    int rectLeftEdge = (widthLoop-drawX)*SpriteSizeWidth;
                    int rectTopEdge = heightLoop*SpriteSizeHeight;

                    //draw block depending on character found
                    switch(levelMap[heightLoop][widthLoop]){
                        case 'S': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintSpike);break;
                        case '_': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintSky);break;
                        case 'F': canvas.drawRect(rectLeftEdge,rectTopEdge, rectLeftEdge+SpriteSizeWidth, rectTopEdge+SpriteSizeHeight , paintFloor);
                    }

                    //draw player on canvas
                    canvas.drawBitmap(player.playerImage,player.x,player.y,null);

                }
            }
        }
    }

}

