package a481project.swiftkeys.BlueToothManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 2/14/2017.
 */

public class BlueToothManager extends Activity {
    private final String TAG = this.getClass().getName();
    private final static int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mmDevice;
    private UUID MY_UUID;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BlueToothThread blueToothThread;

    private static final KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);

    /**
     * @param uuid
     */
    public BlueToothManager(String uuid) {
        MY_UUID = java.util.UUID.fromString(uuid);
        Log.i(TAG, "Set UUID to :" + MY_UUID);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
            Log.i(TAG, "Set Bluetooth Adaptor to :" + mBluetoothAdapter.getName());
        }
        else{
            Log.e(TAG, "Failed to set Bluetooth Adaptor");
        }
    }

    public boolean initiateConnection() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Cannot connect, the adaptor is not set");
            return false;

        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        /*
            TODO: add code about discovering new blue tooth devices
         */

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                device.fetchUuidsWithSdp();
                ParcelUuid offeredUuid[] = device.getUuids();
                for (ParcelUuid offUuid : offeredUuid) {
                    if (offUuid.toString().equals(MY_UUID.toString())) {
                        mmDevice = mBluetoothAdapter.getRemoteDevice(device.getAddress());
                        Log.i(TAG, "attempting to connect to " + mmDevice.getName() + " : " + mmDevice.getAddress());
                        blueToothThread = new BlueToothThread();
                        blueToothThread.run();
                        break;
                    }
                }
            }
        }
        return true;
    }

    public boolean isConnectionReady() {
        return mmSocket.isConnected();
    }

    private class BlueToothThread extends Thread {
        public void run() {
            createSocket();
            createBlueToothConnection();
            getBlueToothStreams();

        }

        private void createSocket() {
            BluetoothSocket tmp = null;
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                Log.i(TAG, "Creating socket");
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "expection thrown when creating socket:",e);
                return;
            }
            mmSocket = tmp;
        }

        private void createBlueToothConnection() {
            while (true) {
                try {
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    Log.d(TAG, "Attempting connection");
                    mmSocket.connect();
                    Log.d(TAG, "Connection attempt succeeded");

                } catch (IOException connectException) {
                    Log.e(TAG, "Connection attempt failed. Retrying", connectException);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        continue;
                    }
                    continue;
                }
                break;
            }
        }

        private void getBlueToothStreams() {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.d(TAG, "Attempting to obtain streams");
            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                return;
            }
            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                return;
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        private void readData() {
            if(!mmSocket.isConnected()){return;}

            byte[] bytes = new byte[1024];
            int numBytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(bytes);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    public void sendString(String message) {
        KeyEvent[] events = mKeyCharacterMap.getEvents(message.toCharArray());
        for (KeyEvent event : events) {
            write(event.getAction(), event.getKeyCode());
        }
    }

    public void write(int key) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(key).array();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {

        }
    }

    public void write(int action, int key) {
        byte[] bytes = ByteBuffer.allocate(8).putInt(action).putInt(key).array();
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {

        }
    }

}
