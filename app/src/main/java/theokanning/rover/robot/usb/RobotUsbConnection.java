package theokanning.rover.robot.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import theokanning.rover.robot.RobotConnection;
import theokanning.rover.robot.RobotConnectionListener;

/**
 * Handles scanning and connecting to a usb device
 *
 * @author Theo Kanning
 */
public class RobotUsbConnection implements RobotConnection{

    private static final String TAG = "RobotUsbConnection";
    private static final String ACTION_USB_PERMISSION = "theokanning.rover.USB_PERMISSION";

    private Context context;

    private UsbSerialDevice serialPort;

    private RobotConnectionListener listener;

    public RobotUsbConnection(Context context) {
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
    }

    @Override
    public void connect(RobotConnectionListener listener){
        this.listener = listener;
        startScan();
    }

    @Override
    public void disconnect(){
        this.listener.onDisconnect();
        this.listener = null;
        tryUnregisteringReceiver();
        closeSerialPort();
    }

    @Override
    public void sendMessage(String message) {
        if (serialPort != null) {
            serialPort.write(message.getBytes());
        }
    }

    private void startScan() {

        closeSerialPort();

        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        // Iterate over all devices
        Iterator<String> it = devices.keySet().iterator();
        while (it.hasNext()) {
            String deviceName = it.next();
            UsbDevice device = devices.get(deviceName);
            String vendorId = Integer.toHexString(device.getVendorId()).toUpperCase();
            String PID = Integer.toHexString(device.getProductId()).toUpperCase();
            if (!manager.hasPermission(device)) {
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                manager.requestPermission(device, mPermissionIntent);
                return;
            } else {
                connectToDevice(device);
            }
        }
    }

    private void connectToDevice(UsbDevice device) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDeviceConnection connection = manager.openDevice(device);
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        configureSerialPort();
        listener.onConnect();
    }

    public boolean isConnected() {
        return serialPort != null;
    }

    private void configureSerialPort() {
        if (serialPort.open()) {
            serialPort.setBaudRate(115200);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(callback);
            Toast.makeText(context, "Serial port opened", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Serial port not open", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
        }
    }

    private void tryUnregisteringReceiver() {
        try {
            context.unregisterReceiver(usbReceiver);
        } catch (IllegalArgumentException e){
            //receiver is unregistered
        }
    }


    private UsbSerialInterface.UsbReadCallback callback = bytes -> {
        String data = tryEncodingBytesAsString(bytes);
        sendMessageToListener(data);
        Log.e(TAG, "Message received: " + data);
    };

    private String tryEncodingBytesAsString(byte[] bytes) {
        String data = "";
        try {
            data = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void sendMessageToListener(String data) {
        if (listener != null) {
            listener.onMessageReceived(data);
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                        connectToDevice(device);
                    } else {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
