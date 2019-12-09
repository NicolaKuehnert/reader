package com.example.reader;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BLEConnection {

    protected BluetoothAdapter BA;
    protected Set<BluetoothDevice> scannedDevices;
    protected BluetoothDevice device;
    protected String DeviceName;
    protected String deviceHardwareAddress;
    private BluetoothGatt bluetoothGatt;

    private static final String TAG = "BLE_READER";



    Context context;

    public BLEConnection(Context context){
        this.context = context;

        BA = BluetoothAdapter.getDefaultAdapter();
    }




    private final String CHARACTERISTIC_UUID = "6d350151-ab8d-4ab6-9258-078ebda04501";
    private final String SERVICE_UUID = "6d350151-ab8d-4ab6-9258-078ebda04501";
    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    /*
    Get all bonded devices
     */
    public void search(){
        scannedDevices = BA.getBondedDevices();
    }

    /*
    Connect to set device
    If connected, disconnect and reconnect
     */
    public int connect(){

        if(bluetoothGatt != null){
            disconnect();
        }
        if(device == null){
            return 1;
        }

        bluetoothGatt = device.connectGatt(context, true, gattCallback);
        bluetoothGatt.discoverServices();
        return 0;


    }

    /*
    Disconnect GATT from device
     */
    public void disconnect(){
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;

    }

    /*
    Update the characteristic and write it to the server
     */
    public void sendUpdate(String message){
        if(bluetoothGatt == null){
            return;
        }
        BluetoothGattService ser = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
        if(ser == null){
            broadcastUpdate("service not found");
            return;
        }
        BluetoothGattCharacteristic chara = ser.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
        chara.setValue(message);
        bluetoothGatt.writeCharacteristic(chara);
        broadcastUpdate("Update send: " + message);



    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            broadcastUpdate(ACTION_GATT_CONNECTED);
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> gattServices = gatt.getServices();
            for(BluetoothGattService item : gattServices){
                UUID itemId = item.getUuid();
                UUID clientId = UUID.fromString(SERVICE_UUID);
                if (itemId.equals(clientId)){
                    broadcastUpdate("Client Service UUID equals Item Service UUID");
                    BluetoothGattCharacteristic chara = item.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                    chara.setValue("Update!");
                    gatt.writeCharacteristic(chara);
                }
            }
        }
    };

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic){
        String value = characteristic.getValue().toString();
        Log.i(TAG, action + " : " + value);

    }

    private void broadcastUpdate(final String action){
        Log.i(TAG, action);
    }

}
