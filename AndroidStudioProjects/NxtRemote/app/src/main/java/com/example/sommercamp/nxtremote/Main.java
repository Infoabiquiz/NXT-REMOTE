package com.example.sommercamp.nxtremote;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Main extends ActionBarActivity {

    Bluetooth bluetooth;
    Movement movement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = new Bluetooth(this);
        movement = new Movement(bluetooth);
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

        }
    }
}
