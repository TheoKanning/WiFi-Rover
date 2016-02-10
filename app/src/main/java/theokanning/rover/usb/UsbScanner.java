package theokanning.rover.usb;

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

/**
 * Handles scanning and connecting to a usb device
 *
 * @author Theo Kanning
 */
public class UsbScanner {

    public interface UsbScannerListener {
        void onConnect();

        void onDisconnect();
    }

    private static final String TAG = "UsbScanner";
    private static final String ACTION_USB_PERMISSION = "com.blecentral.USB_PERMISSION";

    private Context context;

    private UsbSerialDevice serialPort;


    private UsbScannerListener listener;

    public UsbScanner(Context context) {
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbReceiver, filter);
    }

    public void registerListener(UsbScannerListener listener) {
        this.listener = listener;
    }

    public void unregisterListener(){
        this.listener = null;
    }

    public void startScan() {
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

    public boolean isConnected(){
        return serialPort != null;
    }

    public void configureSerialPort(){
        if(serialPort.open()){
            serialPort.setBaudRate(115200);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(callback); //
            Toast.makeText(context, "Serial port opened", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Serial port not open", Toast.LENGTH_SHORT).show();
        }
    }

    public void write(String message){
        if(serialPort != null) {
            serialPort.write(message.getBytes());
        }
    }

    public void close(){
        if(serialPort != null){
            serialPort.close();
        }
        context.unregisterReceiver(mUsbReceiver);
    }

    private UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                Log.e(TAG, "Message received: " + data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                        connectToDevice(device);
                    } else {
                        Toast.makeText(context, "Permission revoked", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}
