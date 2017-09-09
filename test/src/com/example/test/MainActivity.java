package com.example.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test.DeviceListAdapter.DeviceEntry;

public class MainActivity extends Activity {

	public int is_bt_opened = 0;
	final String BT_TAG = "[BLE]";

	public final static UUID HR_SERVICE_UUID = UUID
			.fromString("0000180D-0000-1000-8000-00805f9b34fb");
	private static final UUID HR_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID
			.fromString("00002A38-0000-1000-8000-00805f9b34fb");

	private static final UUID HR_CHARACTERISTIC_UUID = UUID
			.fromString("00002A37-0000-1000-8000-00805f9b34fb");

	private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private final static UUID BATTERY_SERVICE = UUID
			.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID
			.fromString("00002A19-0000-1000-8000-00805f9b34fb");
	/** Newco implementation of the battery level characteristic */
	private final static UUID BATTERY_LEVEL_STATE_CHARACTERISTIC = UUID
			.fromString("00002A1B-0000-1000-8000-00805f9b34fb");
	public final static UUID HT_SERVICE_UUID = UUID
			.fromString("00001809-0000-1000-8000-00805f9b34fb");

	private static final UUID HT_MEASUREMENT_CHARACTERISTIC_UUID = UUID
			.fromString("00002A1C-0000-1000-8000-00805f9b34fb");

	private static final UUID HT_IMEDIATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID
			.fromString("00002A1E-0000-1000-8000-00805f9b34fb");

	private static final UUID HT_MEASUREMENT_INTERVAL_CHARACTERISTIC_UUID = UUID
			.fromString("00002A21-0000-1000-8000-00805f9b34fb");
	private BluetoothGattCharacteristic mHRCharacteristic,
			mHRLocationCharacteristic, mBatteryCharacteritsic,
			mBatteryStateCharacteristic;
	private final int HIDE_MSB_8BITS_OUT_OF_32BITS = 0x00FFFFFF;
	private final int HIDE_MSB_8BITS_OUT_OF_16BITS = 0x00FF;
	private final int SHIFT_LEFT_8BITS = 8;
	private final int SHIFT_LEFT_16BITS = 16;
	private final int GET_BIT24 = 0x00400000;
	private static final int FIRST_BIT_MASK = 0x01;

	private boolean isHRServiceFound = false;
	private boolean isNotificationEnable = false;
	private boolean isBatteryServiceFound = false;
	private boolean isBatteryStateDescriptorWritten = false;
	private boolean isBatteryStateCharacteristicFound = false;

	private BluetoothGattCharacteristic mHTCharacteristic,
			mHTIntervalCharacter;
	private BluetoothGattService mHTService, mBatteryService;

	private boolean isHTServiceFound = false;

	public BluetoothGatt mBluetoothGatt;

	public ArrayList<BluetoothDevice> inquiry_device_list;
	public ArrayList<String> device_name_list;
	public ArrayList<DeviceEntry> deviceList;

	public DeviceListAdapter deviceListAdapter;
	public BluetoothAdapter bt_local_dev;

	private Handler handler; // callbac给主线程发消息

