package com.example.test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
	private List<DeviceEntry> mItems; // 仅仅是指针
	private LayoutInflater inflater;
	private String[] stateStr;
	private final static double INVALID_TEMP_VALUE = 1000;
	private final static int INVALID_BAT_VALUE = 1000;

	public DeviceListAdapter(Context context, List<DeviceEntry> data) {
		inflater = LayoutInflater.from(context);
		mItems = data;
		stateStr = context.getResources().getStringArray(R.array.bt_state_item);
	}

	public static class DeviceEntry {
		public BluetoothDevice device; // 设备
		public BluetoothGatt gatt;
		public int state; // 设备状态
		public int rssi;
		public BluetoothGattService bas;
		public BluetoothGattService hts;
		public double temp_value; // 温度值,摄氏度
		public int battery_value; // 电池电量，百分比

		public DeviceEntry(BluetoothDevice s_device, int s_state) {
			device = s_device;
			state = s_state;
			rssi = -255;
			gatt = null;
			bas = null;
			hts = null;
			temp_value = INVALID_TEMP_VALUE;
			battery_value = INVALID_BAT_VALUE;
		}
	}

	public class itemHolder {
		// img,title,infor,
		public ImageView image = null;
		public TextView title = null;
		public TextView info = null;
		public TextView rssi = null;
		public TextView temp = null;
		public TextView bat = null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return 0;
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		// return null;
		return mItems.get(position);
	}

	public DeviceEntry getItemByGatt(BluetoothGatt gatt) {
		int i;
		for (i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).gatt == gatt) {
				return mItems.get(i);
			}
		}

		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		// return 0;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		itemHolder holder;
		DeviceEntry item;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bt_list_item, null);
			holder = new itemHolder();
			if (holder != null) {
				holder.image = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.rssi = (TextView) convertView.findViewById(R.id.rssi);
				holder.temp = (TextView) convertView.findViewById(R.id.temp_value);
				holder.bat = (TextView) convertView.findViewById(R.id.bat_value);
			}
		} else {
			holder = (itemHolder) convertView.getTag();
		}

		item = mItems.get(position);
		if ((item == null) || (item.device == null)) {
			holder.title.setText(R.string.no_device);
		} else {
			if (item.device.getName() != null) {
				holder.title.setText(item.device.getName());
			} else {
				holder.title.setText(item.device.getAddress());
			}
		}
		if (item.state < stateStr.length - 1) {
			holder.info.setText(stateStr[item.state]);
		} else {
			holder.info.setText(stateStr[stateStr.length]);
		}

		if (item.temp_value != INVALID_TEMP_VALUE) {
			holder.temp.setText("     " + Double.toString(item.temp_value) + "℃");
		}

		/*
		 * if (item.rssi == -255) {
		 * holder.rssi.setText(R.string.rssi_null+R.string.rssi_unit); } else {
		 * holder.rssi.setText(item.rssi+ R.string.rssi_unit); }
		 */
		convertView.setTag(holder);
		return convertView;
	}
}
