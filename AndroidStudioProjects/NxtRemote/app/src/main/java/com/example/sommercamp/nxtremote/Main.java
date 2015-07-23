package com.example.sommercamp.nxtremote;

import android.graphics.Point;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch);
        currentLayout = CurrentLayout.touch;

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
                                Point touchedPoint = new Point((int)event.getRawX(),
                                        (int)event.getRawY());

                                Point middlePoint = new Point(v.getMeasuredWidth() / 2,
                                        v.getMeasuredHeight() / 2);



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
        }
    }
}
