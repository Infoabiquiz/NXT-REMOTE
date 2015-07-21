package com.example.sommercamp.nxtremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lego.minddroid.NxtConnection;

import java.util.Set;

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

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        nxtMacAddress = pairedDevices.iterator().next().getAddress();

        if(nxtConnection == null || !nxtConnection.isConnected())
        {
            nxtConnection = new NxtConnection(main, this, bluetoothAdapter);
            nxtConnection.setMACAddress(nxtMacAddress);
            nxtConnection.start();
        }
    }

    public void disconnect()
    {
        if(nxtConnection != null && nxtConnection.isConnected())
        {
            nxtConnection = null;
            nxtMacAddress = "";
            Toast.makeText(main, "Disconnected", Toast.LENGTH_SHORT).show();

            isConnected = false;
            showMovementButtons(false);
            ((Button)main.findViewById(R.id.btn_connect)).setText("Connect");
        }
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
                ((Button)main.findViewById(R.id.btn_connect)).setText("Disconnect");
                break;

            case NxtConnection.CONNECT_ERROR:
                Toast.makeText(main, "Failed connecting", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(main, "Failed connecting", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showMovementButtons(boolean show)
    {
        int visibility = show ? View.VISIBLE : View.GONE;

        main.findViewById(R.id.btn_forwards).setVisibility(visibility);
        main.findViewById(R.id.btn_backwards).setVisibility(visibility);
        main.findViewById(R.id.btn_left).setVisibility(visibility);
        main.findViewById(R.id.btn_right).setVisibility(visibility);
        main.findViewById(R.id.btn_stop).setVisibility(visibility);
    }
}
