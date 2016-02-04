package theokanning.rover.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Class for handling connection to a single device
 * Largely taken from google documentation
 * http://developer.android.com/guide/topics/connectivity/bluetooth.html
 *
 * @author Theo Kanning
 */
public class BluetoothConnection {

    public interface BluetoothConnectionListener {
        void onMessageReceived(String message);

        void onConnect();

        void onDisconnect();
    }

    private static final String TAG = BluetoothConnection.class.getSimpleName();

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int MESSAGE_RECEIVED = 1;

    private BluetoothConnectionListener bluetoothConnectionListener;

    private BluetoothDevice bluetoothDevice;

    private BluetoothHandler bluetoothHandler;

    private ConnectThread connectThread;

    private ConnectedThread connectedThread;

    private boolean isConnected = false;

    public BluetoothConnection(BluetoothDevice device, BluetoothConnectionListener listener) {
        this.bluetoothDevice = device;
        this.bluetoothConnectionListener = listener;
        this.bluetoothHandler = new BluetoothHandler(listener);
        this.connectThread = new ConnectThread(device);
        bluetoothHandler.post(connectThread);
    }

    public void write(String message) {
        if (connectedThread != null) {
            connectedThread.write(message);
        }
    }

    public void disconnect() {
        connectThread.cancel();
    }

    public boolean isConnected() {
        return isConnected;
    }

    private static class BluetoothHandler extends Handler {
        WeakReference<BluetoothConnectionListener> listenerWeakReference;

        BluetoothHandler(BluetoothConnectionListener listener) {
            listenerWeakReference = new WeakReference<BluetoothConnectionListener>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVED:
                    BluetoothConnectionListener listener = listenerWeakReference.get();
                    if (listener != null) {
                        //Reading message not yet implemented
                        listener.onMessageReceived("Message Received");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to bluetoothSocket,
            // because bluetoothSocket is final
            BluetoothSocket tmp = null;
            bluetoothDevice = device;

            Log.d(TAG, "Initializing ConnectThread, address = " + bluetoothDevice.getAddress());

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothSocket = tmp;
        }

        public void run() {

            try {
                // Connect the device through the bluetoothSocket. This will block
                // until it succeeds or throws an exception
                bluetoothSocket.connect();
                bluetoothConnectionListener.onConnect();
            } catch (IOException connectException) {
                // Unable to connect; close the bluetoothSocket and get out
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

            isConnected = true;

            // Do work to manage the connection (in a separate thread)
            connectedThread = new ConnectedThread(bluetoothSocket);
        }

        /**
         * Will cancel an in-progress connection, and close the bluetoothSocket
         */
        public void cancel() {
            try {
                bluetoothSocket.close();
                isConnected = false;
                bluetoothConnectionListener.onDisconnect();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    bluetoothHandler.obtainMessage(MESSAGE_RECEIVED, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            try {
                outputStream.write(message.getBytes());
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
