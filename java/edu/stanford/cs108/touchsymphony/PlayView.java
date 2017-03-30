package edu.stanford.cs108.touchsymphony;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import android.os.*;

import java.util.logging.Handler.*;

/**
 * Created by ruiaguiar1 on 3/27/17.
 */

public class PlayView extends View {
    PersistentSaver saver;
    private long startTime;
    private long currTime;
    private long epsilon = 40;
    private Context context;
    private Paint colorPaint;
    private AnimatableRectF mCurrentRect;
    private Canvas canvas;


    public PlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        saver = PersistentSaver.getInstance(context);
        this.context=context;
        setWillNotDraw(false);
        setWillNotCacheDrawing(false);


    }

    @Override
    public void onDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        this.canvas=canvas;
        //Draws current rectangle
        if(mCurrentRect !=null&& colorPaint!=null) {

            canvas.drawOval(mCurrentRect, colorPaint);

        }
    }

    public void playSong(){
        //Get values from SQL database
        String song = saver.getCurrSongName();
       final ArrayList<Long> Times = saver.getTimesForNotes(song);
        final ArrayList<Float> xCoordinates = saver.getPressXCoordinates(song);
        final ArrayList<Float> yCoordinates = saver.getPressYCoordinates(song);
        final ArrayList<String> notes = saver.getNotesForSong(song);
        //play note with that x and y coordinate and draw circle for each note
        for(int i=0;i<Times.size();i++) {
            Handler mainHandler = new Handler(context.getMainLooper());
            final int x = i;
            mainHandler.postDelayed(new Runnable() {
                public void run() {
                    playNote(xCoordinates.get(x), yCoordinates.get(x), notes.get(x));
                }
            }, Times.get(i));

        }
    }

    //Generates color based on the press x and y.
    private int generateColor(float pressx, float pressy){
        int Red = (((int) (pressx%255)) << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        int Green = (((int) (pressy%255)) << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        int Blue = ((int)((pressx+pressy)%255)) & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.

    }

    //Plays song from media
    private void playSound(String noun){
        Resources res = context.getResources();
        int resID = res.getIdentifier(noun, "raw", context.getPackageName());
        MediaPlayer mediaPlayer = MediaPlayer.create(context,resID);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

    //Plays sound and draws circle

    private void playNote(float pressx, float pressy, String note){
        colorPaint=new Paint();
        colorPaint.setStyle(Paint.Style.STROKE);
        colorPaint.setStrokeWidth(10);
        colorPaint.setColor(generateColor(pressx,pressy));
        float translateX = 50.0f;
        float translateY = 50.0f;
        Random rand = new Random(10000);
        int xr = rand.nextInt();
        int yr = rand.nextInt();
        int startColor = generateColor(pressx,pressy);
        int midColor = generateColor(pressx+rand.nextInt(),pressy+rand.nextInt());
        int endColor = generateColor(pressx+xr,pressy+yr);
        mCurrentRect = new AnimatableRectF(pressx-50,pressy-50,pressx+50,pressy+50);

        DrawThread thread = new DrawThread(startColor,midColor,endColor,translateX,translateY);
        thread.run();
        playSound(note);
    }

    //Thread to draw circles

    class DrawThread implements Runnable{
        private int startColor;
        private int endColor;
        private int midColor;
        private float translateX;
        private float translateY;
        public DrawThread(int startColor, int midColor, int endColor, float translateX, float translateY){
            this.startColor=startColor;
            this.endColor=endColor;
            this.midColor=midColor;
            this.translateX=translateX;
            this.translateY=translateY;
        }

        public void run(){
            playAnimation(startColor,midColor,endColor, translateX, translateY);
        }
    }

    //Draws the circle
    private void playAnimation(int startColor, int midColor, int endColor, float translateX, float translateY){
        ObjectAnimator animateLeft = ObjectAnimator.ofFloat(mCurrentRect, "left", mCurrentRect.left, mCurrentRect.left-translateX);
        ObjectAnimator animateRight = ObjectAnimator.ofFloat(mCurrentRect, "right", mCurrentRect.right, mCurrentRect.right+translateX);
        ObjectAnimator animateTop = ObjectAnimator.ofFloat(mCurrentRect, "top", mCurrentRect.top, mCurrentRect.top-translateY);
        ObjectAnimator animateBottom = ObjectAnimator.ofFloat(mCurrentRect, "bottom", mCurrentRect.bottom, mCurrentRect.bottom+translateY);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator animateColor = ObjectAnimator.ofObject(this, "color", evaluator, startColor,midColor,endColor);
        animateBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //TODO: DEBUG ANIMATOR
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                postInvalidate();
            }
        });
        AnimatorSet rectAnimation = new AnimatorSet();
        rectAnimation.playTogether(animateLeft, animateRight, animateTop, animateBottom,animateColor);
        rectAnimation.setDuration(1000).start();
        postInvalidate();


    }

    //For paint animation
    public void setColor(int color) {
        colorPaint.setColor(color);
        postInvalidate();
    }

    //Getters and setters


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }

}
