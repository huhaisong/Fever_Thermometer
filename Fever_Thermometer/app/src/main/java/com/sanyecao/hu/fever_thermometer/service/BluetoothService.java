package com.sanyecao.hu.fever_thermometer.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.sanyecao.hu.fever_thermometer.ui.temperature.MachineFragment.MESSAGE_CONNECT_OUT_TIME;
import static com.sanyecao.hu.fever_thermometer.ui.temperature.MachineFragment.MESSAGE_UPDATE_BATTERY_LEVEL;
import static com.sanyecao.hu.fever_thermometer.ui.temperature.MachineFragment.MESSAGE_UPDATE_CONNECTED_STATE;
import static com.sanyecao.hu.fever_thermometer.ui.temperature.MachineFragment.MESSAGE_UPDATE_TEMPERATURE;

/**
 * Created by huhaisong on 2017/8/31 9:59.
 * 蓝牙温度计设备service
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

    private static final int FIRST_BIT_MASK = 0x01;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private String adress;

    public BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        private final int HIDE_MSB_8BITS_OUT_OF_16BITS = 0x00FF;
        private final int SHIFT_LEFT_8BITS = 8;
        private final int SHIFT_LEFT_16BITS = 16;
        private final int GET_BIT24 = 0x00400000;
        private BluetoothGattCharacteristic mHTIntervalCharacter;
        private BluetoothGattCharacteristic mHTCharacteristic;
        private BluetoothGattCharacteristic mBatteryCharacteristic;
        private boolean isBatteryServiceFound = false;
        private boolean isHTServiceFound = false;
        private BluetoothGattService mHTService, mBatteryService;

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (gatt != mBluetoothGatt)
                return;
            Log.e(TAG, "onConnectionStateChange: status = " + status + ",newState = " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mHandler.sendEmptyMessage(MESSAGE_UPDATE_CONNECTED_STATE);
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connect(adress);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT_OUT_TIME, 3 * 1000);
                }
            } else if (status == 133) {
                connect(adress);
                mHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT_OUT_TIME, 3 * 1000);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (gatt != mBluetoothGatt)
                return;
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
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(
                        BATTERY_LEVEL_CHARACTERISTIC)) {
                    int batteryValue = characteristic.getValue()[0];
                    Message message = Message.obtain();
                    message.arg1 = batteryValue;
                    message.what = MESSAGE_UPDATE_BATTERY_LEVEL;
                    mHandler.sendMessage(message);
                    if (isHTServiceFound) {
                        enableHTIndication();
                        ChangeHTP_Interval();
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            double tempValue;
            if ((characteristic.getUuid().equals(HT_IMEDIATE_MEASUREMENT_CHARACTERISTIC_UUID))
                    || (characteristic.getUuid().equals(HT_MEASUREMENT_CHARACTERISTIC_UUID))) {
                try {
                    //获得的温度
                    tempValue = decodeTemperature(characteristic.getValue());
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("temperature", tempValue);
                    message.setData(bundle);
                    message.what = MESSAGE_UPDATE_TEMPERATURE;
                    mHandler.sendMessage(message);
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
            double temperatureValue;
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
            mBatteryCharacteristic = mBatteryService
                    .getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
            if (mBatteryCharacteristic != null) {
                mBluetoothGatt.readCharacteristic(mBatteryCharacteristic);
            }
        }

        private void ChangeHTP_Interval() {
            final byte[] interval_val = {0x0a, 0x00};
            mHTIntervalCharacter = mHTService
                    .getCharacteristic(HT_MEASUREMENT_INTERVAL_CHARACTERISTIC_UUID);
            mHTIntervalCharacter.setValue(interval_val);
            mBluetoothGatt.writeCharacteristic(mHTIntervalCharacter);
        }

        private void enableHTIndication() {
            Log.e(TAG, "enableHTIndication()");
            mHTCharacteristic = mHTService.getCharacteristic(HT_MEASUREMENT_CHARACTERISTIC_UUID);
            mBluetoothGatt.setCharacteristicNotification(mHTCharacteristic, true);
            BluetoothGattDescriptor descriptor = mHTCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE/* ENABLE_NOTIFICATION_VALUE */);// ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return myBinder;
    }

    public boolean initialize() {
        if (getAdapter() == null)
            return false;
        return false;
    }

    //如果设备支持BLE，那么就可以获取蓝牙适配器。
    private BluetoothAdapter getAdapter() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    private BleBinder myBinder;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
        initialize();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(myBroadcastReceiver, filter);
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
    /*public void disableBluetooth() {
        if (mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
    }*/

    //扫描蓝牙
    public void startScan() {
        Log.e(TAG, "startScan: ");
        stopScan();
        closeBluetooth();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startLeScan(leScanHook);
    }

    //停止扫描........
    public void stopScan() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.stopLeScan(leScanHook);
    }

    //关闭设备
    public void closeBluetooth() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    //链接外设蓝牙
    public boolean connect(final String address) {
        Log.e(TAG, "connect: ");
        stopScan();
        closeBluetooth();
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(getBaseContext(), false, mGattCallback);
        return true;
    }

    public class BleBinder extends Binder {
        public void connectMachine(Handler handler) {
            mHandler = handler;
            if (mBluetoothAdapter.isEnabled()) {
                startScan();
            } else {
                enableBluetooth();
            }
        }

        public void closeBluetoothService() {
            closeBluetooth();
        }

        public void stopServiceScan() {
            stopScan();
        }

        public boolean getEnableState() {
            return mBluetoothAdapter.isEnabled();
        }
    }

    public BluetoothAdapter.LeScanCallback leScanHook = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG, "Found:" + device.getName() + " " + rssi + " " + Arrays.toString(scanRecord));
            if (device.getName().equals("CLVTK")) {
                adress = device.getAddress();
                connect(adress);
            }
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        closeBluetooth();
        unregisterReceiver(myBroadcastReceiver);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                startScan();
            }
        }
    };
}
