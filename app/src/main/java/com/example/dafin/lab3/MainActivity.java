package com.example.dafin.lab3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import static android.graphics.BitmapFactory.decodeResource;


public class MainActivity extends AppCompatActivity implements SensorEventListener2
{
    // Ball position variables
    private float posX = 0.0f;
    private float posY = 0.0f;
    // Ball acceleration variables
    private float accX = 0.0f;
    private float accY = 0.0f;
    // Ball speed variables
    private float velX = 0.0f;
    private float velY = 0.0f;
    // Background variables
    private int bgX =50;
    private int bgY = 50;
    // The Ball on screen
    private Bitmap ball;
    // Hight and wight for the bax
    private float maxX =0.0f;
    private float maxY = 0.0f;
    // Touch the corner variable
    private boolean hitWalls =false;

    // Needed variables for sensor reading and managing
    private MediaPlayer myPlayer;
    private Vibrator myVeberator;
    private SensorManager mySensorManager;

    /**
     * Activity life circle function run when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Sets the orientation of the phone to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        stablishBorders(size);
        setUp(size);
    }

    /**
     * Function that stablish boarders around the screen
     * @param size
     */
    private void stablishBorders(Point size)
    {
        // initialize the max height and wight
        maxX = (float) (size.x - 0.05*size.x);
        maxY = (float) (size.y - 0.05*size.y - 200);
    }

    /**
     * Function to set up sensors and the ball, also set up the view for the UI
     * @param size
     */
    private void setUp(Point size)
    {
        posX = (float) size.x/2;
        posY = (float) size.y/2;

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myVeberator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        myPlayer = MediaPlayer.create(this, R.raw.killstreak);

        BallView ballView = new BallView(this);
        setContentView(ballView);
    }

    /**
     * Function to update the ball movement
     */
    private void updateBall()
    {
        boolean awayX = true;
        boolean awayY = true;

        setUpUpdate();

        if(posX > maxX)
        {
            posX = maxX;
            velX *=-1;                                  // inverse the speed
            awayX = false;
            hitSound();                                 // Hit the music baby
        }
        else if(posX < bgX)
        {
            posX = bgX;
            awayX = false;
            velX *=-1;
            hitSound();
        }

        if(posY > maxY)
        {
            posY = maxY;
            awayY = false;
            velY *=-1;
            hitSound();

        }
        else if(posY < bgY)
        {
            posY = bgY;
            awayY = false;
            velY *=-1;
            hitSound();
        }

        if(hitWalls && awayX && awayY)
        {
            hitWalls = false;
        }
    }

    private void setUpUpdate() {
        posX += velX;
        posY += velY;

        velX = velX +  (accX);
        velY = velY + (accY);

        velX *= 0.99f;
        velY *= 0.99f;
    }

    /**
     * Auto generated function
     * @param sensor
     */
    @Override
    public void onFlushCompleted(Sensor sensor)
    {
        // Doesn't need to be implemented.
        // Maybe later -_- !!
    }

    /**
     * Auto generated function that react when sanson read change
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            accX = 0.2f*event.values[1];
            accY = 0.2f*event.values[0];
            updateBall();
        }
    }

    /**
     * Auto generated function
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Doesn't need to be implemented.
        // Maybe later -_- !!
    }

    /**
     * Inner Class that show the ball as a view
     */
    private class BallView extends View
    {
        private Rect background;
        private Paint bgColor;

        /**
         * Constructor
         * @param context
         */
        public BallView(Context context)
        {
            super(context);
            int ball_w = 100;
            int right_border = Math.round(maxX)+ ball_w;
            int ball_h = 100;
            int left_border = Math.round(maxY)+ ball_h;
            background = new Rect(bgX, bgY,right_border,left_border);

            bgColor = new Paint();
            bgColor.setColor(Color.BLUE);

            Bitmap ball_image = decodeResource(getResources(), R.drawable.ball);

            ball = Bitmap.createScaledBitmap(ball_image, ball_w, ball_h,true);
        }

        /**
         * Auto generated function to draw the ball and background
         * @param canvas
         */
        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawRect(background, bgColor);
            canvas.drawBitmap(ball, posX, posY,null);
            invalidate();
        }
    }

    /**
     * Function to hitWalls the sound
     */
    public void hitSound()
    {
        if(!hitWalls)
        {
            myPlayer.start();
            int VIBRATION_TIME = 100;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                myVeberator.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else
            {
                myVeberator.vibrate(VIBRATION_TIME);
            }
            hitWalls = true;
        }
    }

    /**
     * Android life circle function, on start.
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        mySensorManager.registerListener(this, mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Android life circle function, on stop.
     */
    @Override
    protected void onStop()
    {
        mySensorManager.unregisterListener(this);
        super.onStop();
    }
}
