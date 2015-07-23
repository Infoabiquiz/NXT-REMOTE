package com.example.sommercamp.nxtremote;

import com.lego.minddroid.NxtConnection;

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

        setEnginePower(60, 60);
    }

    public void moveBackwards()
    {
        if(!bluetooth.isConnected())
            return;

        setEnginePower(-60, -60);
    }

    public void turnRight()
    {
        if(!bluetooth.isConnected())
            return;

        setEnginePower(60, -60);
    }

    public void turnLeft()
    {
        if(!bluetooth.isConnected())
            return;

        setEnginePower(-60, 60);
    }

    public void stop()
    {
        if(!bluetooth.isConnected())
            return;

        setEnginePower(0, 0);
    }

    private void setEnginePower(int leftEngine, int rightEngine)
    {
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_B, rightEngine, 0);
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_C, leftEngine, 0);
    }
}
