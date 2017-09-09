package com.sanyecao.hu.fever_thermometer.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by huhaisong on 2017/8/31 9:59.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final static UUID BATTERY_SERVICE = UUID
            .fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID
            .fromString("00002A19-0000-1000-8000-00805f9b34fb");

    public final static UUID HT_SERVICE_UUID = UUID
            .fromString("00001809-0000-1000-8000-00805f9b34fb");

    private static final UUID HT_MEASUREMENT_CHARACTERISTIC_UUID = UUID
            .fromString("00002A1C-0000-1000-8000-00805f9b34fb");

    private static final UUID HT_IMEDIATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID
            .fromString("00002A1E-0000-1000-8000-00805f9b34fb");

    private static final UUID HT_MEASUREMENT_INTERVAL_CHARACTERISTIC_UUID = UUID
            .fromString("00002A21-0000-1000-8000-00805f9b34fb");

    private final int HIDE_MSB_8BITS_OUT_OF_32BITS = 0x00FFFFFF;
    private final int HIDE_MSB_8BITS_OUT_OF_16BITS = 0x00FF;
    private final int SHIFT_LEFT_8BITS = 8;
    private final int SHIFT_LEFT_16BITS = 16;
    private final int GET_BIT24 = 0x00400000;
    private static final int FIRST_BIT_MASK = 0x01;

    private Set<BluetoothDevice> newDevices = new HashSet<>();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothDevice mBluetoothDevice;
    private Handler mBleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private OnDataAvailableListener mOnDataAvailableListener;
    private Context mContext;

    private BluetoothGatt mBluetoothGatt;
    public BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public BluetoothGattCharacteristic mHTIntervalCharacter;
        public BluetoothGattCharacteristic mBatteryCharacteritsic;
        public boolean isBatteryServiceFound = false;
        public boolean isHTServiceFound = false;
        private BluetoothGattService mHTService, mBatteryService;

        /**
         * Callback indicating when GATT client has connected/disconnected
         * to/from a remote GATT server.
         *
         * @param gatt
         *            GATT client
         * @param status
         *            Status of the connect or disconnect operation.
         *            {@link BluetoothGatt#GATT_SUCCESS} if the operation
         *            succeeds.
         * @param newState
         *            Returns the new connection state. Can be one of
         *            {@link BluetoothProfile#STATE_DISCONNECTED} or
         *            {@link BluetoothProfile#STATE_CONNECTED}
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // deviceListAdapter.notifyDataSetChanged();
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                }
            }
        }

        /**
         * Callback invoked when the list of remote services, characteristics
         * and descriptors for the remote device have been updated, ie new
         * services have been discovered.
         *
         * @param gatt
         *            GATT client invoked {@link BluetoothGatt#discoverServices}
         * @param status
         *            {@link BluetoothGatt#GATT_SUCCESS} if the remote device
         *            has been explored successfully.
         */
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();

                for (BluetoothGattService service : services) {
                    if (service.getUuid().equals(HT_SERVICE_UUID)) {
                        isHTServiceFound = true;
                        mHTService = service;
                    }
                    if (service.getUuid().equals(BATTERY_SERVICE)) {
                        mBatteryService = service;
                        isBatteryServiceFound = true;
                    }
                }
                if (!isHTServiceFound) {
                    gatt.disconnect();
                    return;
                }
                if (isBatteryServiceFound) {
                    readBatteryLevel();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(
                        BATTERY_LEVEL_CHARACTERISTIC)) {
                    int batteryValue = characteristic.getValue()[0];
                    Log.d(TAG, "Battery: " + batteryValue);
                    if (isHTServiceFound) {
                        ChangeHTP_Interval();
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            double tempValue = 0.0;
            if ((characteristic.getUuid()
                    .equals(HT_IMEDIATE_MEASUREMENT_CHARACTERISTIC_UUID))
                    || (characteristic.getUuid()
                    .equals(HT_MEASUREMENT_CHARACTERISTIC_UUID))) {
                try {
                    //获得的温度
                    tempValue = decodeTemperature(characteristic.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * This method decode temperature value received from Health Thermometer
         * device First byte {0} of data is flag and first bit of flag shows
         * unit information of temperature. if bit 0 has value 1 then unit is
         * Fahrenheit and Celsius otherwise Four bytes {1 to 4} after Flag bytes
         * represent the temperature value in IEEE-11073 32-bit Float format
         */
        private double decodeTemperature(byte[] data) throws Exception {
            double temperatureValue = 0.0;
            byte flag = data[0];
            byte exponential = data[4]; // 0xfe,即-2
            short firstOctet = convertNegativeByteToPositiveShort(data[1]);
            short secondOctet = convertNegativeByteToPositiveShort(data[2]);
            short thirdOctet = convertNegativeByteToPositiveShort(data[3]);
            int mantissa = ((thirdOctet << SHIFT_LEFT_16BITS)
                    | (secondOctet << SHIFT_LEFT_8BITS) | (firstOctet))
                    & HIDE_MSB_8BITS_OUT_OF_32BITS;
            Log.d(TAG, "mantissa " + mantissa + "expo " + exponential);
            mantissa = getTwosComplimentOfNegativeMantissa(mantissa);
            Log.d(TAG, "manstissa2 " + mantissa);
            temperatureValue = mantissa / 100.0;
            if ((flag & FIRST_BIT_MASK) != 0) {
                temperatureValue = (float) ((98.6 * temperatureValue - 32) * (5 / 9.0));
            }
            return temperatureValue;
        }

        private short convertNegativeByteToPositiveShort(byte octet) {
            if (octet < 0) {
                return (short) (octet & HIDE_MSB_8BITS_OUT_OF_16BITS);
            } else {
                return octet;
            }
        }

        private int getTwosComplimentOfNegativeMantissa(int mantissa) {
            if ((mantissa & GET_BIT24) != 0) {
                return ((((~mantissa) & HIDE_MSB_8BITS_OUT_OF_32BITS) + 1) * (-1));
            } else {
                return mantissa;
            }
        }

        private void readBatteryLevel() {
            mBatteryCharacteritsic = mBatteryService
                    .getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
            if (mBatteryCharacteritsic != null) {
                mBluetoothGatt.readCharacteristic(mBatteryCharacteritsic);
            }
        }

        private void ChangeHTP_Interval() {
            final byte[] interval_val = {0x0a, 0x00};
            mHTIntervalCharacter = mHTService
                    .getCharacteristic(HT_MEASUREMENT_INTERVAL_CHARACTERISTIC_UUID);
            mHTIntervalCharacter.setValue(interval_val);
            mBluetoothGatt.writeCharacteristic(mHTIntervalCharacter);
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public interface OnDataAvailableListener {
        void onCharacteristicRead(BluetoothGatt gatt,
                                  BluetoothGattCharacteristic characteristic, int status);

        void onCharacteristicWrite(BluetoothGatt gatt,
                                   BluetoothGattCharacteristic characteristic);
    }

    public void setOnDataAvailableListener(OnDataAvailableListener l) {
        mOnDataAvailableListener = l;
    }

    public boolean initialize() {
        if (getAdapter() == null)
            return false;
        return false;
    }

    //如果设备支持BLE，那么就可以获取蓝牙适配器。
    private BluetoothAdapter getAdapter() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    private class MyBluetoothBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {//蓝牙状态改变
                if (mBluetoothAdapter.isEnabled())
                    startScan();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {//绑定不同设备状态改变
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {//搜索到设备
                Log.i("ACTION_FOUND", "----------");
                newDevices.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {//搜索状态改变
            }
        }
    }

    private MyBluetoothBroadcastReceive myBluetoothBroadcastReceive = new MyBluetoothBroadcastReceive();
    private BleBinder myBinder;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myBluetoothBroadcastReceive, filter);
        initialize();
        myBinder = new BleBinder();
    }

    //打开蓝牙
    public void enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.enable();
                }
            }).start();
        }
    }

    //关闭蓝牙
    public void disableBluetooth() {
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
    }

    //扫描蓝牙
    public void startScan() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    //停止扫描........
    public void stopScan() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    //关闭设备
    public void closeBluetooth() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    //链接外设蓝牙
    public boolean connect(final String address) {
        stopScan();
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        return true;
    }

    public void disConnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public class BleBinder extends Binder {
        public void connectMachine(int machineId) {
            if (mBluetoothAdapter.isEnabled()) {
                startScan();
            } else {
                enableBluetooth();
            }
        }
    }
}
