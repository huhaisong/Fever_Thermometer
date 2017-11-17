package com.sanyecao.hu.fever_thermometer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanyecao.hu.fever_thermometer.R;

import java.util.List;

/**
 * Created by huhaisong on 2017/9/7 17:49.
 */

public class HistoryNoteRecodeAdapter extends BaseAdapter {

    private List<String> items;
    private Context mContext;
    private LayoutInflater mInflater;

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public HistoryNoteRecodeAdapter(List<String> items, Context mContext, LayoutInflater mInflater) {
        this.items = items;
        this.mContext = mContext;
        this.mInflater = mInflater;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return getCount() != 0 ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_note_recode_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (getCount() != 0)
            holder.textView.setText(items.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
