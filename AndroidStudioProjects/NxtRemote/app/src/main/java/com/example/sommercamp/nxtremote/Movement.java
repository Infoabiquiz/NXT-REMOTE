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
    private int movementState = 1;

    private MovementMode currentMovement;
    private final static int startValue = 65;
    private int clickCounter = 65;

    public Movement(Bluetooth bluetooth)
    {
        this.bluetooth = bluetooth;
    }

    public void setMovement(MovementMode nextMovement)
    {
        if(!bluetooth.isConnected())
            return;

        handleMovementStateChanges(nextMovement);

        switch(nextMovement)
        {
            case forwards:
                setEnginePower(clickCounter, clickCounter);
                movementState = 1;
                break;

            case backwards:
                setEnginePower(-clickCounter, -clickCounter);
                movementState = -1;
                break;

            case left:
                if (movementState == 1)
                    setEnginePower(clickCounter / 2, clickCounter + 10);
                else
                    setEnginePower(-clickCounter / 2, -clickCounter);
                break;

            case right:
                if (movementState == 1)
                    setEnginePower(clickCounter + 10, clickCounter / 2);
                else
                    setEnginePower(-clickCounter, -clickCounter / 2);
                break;

            case stop:
                setEnginePower(-leftEnginePower, -rightEnginePower);
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                setEnginePower(0, 0);
                break;
        }
    }

    private void handleMovementStateChanges(MovementMode mode)
    {
        if (currentMovement == mode || mode == MovementMode.left || mode == MovementMode.right)
            clickCounter += 10;
        else
        {
            clickCounter = startValue;
            currentMovement = mode;
        }
    }

    public void setEnginePower(int leftEngine, int rightEngine)
    {
        if(!bluetooth.isConnected())
            return;

        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_B, rightEngine, 0);
        bluetooth.sendBTCMessage(0, NxtConnection.MOTOR_C, leftEngine, 0);

        leftEnginePower = leftEngine;
        rightEnginePower = rightEngine;
    }
}
