package com.example.sommercamp.nxtremote;

import com.lego.minddroid.NxtConnection;

/**
 * Created by sommercamp on 21.07.15.
 *
 */

public class Movement
{
    private Bluetooth bluetooth;
    private int leftEnginePower;
    private int rightEnginePower;

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

        setEnginePower(-leftEnginePower, -rightEnginePower);

        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        setEnginePower(0, 0);
    }

    private void setEnginePower(int leftEngine, int rightEngine)
    {
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_B, rightEngine, 0);
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_C, leftEngine, 0);

        leftEnginePower = leftEngine;
        rightEnginePower = rightEngine;
    }
}
