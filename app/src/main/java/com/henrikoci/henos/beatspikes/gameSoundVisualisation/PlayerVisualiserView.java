package com.henrikoci.henos.beatspikes.gameSoundVisualisation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.henrikoci.henos.beatspikes.R;
import com.henrikoci.henos.beatspikes.gameInternals.GameViewPort;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;


public class PlayerVisualiserView extends View {

    /**
     * constant value for Height of the bar
     */
    public static final int VISUALIZER_HEIGHT = 250;

    /**
     * bytes array converted from file.
     */
    private byte[] bytes;

    /**
     * Percentage of audio sample scale
     * Should updated dynamically while audioPlayer is played
     */
    private float denseness;

    /**
     * Canvas painting for sample scale, filling played part of audio sample
     */
    private Paint playedStatePainting = new Paint();
    /**
     * Canvas painting for sample scale, filling not played part of audio sample
     */
    private Paint notPlayedStatePainting = new Paint();

    private int width;
    private int height;

    // Instance of GameViewPort
    GameViewPort gameClassInstance = new GameViewPort(getContext());

    // levelMap array moved to this class due to it not initialising.
    private int levelheight = gameClassInstance.getLevelHeight();
    private int levelWidth;// = gameClassInstance.getLevelWidth();

    public char[][] levelMap;// = new char[levelheight][levelWidth];

    //modal for analysis types
    AlertDialog analysisAlertDialog;

    //latency time to draw delay handler
    private Handler delayedHandler = new Handler();

    // analysis types modal sentences
    static CharSequence[] analysisType = new CharSequence[]{
            "75% of amplitude",
            "using preset genre",
            "any present amplitude",
    };

    public PlayerVisualiserView(Context context) {
        super(context);
        init();
    }

    public PlayerVisualiserView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //This code runs when the view is loaded on screen.
    private void init() {
        bytes = null;

        //Set draw colours
        playedStatePainting.setStrokeWidth(1f);
        playedStatePainting.setAntiAlias(true);
        playedStatePainting.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        notPlayedStatePainting.setStrokeWidth(1f);
        notPlayedStatePainting.setAntiAlias(true);
        notPlayedStatePainting.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        //Draw the visual bars using the media file imported
        updateVisualizer(fileToBytes(gameActivity.audioFile));

        //set progress bar to init. array
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sound generation");
        progressDialog.setMessage("Initialising Array");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Initialise the default game array which is blank (F floor and _ sky)
        initialiseGameArray();

        //Change message to spike generation state
        //progressDialog.setMessage("Analysing audio spectrum");

        //Show info
        delayedHandler.postDelayed(new Runnable() {
            public void run() {

                checkSpikeAnalysisType(); //perform spike detection

                progressDialog.cancel(); //close progress as modal will open
            }
        }, 200); //delay 200ms
    }


