package com.example.sommercamp.nxtremote;

import android.util.Log;

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

    private CurrentMovement currentMovement;
    private int clickCounter = 65;

    public Movement(Bluetooth bluetooth)
    {
        this.bluetooth = bluetooth;
    }

    public void moveForwards()
    {
        if(!bluetooth.isConnected())
            return;

        if (currentMovement == CurrentMovement.forwards){
            clickCounter +=10;

        }
        else{
            clickCounter = 65;
            currentMovement = CurrentMovement.forwards;
        }
        setEnginePower(clickCounter, clickCounter);
    }

    public void moveBackwards()
    {
        if(!bluetooth.isConnected())
            return;

        if (currentMovement == CurrentMovement.backwards){
            clickCounter +=10;

        }
        else{
            clickCounter = 65;
            currentMovement = CurrentMovement.backwards;
        }

        setEnginePower(-clickCounter, -clickCounter);
    }

    public void turnRight()
    {
        if(!bluetooth.isConnected())
            return;

        if (currentMovement == CurrentMovement.right){
            clickCounter +=10;

        }
        else{
            clickCounter = 65;
            currentMovement = CurrentMovement.right;
        }

        setEnginePower(clickCounter - 10, -clickCounter + 10);
    }

    public void turnLeft()
    {
        if(!bluetooth.isConnected())
            return;

        if (currentMovement == CurrentMovement.left){
            clickCounter +=10;

        }
        else{
            clickCounter = 65;
            currentMovement = CurrentMovement.left;
        }

        setEnginePower(-clickCounter + 10, clickCounter - 10);
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
        clickCounter = 65;
        setEnginePower(0, 0);
        currentMovement = CurrentMovement.stop;
    }

    public void setEnginePower(int leftEngine, int rightEngine)
    {
        if(!bluetooth.isConnected())
            return;

        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_B, rightEngine, 0);
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_C, leftEngine, 0);

        leftEnginePower = leftEngine;
        rightEnginePower = rightEngine;

        Log.i("MotorPower: ", leftEngine + " - " + rightEngine);
    }
}
