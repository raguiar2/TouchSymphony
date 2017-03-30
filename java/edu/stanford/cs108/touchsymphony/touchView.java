package edu.stanford.cs108.touchsymphony;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.animation.*;

import java.util.Random;

/**
 * Created by ruiaguiar1 on 3/13/17.
 */

public class touchView extends View {
    private PersistentSaver saver;
    private  boolean hideIntro;
    private Context context;
    private Canvas canvas;
    private float pressx;
    private float pressy;
    private AnimatableRectF mCurrentRect;
    private Paint colorPaint;
    private boolean recording;
    private boolean containsNote;
    private long starttime;
    private long currtime;
    private String currnote;




    public touchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hideIntro=true;
        recording=false;
        this.context=context;
        saver = PersistentSaver.getInstance(context);
        currtime=System.currentTimeMillis();
    }

    //Draws rect and inserts.
    @Override
    public void onDraw(Canvas canvas){
        this.canvas=canvas;
        super.onDraw(canvas);
        if(mCurrentRect !=null&& colorPaint!=null)
        canvas.drawOval(mCurrentRect,colorPaint);
        if(recording && !containsNote){
            containsNote=true;
            saver.insertNote(currnote,pressx,pressy,currtime);
        }
    }

    //Generates color based on the press x and y.
    private int generateColor(float pressx, float pressy){
        int Red = (((int) (pressx%255)) << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        int Green = (((int) (pressy%255)) << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        int Blue = ((int)((pressx+pressy)%255)) & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.

    }

    //Plays specified note
    private void playSound(){
        Resources res = context.getResources();
        String noun = getNounFromPosition();
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



    private String getNounFromPosition() {
        Spinner insturmentList =(Spinner) ((ComposeActivity) getContext()).findViewById(R.id.instruments_spinner);
        String insturment = insturmentList.getSelectedItem().toString();
        String result= "";
        //TODO: FILL OUT METHODS WITH SOUNDS AND SPELL CHECK GUTIAR
        if(insturment.equals("Piano")){
        result = getPianoKey();
        }
        else if (insturment.equals("Drum")){
            result=getDrumSound();

        }
        else if (insturment.equals("Electric Guitar")){
            result=getElectricGutiarSound();

        }
        else if (insturment.equals("Bass")){
            result=getBassSound();
        }
        currnote=result;
        return result;
    }

    //Gets correct note from res folder based on user selection of insturment

    private String getPianoKey(){
        String result;
        if(0<pressy && pressy<this.getHeight()/8) result="b1";
        else if(this.getHeight()/8<pressy && pressy<2*this.getHeight()/8) result="a1";
        else if(pressy>2*this.getHeight()/8&& pressy<3*this.getHeight()/8) result="g1";
        else if(pressy>3*this.getHeight()/8&& pressy<4*this.getHeight()/8) result="d1";
        else if(pressy>4*this.getHeight()/8&& pressy<5*this.getHeight()/8) result="e1";
        else if(pressy>5*this.getHeight()/8&& pressy<6*this.getHeight()/8) result="f1";
        else result="c1";
        return result;
    }

    private String getDrumSound(){
        String result;
        if(0<pressy && pressy<this.getHeight()/8) result="cowbell_drum";
        else if(this.getHeight()/8<pressy && pressy<2*this.getHeight()/8) result="clap_drum";
        else if(pressy>2*this.getHeight()/8&& pressy<3*this.getHeight()/8) result="clave_drum";
        else if(pressy>3*this.getHeight()/8&& pressy<4*this.getHeight()/8) result="crash_drum";
        else if(pressy>4*this.getHeight()/8&& pressy<5*this.getHeight()/8) result="hihat_drum";
        else if(pressy>5*this.getHeight()/8&& pressy<6*this.getHeight()/8) result="kickdrum";
        else result="rim_drum";
        return result;
    }

    private String getElectricGutiarSound(){
        String result;
        if(0<pressy && pressy<this.getHeight()/8) result="electricfopen";
        else if(this.getHeight()/8<pressy && pressy<2*this.getHeight()/8) result="electricg";
        else if(pressy>2*this.getHeight()/8&& pressy<3*this.getHeight()/8) result="electriccmuted";
        else if(pressy>3*this.getHeight()/8&& pressy<4*this.getHeight()/8) result="electricdmuted";
        else if(pressy>4*this.getHeight()/8&& pressy<5*this.getHeight()/8) result="electricgmuted";
        else if(pressy>5*this.getHeight()/8&& pressy<6*this.getHeight()/8) result="electricbmuted";
        else result="electricamuted";
        return result;
    }

    private String getBassSound(){
        String result;
        if(0<pressy && pressy<this.getHeight()/8) result="bassa";
        else if(this.getHeight()/8<pressy && pressy<2*this.getHeight()/8) result="bassb";
        else if(pressy>2*this.getHeight()/8&& pressy<3*this.getHeight()/8) result="bassc";
        else if(pressy>3*this.getHeight()/8&& pressy<4*this.getHeight()/8) result="bassd";
        else if(pressy>4*this.getHeight()/8&& pressy<5*this.getHeight()/8) result="basse";
        else if(pressy>5*this.getHeight()/8&& pressy<6*this.getHeight()/8) result="bassf";
        else result="bassglow";
        return result;
    }

    public void setColor(int color) {
        colorPaint.setColor(color);
        invalidate();
    }
    class DrawThread implements Runnable{
        private int startColor;
        private int endColor;
        private int midColor;
        private float translateX;
        private float translateY;
        private AnimatableRectF mCurrentRect;
        public DrawThread(int startColor, int midColor, int endColor, float translateX, float translateY, AnimatableRectF mCurrentRect){
            this.startColor=startColor;
            this.endColor=endColor;
            this.midColor=midColor;
            this.translateX=translateX;
            this.translateY=translateY;
            this.mCurrentRect=mCurrentRect;
        }

        public void run(){
            animateObject(startColor,midColor,endColor, translateX, translateY,mCurrentRect);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(hideIntro){
            hideIntro=false;
            TextView introView =(TextView) ((ComposeActivity) getContext()).findViewById(R.id.openText);
            introView.setVisibility(View.INVISIBLE);
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                containsNote=false;
                pressx=event.getX();
                pressy=event.getY();
                if(recording) currtime = System.currentTimeMillis()-starttime;
                AnimatableRectF mCurrentRect = new AnimatableRectF(pressx-50,pressy-50,pressx+50,pressy+50);
                Paint colorPaint=new Paint();
                colorPaint.setStyle(Paint.Style.STROKE);
                colorPaint.setStrokeWidth(10);
                colorPaint.setColor(generateColor(pressx,pressy));
                this.colorPaint = colorPaint;
                this.mCurrentRect = mCurrentRect;
                float translateX = 50.0f;
                float translateY = 50.0f;
                Random rand = new Random(10000);
                int xr = rand.nextInt();
                int yr = rand.nextInt();
                int startColor = generateColor(pressx,pressy);
                int midColor = generateColor(pressx+rand.nextInt(),pressy+rand.nextInt());
                int endColor = generateColor(pressx+xr,pressy+yr);
                DrawThread thread = new DrawThread(startColor,midColor,endColor,translateX,translateY,mCurrentRect);
                thread.run();
                playSound();
        }

        invalidate();
        return true;
    }

    private void animateObject(int startColor, int midColor, int endColor, float translateX, float translateY, AnimatableRectF mCurrentRect){
        ObjectAnimator animateLeft = ObjectAnimator.ofFloat(mCurrentRect, "left", mCurrentRect.left, mCurrentRect.left-translateX);
        ObjectAnimator animateRight = ObjectAnimator.ofFloat(mCurrentRect, "right", mCurrentRect.right, mCurrentRect.right+translateX);
        ObjectAnimator animateTop = ObjectAnimator.ofFloat(mCurrentRect, "top", mCurrentRect.top, mCurrentRect.top-translateY);
        ObjectAnimator animateBottom = ObjectAnimator.ofFloat(mCurrentRect, "bottom", mCurrentRect.bottom, mCurrentRect.bottom+translateY);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator animateColor = ObjectAnimator.ofObject(this, "color", evaluator, startColor,midColor,endColor);

        animateBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                postInvalidate();
            }
        });

        AnimatorSet rectAnimation = new AnimatorSet();
        rectAnimation.playTogether(animateLeft, animateRight, animateTop, animateBottom,animateColor);
        rectAnimation.setDuration(1000).start();
    }

    //Getters and Setters

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public long getCurrtime() {
        return currtime;
    }

    public void setCurrtime(long currtime) {
        this.currtime = currtime;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }
}
