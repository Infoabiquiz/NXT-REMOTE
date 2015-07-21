package com.example.sommercamp.nxtremote;

/**
 * Created by sommercamp on 21.07.15.
 *
 */

public class Movement
{
    Bluetooth bluetooth;

    public Movement(Bluetooth bluetooth)
    {
        this.bluetooth = bluetooth;
    }

    public void moveForwards()
    {
        if(!bluetooth.isConnected())
            return;

        // TODO
    }

    public void moveBackwards()
    {
        if(!bluetooth.isConnected())
            return;

        // TODO
    }

    public void turnRight()
    {
        if(!bluetooth.isConnected())
            return;

        // TODO
    }

    public void turnLeft()
    {
        if(!bluetooth.isConnected())
            return;

        // TODO
    }

    public void stop()
    {
        if(!bluetooth.isConnected())
            return;

        // TODO
    }
}