	public BroadcastReceiver acl_connected_hook = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(BT_TAG, "acl_connected\n");
		}
	};
	public BroadcastReceiver acl_pair_request_hook = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int mType;
			Log.e(BT_TAG, "smp pairing\n");
			mType = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
					BluetoothDevice.ERROR);
		}
	};

	class MyHandler extends Handler {
		public MyHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == 2) {
				Log.d(BT_TAG, "Message\n\n\n");
				deviceListAdapter.notifyDataSetChanged();
			}
			// textView.setText("我是主线程的Handler，收到了消息："+(String)msg.obj);
		}
	}

	public void MySendMessage(int message) {

		// deviceListAdapter.notifyDataSetChanged();
		Looper looper = Looper.getMainLooper(); // 主线程的Looper对象
												// //这里以主线程的Looper对象创建了handler，
												// //所以，这个handler发送的Message会被传递给主线程的MessageQueue。
		handler = new MyHandler(looper);
		// 构建Message对象
		// 第一个参数：是自己指定的message代号，方便在handler选择性地接收
		// 第二三个参数没有什么意义 //第四个参数需要封装的对象
		Message msg = handler.obtainMessage(message, 2, 2, null);
		handler.sendMessage(msg); // 发送消息
	}

	// need to check whether the new_bt is new device, and add to list if new
	public DeviceEntry DeviceListCheckAdd(ArrayList<DeviceEntry> bt_list,
			BluetoothDevice new_bt) {
		int i = 0;
		DeviceEntry new_entry;
		for (i = 0; i < bt_list.size(); i++) {
			// if (bt_list.get(i).device.getAddress() == new_bt.getAddress())
			if (bt_list.get(i).device.getAddress().equals(new_bt.getAddress())) {
				// Log.d(BT_TAG, "repeat");
				return bt_list.get(i);
			}
		}
		// deviceListAdapter.notifyDataSetChanged();
		Log.d(BT_TAG, "foud" + new_bt.getName());
		new_entry = new DeviceEntry(new_bt, 0);
		bt_list.add(new_entry);

		MySendMessage(2);
		return new_entry;
	}

	public BroadcastReceiver discovery_result = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice remoteDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// inquiry_device_list.add(remoteDevice);
			// device_name_list.add(remoteDeviceName);
			// aa.notifyDataSetChanged();

			// deviceList.add(new DeviceEntry(remoteDevice, 0));
			DeviceListCheckAdd(deviceList, remoteDevice);
			deviceListAdapter.notifyDataSetChanged();

			Log.d(BT_TAG, "found:" + remoteDevice.getName());
			/*
			 * if (remoteDevice.createBond() == true) { Log.d(BT_TAG,
			 * "start bonding\n"); } else { Log.d(BT_TAG, "fail to bond\n"); }
			 */
		}

	};

	public class ParsedAd {
		byte flags;
		String localName;
		short manufacturer;

		public ParsedAd() {
			return;
		}
	}

	public void parseData(byte[] adv_data) {
		// ParsedAd parsedAd = new ParsedAd();
		ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(
				ByteOrder.LITTLE_ENDIAN);
		while (buffer.remaining() > 2) {
			byte length = buffer.get();
			if (length == 0)
				break;

			byte type = buffer.get();
			length -= 1;
			switch (type) {
			case 0x01: // Flags
				// parsedAd.flags = buffer.get();
				length--;
				Log.d(BT_TAG, "Flags=" + buffer.get());
				break;
			case 0x02: // Partial list of 16-bit UUIDs
			case 0x03: // Complete list of 16-bit UUIDs
			case 0x14: // List of 16-bit Service Solicitation UUIDs
				Log.d(BT_TAG, "UUID " + type);
				while (length >= 2) {
					// parsedAd.uuids.add(UUID.fromString(String.format(
					// "%08x-0000-1000-8000-00805f9b34fb",
					// buffer.getShort())));
					Log.d(BT_TAG, "uuids:" + buffer.getShort());
					length -= 2;
				}
				break;
			case 0x04: // Partial list of 32 bit service UUIDs
			case 0x05: // Complete list of 32 bit service UUIDs
				while (length >= 4) {
					// parsedAd.uuids
					// .add(UUID.fromString(String.format(
					// "%08x-0000-1000-8000-00805f9b34fb",
					// buffer.getInt())));
					buffer.getInt();
					length -= 4;
				}
				break;
			case 0x06: // Partial list of 128-bit UUIDs
			case 0x07: // Complete list of 128-bit UUIDs
			case 0x15: // List of 128-bit Service Solicitation UUIDs
				while (length >= 16) {
					// long lsb = buffer.getLong();
					// long msb = buffer.getLong();
					// parsedAd.uuids.add(new UUID(msb, lsb));

					Log.d(BT_TAG, "UUID:" + buffer.getLong() + buffer.getLong());
					length -= 16;
				}
				break;
			case 0x08: // Short local device name
			case 0x09: // Complete local device name
				byte sb[] = new byte[length];
				String Localname;
				buffer.get(sb, 0, length);
				length = 0;
				// parsedAd.localName = new String(sb).trim();
				Localname = new String(sb).trim();
				Log.d(BT_TAG, "LocalName=" + Localname);
				break;
			case (byte) 0xcf: // Manufacturer Specific Data
				// parsedAd.manufacturer = buffer.getShort();
				Log.d(BT_TAG, "CV=");
				while (length >= 1) {
					// parsedAd.uuids
					// .add(UUID.fromString(String.format(
					// "%08x-0000-1000-8000-00805f9b34fb",
					// buffer.getInt())));
					Log.d(BT_TAG, " " + buffer.get());
					length -= 1;
				}
				break;
			default: // skip
				break;
			}
			if (length > 0) {
				buffer.position(buffer.position() + length);
			}
		}
		return;// parsedAd;
	}

	public LeScanCallback leScanHook = new LeScanCallback() {
		/**
		 * Callback reporting an LE device found during a device scan initiated
		 * by the {@link BluetoothAdapter#startLeScan} function.
		 * 
		 * @param device
		 *            Identifies the remote device
		 * @param rssi
		 *            The RSSI value for the remote device as reported by the
		 *            Bluetooth hardware. 0 if no RSSI value is available.
		 * @param scanRecord
		 *            The content of the advertisement record offered by the
		 *            remote device.
		 */
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			DeviceEntry new_entry;
			Log.d(BT_TAG, "Found" + device.getName() + " " + rssi + " "
					+ scanRecord);
			parseData(scanRecord);
			new_entry = DeviceListCheckAdd(deviceList, device);
			if (new_entry != null) {
				new_entry.rssi = rssi;
				// deviceListAdapter.notifyDataSetChanged();
			}

		}
	};

	public BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
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
			DeviceEntry deviceItem = deviceListAdapter.getItemByGatt(gatt);
			if (deviceItem != null) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					deviceItem.state = newState;
					// deviceListAdapter.notifyDataSetChanged();
					MySendMessage(2);
					if (newState == BluetoothProfile.STATE_CONNECTED) {
						Log.d(BT_TAG, "new connect device\n");
						gatt.discoverServices();
					}
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
			Log.d(BT_TAG, "services discovered\n");

			DeviceEntry deviceItem = deviceListAdapter.getItemByGatt(gatt);
			if (deviceItem != null) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					List<BluetoothGattService> services = gatt.getServices();
					for (BluetoothGattService service : services) {
						if (service.getUuid().equals(HT_SERVICE_UUID)) {
							isHTServiceFound = true;
							mHTService = service;
							Log.e(BT_TAG, "found hts\n");
						}
						if (service.getUuid().equals(BATTERY_SERVICE)) {
							mBatteryService = service;
							isBatteryServiceFound = true;
							Log.e(BT_TAG, "found bas\n");
						}
					}
					if (isHTServiceFound) {
						// mCallbacks.onServicesDiscovered(false);
						// enableHTIndication();
					} else {
						// mCallbacks.onDeviceNotSupported();
						gatt.disconnect();
						return;
					}
					if (isBatteryServiceFound) {
						readBatteryLevel();
					} else if (isHTServiceFound) {

					}
				} else {
					// mCallbacks.onError(ERROR_DISCOVERY_SERVICE, status);
				}
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.d(BT_TAG, "cr");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (characteristic.getUuid().equals(
						BATTERY_LEVEL_CHARACTERISTIC)) {
					int batteryValue = characteristic.getValue()[0];
					Log.d(BT_TAG, "Battery: " + batteryValue);
					// mCallbacks.onBatteryValueReceived(batteryValue);

					// enableBatteryNTF();
					if (isHTServiceFound) {
						// enableHTIndication();
						ChangeHTP_Interval();
					}
				}
			} else {
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			double tempValue = 0.0;
			DeviceEntry device;
			Log.e(BT_TAG, "value changed\n");
			if ((characteristic.getUuid()
					.equals(HT_IMEDIATE_MEASUREMENT_CHARACTERISTIC_UUID))
					|| (characteristic.getUuid()
							.equals(HT_MEASUREMENT_CHARACTERISTIC_UUID))) {
				try {
					tempValue = decodeTemperature(characteristic.getValue());
					Log.e(BT_TAG, "Temp value: " + tempValue);
					// mCallbacks.onHTValueReceived(tempValue);
					device = deviceListAdapter.getItemByGatt(gatt);
					if ((device != null)
							&& (Double.compare(device.temp_value, tempValue) != 0)) {
						device.temp_value = tempValue;
						MySendMessage(2);
					}
				} catch (Exception e) {
					Log.e(BT_TAG, "invalid temperature value");
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
			Log.d(BT_TAG, "mantissa " + mantissa + "expo " + exponential);
			mantissa = getTwosComplimentOfNegativeMantissa(mantissa);
			Log.d(BT_TAG, "manstissa2 " + mantissa);
			// /temperatureValue = (mantissa * Math.pow(10, exponential));
			temperatureValue = mantissa / 100.0;

			/*
			 * Conversion of temperature unit from Fahrenheit to Celsius if unit
			 * is in Fahrenheit Celsius = (98.6*Fahrenheit -32) 5/9
			 */
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

		/**
		 * Callback indicating the result of a characteristic write operation.
		 * 
		 * <p>
		 * If this callback is invoked while a reliable write transaction is in
		 * progress, the value of the characteristic represents the value
		 * reported by the remote device. An application should compare this
		 * value to the desired value to be written. If the values don't match,
		 * the application must abort the reliable write transaction.
		 * 
		 * @param gatt
		 *            GATT client invoked
		 *            {@link BluetoothGatt#writeCharacteristic}
		 * @param characteristic
		 *            Characteristic that was written to the associated remote
		 *            device.
		 * @param status
		 *            The result of the write operation
		 *            {@link BluetoothGatt#GATT_SUCCESS} if the operation
		 *            succeeds.
		 */
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
		}

		/**
		 * Callback reporting the result of a descriptor read operation.
		 * 
		 * @param gatt
		 *            GATT client invoked {@link BluetoothGatt#readDescriptor}
		 * @param descriptor
		 *            Descriptor that was read from the associated remote
		 *            device.
		 * @param status
		 *            {@link BluetoothGatt#GATT_SUCCESS} if the read operation
		 *            was completed successfully
		 */
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
		}

		/**
		 * Callback indicating the result of a descriptor write operation.
		 * 
		 * @param gatt
		 *            GATT client invoked {@link BluetoothGatt#writeDescriptor}
		 * @param descriptor
		 *            Descriptor that was writte to the associated remote
		 *            device.
		 * @param status
		 *            The result of the write operation
		 *            {@link BluetoothGatt#GATT_SUCCESS} if the operation
		 *            succeeds.
		 */
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
		}

		/**
		 * Callback invoked when a reliable write transaction has been
		 * completed.
		 * 
		 * @param gatt
		 *            GATT client invoked
		 *            {@link BluetoothGatt#executeReliableWrite}
		 * @param status
		 *            {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
		 *            transaction was executed successfully
		 */
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		}

		/**
		 * Callback reporting the RSSI for a remote device connection.
		 * 
		 * This callback is triggered in response to the
		 * {@link BluetoothGatt#readRemoteRssi} function.
		 * 
		 * @param gatt
		 *            GATT client invoked {@link BluetoothGatt#readRemoteRssi}
		 * @param rssi
		 *            The RSSI value for the remote device
		 * @param status
		 *            {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read
		 *            successfully
		 */
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		}

		private void readBatteryLevel() {
			mBatteryCharacteritsic = mBatteryService
					.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
			if (mBatteryCharacteritsic != null) {
				mBluetoothGatt.readCharacteristic(mBatteryCharacteritsic);
			}
		}

		private void enableHTIndication() {
			Log.e(BT_TAG, "enableHTIndication()");
			mHTCharacteristic = mHTService
					.getCharacteristic(HT_MEASUREMENT_CHARACTERISTIC_UUID);
			mBluetoothGatt.setCharacteristicNotification(mHTCharacteristic,
					true);
			BluetoothGattDescriptor descriptor = mHTCharacteristic
					.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE/* ENABLE_NOTIFICATION_VALUE */);// ENABLE_INDICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}

		private void ChangeHTP_Interval() {
			Log.e(BT_TAG, "ChangeHTP_Interval()");
			final byte[] interval_val = { 0x0a, 0x00 };
			mHTIntervalCharacter = mHTService
					.getCharacteristic(HT_MEASUREMENT_INTERVAL_CHARACTERISTIC_UUID);
			mHTIntervalCharacter.setValue(interval_val);
			mBluetoothGatt.writeCharacteristic(mHTIntervalCharacter);
		}

		// 使能电池电量Notify
		private void enableBatteryNTF() {
			Log.e(BT_TAG, "enableBatteryNTF()");
			mBatteryCharacteritsic = mBatteryService
					.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
			mBluetoothGatt.setCharacteristicNotification(
					mBatteryCharacteritsic, true);
			BluetoothGattDescriptor descriptor = mBatteryCharacteritsic
					.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE/* ENABLE_NOTIFICATION_VALUE */);// ENABLE_INDICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}

		/**
		 * Writing 1 to descriptor of Battery Level State characteristic
		 */
		private void writeBatteryStateDescriptor() {
			if (isBatteryStateCharacteristicFound) {
				BluetoothGattDescriptor descriptor = mBatteryStateCharacteristic
						.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
				byte[] value = new byte[1];
				value[0] = 1;
				descriptor.setValue(value);
				isBatteryStateDescriptorWritten = true;
				Log.e(BT_TAG, "writing battery state desciptor");
				mBluetoothGatt.writeDescriptor(descriptor);
			}
		}

		/**
		 * Disabling notification on Heart Rate Characteristic
		 */
		private void disableNotification() {
			if (isNotificationEnable) {
				mBluetoothGatt.setCharacteristicNotification(mHRCharacteristic,
						false);
				BluetoothGattDescriptor descriptor = mHRCharacteristic
						.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
				descriptor
						.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);
				isNotificationEnable = false;
			}
		}
	};

	@Override
	public void finish() {
		super.finish();
		Log.v(BT_TAG, "finish");
		bt_local_dev.stopLeScan(leScanHook);
		/** android NOT guarantee that onDestroy() follows finish() */
		// /releaseAll();
		unregisterReceiver(discovery_result);
		unregisterReceiver(acl_connected_hook);
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inquiry_device_list = new ArrayList<BluetoothDevice>();
		device_name_list = new ArrayList<String>();
		deviceList = new ArrayList<DeviceEntry>();

		final Button button1 = (Button) findViewById(R.id.button_open_bt);

		final Button start_search_button = (Button) findViewById(R.id.button_start_search);
		final TextView textview1 = (TextView) findViewById(R.id.textview_debug);
		final TextView bt_name_text_view = (TextView) findViewById(R.id.textview_bt_local_name);
		ListView myListView = (ListView) findViewById(R.id.search_device_list);
		final ArrayAdapter<String> aa;

		// aa = new ArrayAdapter<BluetoothDevice>(this,
		// android.R.layout.simple_list_item_1, inquiry_device_list);
		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, device_name_list);
		deviceListAdapter = new DeviceListAdapter(this, deviceList);

		myListView.setOnItemClickListener(mOnItemClickListener);

		// myListView.setAdapter(aa);
		myListView.setAdapter(deviceListAdapter);

		bt_local_dev = BluetoothAdapter.getDefaultAdapter();

		// bt_local_dev.

		// textview1.setText(bt_local_dev.getAddress());
		// bt_name_text_view.setText(bt_local_dev.getName());
		registerReceiver(discovery_result, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		registerReceiver(acl_connected_hook, new IntentFilter(
				BluetoothDevice.ACTION_ACL_CONNECTED));
		registerReceiver(acl_pair_request_hook, new IntentFilter(
				BluetoothDevice.ACTION_BOND_STATE_CHANGED));
		registerReceiver(acl_pair_request_hook, new IntentFilter(
				BluetoothDevice.EXTRA_PAIRING_VARIANT));

		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (is_bt_opened == 0) {

					button1.setText(R.string.str_close_bt);
					bt_local_dev.enable();
					is_bt_opened = 1;
					Log.d(BT_TAG, "open bt\n");
				} else {
					is_bt_opened = 0;
					button1.setText(R.string.str_open_bt);
					bt_local_dev.disable();
					Log.d(BT_TAG, "close bt\n");
				}

			}
		});

		start_search_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((bt_local_dev.isEnabled())
						&& (!bt_local_dev.isDiscovering())) {

					// bt_local_dev.startDiscovery();
					bt_local_dev.startLeScan(leScanHook);
					Log.d(BT_TAG, "le scanning\n");
					deviceList.clear();
					deviceListAdapter.notifyDataSetChanged();
					// Intent intent = new Intent(MainActivity.this,
					// bt_remote_device_list.class);
					// Bundle bundle = new Bundle();
					// Bundle.putObject("local_device", bt_local_dev);
					// intent.putExtra("key_bt_dev", (int)bt_local_dev);
					// startActivity(intent);
				}
			}
		});

	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(BT_TAG, "click" + position + id);
			DeviceEntry device = (DeviceEntry) parent
					.getItemAtPosition(position);
			if (device != null) {
				switch (device.state) {

				case BluetoothProfile.STATE_DISCONNECTED:
					Log.d(BT_TAG, "createBond " + device.device.getName());
					/*
					 * if (device.device.createBond() == true) { Log.d(BT_TAG,
					 * "connecting\n"); device.state = 1;
					 * deviceListAdapter.notifyDataSetChanged(); } else {
					 * Log.d(BT_TAG, "connect fail\n"); }
					 */
					device.gatt = device.device.connectGatt(
							getApplicationContext(), false, gattCallback);
					bt_local_dev.stopLeScan(leScanHook);
					mBluetoothGatt = device.gatt;
					break;
				case BluetoothProfile.STATE_CONNECTING:

					break;
				case BluetoothProfile.STATE_CONNECTED:

					device.gatt.disconnect();
					break;
				}
			}
			// DeviceEntry deviceEntry = (DeviceEntry)
			// parent.getItemAtPosition(position);
			// mSearchProgressBar.setVisibility(View.INVISIBLE);
			// mBluzConnector.connect(deviceEntry.device);
		}
	};
}