    public void checkSpikeAnalysisType(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Spike frequency detection");

        builder.setCancelable(false); //cannot close

        builder.setSingleChoiceItems(analysisType, -1, new DialogInterface.OnClickListener() { //modal select item from list defined in analysos type

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        performSpikeAnalysis(0); //75% amplitude
                        break;
                    case 1:
                        performSpikeAnalysis(1); //genre
                        break;
                    case 2:
                        performSpikeAnalysis(2); //any preset
                        break;
                }
                storeLevelMap(levelMap,"importMusicMap.beat"); //store levelMap in importMusicMap.beat

                Intent intent = new Intent("GameCanvasView.intent.action.Launch"); //launch intent to game class
                getContext().startActivity(intent); //start activity to game class

                ((Activity)getContext()).finish(); //close this activity, no need to have it open!
                analysisAlertDialog.dismiss(); //close dialog
            }
        });
        analysisAlertDialog = builder.create();
        analysisAlertDialog.show();

    }


    public void initialiseGameArray(){
        System.out.println("Level Height:" + levelheight); //height of level
        levelWidth = bytes.length; //width of level
        System.out.println("Level Width:" + bytes.length);

        levelMap = new char[levelheight][levelWidth];
        // Initialise levelMap to blank by adding "_" to each element in the Array
        // Key, "_" = Sky , "F" = Floor and "S" = Spike.

        for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {
            for ( int widthLoop = 0; widthLoop <levelWidth; widthLoop++) {


                if(heightLoop == levelheight-1 ) {
                    levelMap[heightLoop][widthLoop] = 'F'; // initialise default floor for each element on the first row

                }
                else{
                    levelMap[heightLoop][widthLoop] = '_';// initialise default sky for each element
                }
            }
        }
    }

    public void performSpikeAnalysis(int analysisType){
        int spriteHeight = gameClassInstance.getSpriteSizeHeight(); //get spriteSizeHeight from game class

        //case 0
        //Detect 75% of spikes
        if(analysisType==0){
            int maxPeakByte = 0;
            double percThreshold = 0.75;

            //loop through each bar in array
            for (int i = 0; i < bytes.length; i++){
                int numberOfSpikes = bytes[i]/spriteHeight; //scale down to height required

                if(numberOfSpikes > (maxPeakByte*percThreshold)){
                    maxPeakByte = numberOfSpikes; //set maximum peak to the largest amplitude
                }
            }

            //loop through every bar in array
            for (int i = 0; i < bytes.length; i++){
                int numberOfSpikes = bytes[i]/spriteHeight;

                if(numberOfSpikes >= maxPeakByte){
                    //System.out.println("num spikes" + numberOfSpikes);
                    for (int j = levelheight-2; j >= (levelheight - numberOfSpikes); j--){
                        levelMap[j][i] = 'S'; //place spike where the current bar is larger than the peakbyte
                        //System.out.println(levelMap[j][i]);
                    }
                }
            }

        }
        //case 1
        //Detect using genre of song
        else if(analysisType==1){
//TODO
        }
        //case 2
        //Detect all amplitude
        else if(analysisType==2) {
            for (int i = 0; i < bytes.length; i++){
                int numberOfSpikes = bytes[i]/spriteHeight;

                if(numberOfSpikes > 0){
                    //System.out.println("num spikes" + numberOfSpikes);
                    for (int j = levelheight-2; j >= (levelheight - numberOfSpikes); j--){
                        levelMap[j][i] = 'S'; //place spike where it is great than numberOfSpikes without condition
                        //System.out.println(levelMap[j][i]);
                    }
                }
            }
        }
        //No case given
        else{
            Log.e(TAG, "performSpikeAnalysis: No case given");
            //ERROR!
        }
    }


    /**
     * update and redraw Visualizer view
     */
    public void updateVisualizer(byte[] bytes) {
        this.bytes = bytes;
        invalidate();
    }

    /**
     * Update player percent. 0 - file not played, 1 - full played
     *
     * @param percent
     */
    public void updatePlayerPercent(float percent) {
        denseness = (int) Math.ceil(width * percent);
        if (denseness < 0) {
            denseness = 0;
        } else if (denseness > width) {
            denseness = width;
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bytes == null || width == 0) {
            return;
        }
        float totalBarsCount = width / dp(3);
        if (totalBarsCount <= 0.1f) {
            return;
        }
        byte value;
        int samplesCount = (bytes.length * 8 / 5);
        float samplesPerBar = samplesCount / totalBarsCount;
        float barCounter = 0;
        int nextBarNum = 0;

        int y = (height - dp(VISUALIZER_HEIGHT)) / 2;
        int drawBarCount;


        int barNum = 0;
        int lastBarNum;

        for (int a = 0; a < samplesCount; a++) {
            if (a != nextBarNum) {
                continue;
            }
            drawBarCount = 0;
            lastBarNum = nextBarNum;
            while (lastBarNum == nextBarNum) {
                barCounter += samplesPerBar;
                nextBarNum = (int) barCounter;
                drawBarCount++;
            }

            int bitPointer = a * 5;
            int byteNum = bitPointer / Byte.SIZE;
            int byteBitOffset = bitPointer - byteNum * Byte.SIZE;
            int currentByteCount = Byte.SIZE - byteBitOffset;
            int nextByteRest = 5 - currentByteCount;
            value = (byte) ((bytes[byteNum] >> byteBitOffset) & ((2 << (Math.min(5, currentByteCount) - 1)) - 1));
            if (nextByteRest > 0) {
                value <<= nextByteRest;
                value |= bytes[byteNum + 1] & ((2 << (nextByteRest - 1)) - 1);
            }

            for (int b = 0; b < drawBarCount; b++) {
                int x = barNum * dp(3);
                float left = x;
                float top = y + dp(VISUALIZER_HEIGHT - Math.max(1, VISUALIZER_HEIGHT * value / 31.0f));
                float right = x + dp(2);
                float bottom = y + dp(VISUALIZER_HEIGHT);

                if (x < denseness && x + dp(2) < denseness) {
                    canvas.drawRect(left, top, right, bottom, notPlayedStatePainting);
                } else {
                    canvas.drawRect(left, top, right, bottom, playedStatePainting);
                    if (x < denseness) {
                        canvas.drawRect(left, top, right, bottom, notPlayedStatePainting);
                    }
                }

                barNum++;
            }

        }
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getContext().getResources().getDisplayMetrics().density * value);
    }

    public static byte[] fileToBytes(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "fileToBytes: ERROR 0x00000002", e); //added logs for errors when files are not found to convert to bytes
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "fileToBytes: ERROR 0x0000000D", e);
            e.printStackTrace();
        }
        return bytes;
    }

    //store levelmap to data partition
    private void storeLevelMap(char[][] array,String filename){
        String levelMapCombinedString = ""; //string of level characters

        String lineSeperator = System.getProperty( "line.separator" ); //whitespace character
        //initialise size parameters at top of file
        levelMapCombinedString = "" + levelheight + " " + levelWidth + lineSeperator; //first line contains height and width parameters to be set to game class

        //add each individual element to the combined string
        for (int heightLoop = 0; heightLoop <levelheight; heightLoop++) {
            for ( int widthLoop = 0; widthLoop <levelWidth; widthLoop++) {
                levelMapCombinedString = levelMapCombinedString + levelMap[heightLoop][widthLoop];
            }
            levelMapCombinedString = levelMapCombinedString + lineSeperator;
        }

        //output file
        FileOutputStream outputStream;

        try {
            outputStream = gameClassInstance.getContext().openFileOutput(filename, Context.MODE_PRIVATE); //open file for writing
            outputStream.write(levelMapCombinedString.getBytes()); //write file to data partition using filename
            outputStream.close(); //close
        } catch (Exception e) {
            Log.e(TAG, "storeLevelMap: ERROR 0x0000001D", e);
            e.printStackTrace(); //show trace incase of error!
        }
    }

}

