package theokanning.rover.robot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Toast;

/**
 * Class to handle all bluetooth scanning
 *
 * @author Theo Kanning
 */
public class BluetoothScanner {

    public interface OnBluetoothDeviceDiscoveredListener{
        void OnBluetoothDeviceDiscovered(BluetoothDevice device);
    }

    private static final String TAG = BluetoothScanner.class.getSimpleName();
    private static final int SCAN_DURATION = 10000; //10s

    private Context context;

    private BluetoothAdapter btAdapter;

    private Handler handler;

    private OnBluetoothDeviceDiscoveredListener listener;

    public BluetoothScanner(Context context) {
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        handler = new Handler();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.getApplicationContext().registerReceiver(receiver, filter);
    }

    private Runnable endScan = new Runnable() {
        @Override
        public void run() {
            btAdapter.cancelDiscovery();
            Toast.makeText(context, "Scan stopped", Toast.LENGTH_SHORT).show();
        }
    };

    public void startScan(OnBluetoothDeviceDiscoveredListener listener){
        this.listener = listener;
        btAdapter.startDiscovery();
        Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

        handler.postDelayed(endScan, SCAN_DURATION);
    }

    public void stopScan(){
        btAdapter.cancelDiscovery();
        handler.removeCallbacks(endScan);
    }

    public BluetoothDevice getDevices(String address){
        return btAdapter.getRemoteDevice(address);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action) && listener != null) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName() != null) {
                    listener.OnBluetoothDeviceDiscovered(device);
                }
            }
        }
    };
}

