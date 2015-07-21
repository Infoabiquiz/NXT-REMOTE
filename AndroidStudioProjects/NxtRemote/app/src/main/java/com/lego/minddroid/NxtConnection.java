/**
 *   Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *   This file is part of MINDdroid.
 *
 *   MINDdroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MINDdroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.lego.minddroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/receive methods by themselves.
 */
public class NxtConnection extends Thread {
	public static final int MOTOR_A = 0;
	public static final int MOTOR_B = 1;
	public static final int MOTOR_C = 2;
	public static final int READ_MOTOR_STATE = 60;
	public static final int DISCONNECT = 99;

	public static final int CONNECT_OK = 1001;
	public static final int CONNECT_ERROR = 1002;
	public static final int MOTOR_STATE = 1003;
	public static final int STATE_RECEIVEERROR = 1004;
	public static final int FIRMWARE_VERSION = 1006;
	public static final int VIBRATE_PHONE = 1031;

	public static final int NO_DELAY = 0;

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml
	public static final String OUI_LEGO = "00:16:53";

	private final BluetoothAdapter btAdapter;

	/**
	 * the MAC address of the device to connect to
	 */
	private String mMACaddress;

	/**
	 * the Bluetooth socket that is eventually connected to the nxt robot
	 */
	private BluetoothSocket nxtBTsocket = null;

	/**
	 * the output stream of the Bluetooth socket
	 */
	private OutputStream nxtOutputStream = null;

	/**
	 * the output stream of the Bluetooth socket
	 */
	private InputStream nxtInputStream = null;

	/**
	 * flag to tell whether the Bluetooth connection is up or not
	 */
	private boolean connected = false;

	/**
	 * the handler of the user interface
	 */
	private final Handler uiHandler;

	/**
	 * handler for the requests from the user/ui
	 */
	final private Handler requestHandler;

	public NxtConnection(Activity myOwner, Handler uiHandler, BluetoothAdapter adapter)
	{
		this.uiHandler = uiHandler;
		this.btAdapter = adapter;

		requestHandler = initializeHandler();
	}

	public Handler getHandler()
	{
		return requestHandler;
	}

	public void setMACAddress(String mMACaddress)
	{
		this.mMACaddress = mMACaddress;
	}

