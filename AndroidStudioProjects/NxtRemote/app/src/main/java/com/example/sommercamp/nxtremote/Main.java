package com.example.sommercamp.nxtremote;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Main extends ActionBarActivity {

    private Bluetooth bluetooth;
    private Movement movement;
    public CurrentLayout currentLayout;
    private boolean gyroActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch);
        currentLayout = CurrentLayout.touch;

        SensorManager sensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        sensorManager.registerListener(accelerometer, sensorManager
                        .getSensorList(Sensor.TYPE_ORIENTATION).get(0),
                SensorManager.SENSOR_DELAY_GAME);

        init();
    }

    private final SensorEventListener accelerometer = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (bluetooth != null && bluetooth.isConnected() && gyroActivated)
            {
                Log.i("GyroSensor", event.values[2] + " - " + event.values[1]);
                double roll = Math.min(event.values[2], 50);
                double pitch = Math.min(event.values[1], 25);

                movement.setEnginePower((int)(pitch * 2 + (-1* Math.abs(roll)+50)),
                        (int)(pitch*2+(-1* Math.abs(roll)+50)));
            }
        }
    };

    private void init()
    {
        if(bluetooth != null)
            bluetooth.disconnect();

        bluetooth = new Bluetooth(this);
        movement = new Movement(bluetooth);

        if(currentLayout == CurrentLayout.touch)
        {
            findViewById(R.id.touchArea).setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(v.getId() == R.id.touchArea)
                            {
                                Point touchedPoint = new Point((int)event.getX(),
                                        (int)event.getY());

                                Point middlePoint = new Point(v.getMeasuredWidth() / 2,
                                        v.getMeasuredHeight() / 2);

                                double radius = v.getMeasuredWidth() / 2;

                                double strength = Math.sqrt(Math.pow(touchedPoint.x
                                        - middlePoint.x, 2) + Math.pow(touchedPoint.y
                                        - middlePoint.y, 2)) / radius;

                                double yFactor = -(touchedPoint.y - middlePoint.y) / radius;

                                yFactor = Math.max(-100, Math.min(100, yFactor));

                                if(strength < 0.15)
                                    movement.setEnginePower(0, 0);
                                else
                                {
                                    int leftPower  = (int) (100 * yFactor);
                                    int rightPower = (int) (100 * yFactor);

                                    double tmp;

                                    if (touchedPoint.x >= radius)
                                    {
                                        tmp = strength * 100
                                                * ((v.getMeasuredWidth() - touchedPoint.x) / radius);
                                        movement.setEnginePower(leftPower, (int)tmp);
                                    }
                                    else
                                    {
                                        tmp = strength * 100
                                                * ((v.getMeasuredWidth() - touchedPoint.x) / radius);
                                        movement.setEnginePower((int)tmp, rightPower);
                                    }
                                }

                                return true;
                            }
                            return false;
                        }
                    }
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showError(String className, String msg)
    {
        Log.e(className, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public Main() {
        super();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                Log.i(getClass().getName(), "btn_connect");
                if(!bluetooth.isConnected())
                    bluetooth.connect();
                else
                    bluetooth.disconnect();
                break;
            case R.id.btn_forwards:
                movement.moveForwards();
                Log.i(getClass().getName(),"btn_forwards");
                break;
            case R.id.btn_backwards:
                movement.moveBackwards();
                Log.i(getClass().getName(),"btn_backwards");
                break;
            case R.id.btn_left:
                movement.turnLeft();
                Log.i(getClass().getName(),"btn_left");
                break;
            case R.id.btn_stop:
                movement.stop();
                Log.i(getClass().getName(),"btn_stop");
                break;
            case R.id.btn_right:
                movement.turnRight();
                Log.i(getClass().getName(),"btn_right");
                break;


            case R.id.btn_connect2:
                Log.i(getClass().getName(), "btn_connect2");
                if(!bluetooth.isConnected())
                    bluetooth.connect();
                else
                    bluetooth.disconnect();
                break;

            case R.id.btn_switch_layout:
                currentLayout = CurrentLayout.touch;
                setContentView(R.layout.touch);
                init();
                break;
            case R.id.btn_switch_layout2:
                currentLayout = CurrentLayout.main;
                setContentView(R.layout.activity_main);
                init();
                break;

            case R.id.btn_gyro:
                bluetooth.showMovementButtons(gyroActivated);
                gyroActivated = !gyroActivated;
                break;
        }
    }
}
