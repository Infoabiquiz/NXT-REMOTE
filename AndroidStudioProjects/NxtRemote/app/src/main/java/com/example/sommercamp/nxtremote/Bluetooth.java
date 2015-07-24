package com.example.sommercamp.nxtremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lego.minddroid.NxtConnection;

/**
 * Created by sommercamp on 21.07.15.
 *
 */

public class Bluetooth extends Handler
{
    private Main main;
    private boolean isConnected;
    private BluetoothAdapter bluetoothAdapter;
    private NxtConnection nxtConnection;
    private String nxtMacAddress;

    public Bluetooth(Main main)
    {
        this.main = main;
        isConnected = false;
        showMovementButtons(false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        nxtConnection = null;
    }

    public void connect()
    {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            main.startActivityForResult(btIntent, 3);
            Log.i("Bluetooth", "bluetooth adapter enabled");
        }

        nxtMacAddress = "";
        for(BluetoothDevice bluetoothDevice : bluetoothAdapter.getBondedDevices())
        {
            if(bluetoothDevice.getName().toLowerCase().contains("nxt"))
            {
                nxtMacAddress = bluetoothDevice.getAddress();
                break;
            }
        }

        if(nxtConnection == null || !nxtConnection.isConnected())
        {
            nxtConnection = new NxtConnection(main, this, bluetoothAdapter);
            nxtConnection.setMACAddress(nxtMacAddress);
            enableConnectButton(false);
            nxtConnection.start();
        }
    }

    public void disconnect()
    {
        if(nxtConnection != null && nxtConnection.isConnected())
        {
            sendBTCMessage(NxtConnection.NO_DELAY, NxtConnection.DISCONNECT, 0, 0);
            nxtConnection = null;
            nxtMacAddress = "";
            Toast.makeText(main, "Disconnected", Toast.LENGTH_SHORT).show();

            isConnected = false;
            showMovementButtons(false);
            if(main.currentLayout == CurrentLayout.main)
                ((Button)main.findViewById(R.id.btn_connect)).setText("Connect");
            else
                ((Button)main.findViewById(R.id.btn_connect2)).setText("Connect");
        }
    }

    public void forceStopAndDisconnect()
    {

    }

    public boolean isConnected()
    {
        return isConnected;
    }

    @Override
    public void handleMessage(Message message) {
        int messageID = message.getData().getInt("message");

        switch (messageID) {
            case NxtConnection.CONNECT_OK:
                Toast.makeText(main, "Connected", Toast.LENGTH_SHORT).show();

                isConnected = true;
                showMovementButtons(true);
                if(main.currentLayout == CurrentLayout.main)
                    ((Button)main.findViewById(R.id.btn_connect)).setText("Disconnect");
                else
                    ((Button)main.findViewById(R.id.btn_connect2)).setText("Disconnect");
                break;

            case NxtConnection.CONNECT_ERROR:
                Toast.makeText(main, "Failed connecting, connection error",
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(main, "Failed connecting", Toast.LENGTH_SHORT).show();
                break;
        }

        enableConnectButton(true);
    }

    public void showMovementButtons(boolean show)
    {
        if(main != null && main.currentLayout != null && (main.currentLayout == CurrentLayout.main))
        {
            int visibility = show ? View.VISIBLE : View.GONE;

            main.findViewById(R.id.btn_forwards).setVisibility(visibility);
            main.findViewById(R.id.btn_backwards).setVisibility(visibility);
            main.findViewById(R.id.btn_left).setVisibility(visibility);
            main.findViewById(R.id.btn_right).setVisibility(visibility);
            main.findViewById(R.id.btn_stop).setVisibility(visibility);
        }
    }

    public void sendBTCMessage(int delay, int messageID, int value1, int value2)
    {
        try
        {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bundle myBundle = new Bundle();

        myBundle.putInt("message", messageID);
        myBundle.putInt("value1", value1);
        myBundle.putInt("value2", value2);

        Message message = obtainMessage();
        message.setData(myBundle);

        nxtConnection.getHandler().sendMessage(message);
    }

    private void enableConnectButton(boolean enable)
    {
        if(main.currentLayout == CurrentLayout.main)
            main.findViewById(R.id.btn_connect).setEnabled(enable);
        else
            main.findViewById(R.id.btn_connect2).setEnabled(enable);
    }
}