	/**
	 * @return whether the Bluetooth connection is up or not
	 */
	public boolean isConnected()
	{
		return connected;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run()
	{
		try
		{
			createNXTconnection();
		}
		catch(IOException e)
		{
			logException("on_createNXTconnection", e);
			notifyUI(CONNECT_ERROR);
		}

		while(connected)
		{
			try
			{
				// receiveMessage is blocking
				byte[] returnMessage = receiveMessage();

				if(isValidReply(returnMessage))
					dispatchMessage(returnMessage);
			}
			catch(IOException e)
			{
				logException("in_run", e);

				// don't inform the user when connection is already closed
				if(!connected)
					notifyUI(STATE_RECEIVEERROR);

				return;
			}
		}
	}

	private boolean isValidReply(byte[] returnMessage)
	{
		return returnMessage.length >= 2 && (returnMessage[0] == LCPMessage.REPLY_COMMAND
				|| returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY);
	}

	/**
	 * Create a bluetooth connection with SerialPortServiceClass_UUID
	 * @see <a href=
	 *      "http://lejos.sourceforge.net/forum/viewtopic.php?t=1991&highlight=android"
	 *      />
	 * On error the method either sends a message to it's owner or creates an exception in the
	 * case of no message handler.
	 */
	public void createNXTconnection() throws IOException
	{
		//btAdapter.cancelDiscovery();

		BluetoothDevice nxtDevice = btAdapter.getRemoteDevice(mMACaddress);

		if(nxtDevice == null)
		{
			notifyUI(CONNECT_ERROR);
			return;
		}

		nxtBTsocket = createSocket(nxtDevice, !true);

		nxtBTsocket.connect();

		nxtInputStream	= nxtBTsocket.getInputStream();
		nxtOutputStream	= nxtBTsocket.getOutputStream();

		notifyUI(CONNECT_OK);

		connected = true;
	}

	private BluetoothSocket createSocket(BluetoothDevice nxtDevice, boolean useDefault) throws IOException
	{
		BluetoothSocket socket = null;

		if(useDefault)
			socket = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);

		else
		{
			try
			{
				Method method = nxtDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				socket = (BluetoothSocket) method.invoke(nxtDevice, Integer.valueOf(1));
			}
			catch(SecurityException e)
			{
				logException("in_createSocket", e);
			}
			catch(NoSuchMethodException e)
			{
				logException("in_createSocket", e);
			}
			catch(IllegalArgumentException e)
			{
				logException("in_createSocket", e);
			}
			catch(IllegalAccessException e)
			{
				logException("in_createSocket", e);
			}
			catch(InvocationTargetException e)
			{
				logException("in_createSocket", e);
			}
		}

		return socket;
	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	private void destroyNXTconnection()
	{
		try
		{
			if (nxtBTsocket != null)
			{
				nxtBTsocket.close();
				nxtBTsocket = null;
			}

			nxtInputStream = null;
			nxtOutputStream = null;
		}
		catch(IOException e)
		{
			logException("in_destroyNXTconnection", e);
		}
		finally
		{
			connected = false;
		}
	}

	/**
	 * Sends a message on the opened OutputStream
	 * @param message, the message as a byte array
	 */
	public void sendMessage(byte[] message) throws IOException {
		if(nxtOutputStream == null)
			throw new IOException();

		// send message length
		int messageLength = message.length;
		nxtOutputStream.write(messageLength);
		nxtOutputStream.write(messageLength >> 8);
		nxtOutputStream.write(message, 0, message.length);
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	private byte[] receiveMessage() throws IOException {
		if (nxtInputStream == null)
			throw new IOException();

		int length = nxtInputStream.read();

		// unless the end of the stream is not yet reached (would be -1)
		if(length > 0)
		{
			length = (nxtInputStream.read() << 8) + length;

			byte[] returnMessage = new byte[length];

			nxtInputStream.read(returnMessage);

			return returnMessage;
		}

		return new byte[8];
	}

	private void dispatchMessage(byte[] message)
	{
		switch(message[1])
		{
			case LCPMessage.GET_OUTPUT_STATE:

				if (message.length >= 25)
					notifyUI(MOTOR_STATE);
				break;

			case LCPMessage.GET_FIRMWARE_VERSION:

				if (message.length >= 7)
					notifyUI(FIRMWARE_VERSION);
				break;

			case LCPMessage.VIBRATE_PHONE:
				if (message.length == 3)
					notifyUI(VIBRATE_PHONE);
		}
	}

	private Handler initializeHandler()
	{
		return new Handler()
		{
			@Override
			public void handleMessage(Message myMessage)
			{
				int message = myMessage.getData().getInt("message");

				switch(message)
				{
					case MOTOR_A:
					case MOTOR_B:
					case MOTOR_C:
						changeMotorSpeed(message, myMessage.getData().getInt("value1"));
						break;

					case READ_MOTOR_STATE:
						readMotorState(myMessage.getData().getInt("value1"));
						break;

					case DISCONNECT:
						// send stop messages before closing
						changeMotorSpeed(MOTOR_A, 0);
						changeMotorSpeed(MOTOR_B, 0);
						changeMotorSpeed(MOTOR_C, 0);

						waitSomeTime(500);
						destroyNXTconnection();
						break;
				}
			}
		};
	}

	private void changeMotorSpeed(int motor, int speed)
	{
		if (speed > 100)
			speed = 100;

		else if (speed < -100)
			speed = -100;

		try
		{
			sendMessage(LCPMessage.getMotorMessage(motor, speed));
		}
		catch(IOException e)
		{
			logException("in_changeMotorSpeed", e);
		}
	}

	private void readMotorState(int motor)
	{
		try
		{
			sendMessage(LCPMessage.getOutputStateMessage(motor));
		}
		catch(IOException e)
		{
			logException("in_readMotorState", e);
		}
	}

	private void waitSomeTime(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}

		catch(InterruptedException e)
		{
			logException("in_waitSomeTime", e);
		}
	}

	private void notifyUI(int messageID)
	{
		Message message = requestHandler.obtainMessage();

		Bundle bundle = new Bundle();
		bundle.putInt("message", messageID);

		message.setData(bundle);

		uiHandler.sendMessage(message);
	}

	private static void logException(String tag, Exception e)
	{
		Log.e(tag, e.getClass() + ": " + e.getMessage());
	}
}
