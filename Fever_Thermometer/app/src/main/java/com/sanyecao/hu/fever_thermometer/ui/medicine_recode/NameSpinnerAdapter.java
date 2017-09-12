package com.sanyecao.hu.fever_thermometer.ui.medicine_recode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.BabyBean;
import com.sanyecao.hu.fever_thermometer.ui.view.GlideCircleTransform;

import java.util.List;

/**
 * Created by huhaisong on 2017/8/19 14:57.
 */

public class NameSpinnerAdapter extends BaseAdapter {

    private List<BabyBean> items;
    private Context mContext;
    private LayoutInflater mInflater;

    public NameSpinnerAdapter(List<BabyBean> items, Context mContext, LayoutInflater inflater) {
        this.items = items;
        this.mContext = mContext;
        mInflater = inflater;
    }

    public void setItems(List<BabyBean> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            convertView = mInflater.inflate(R.layout.spinner_name_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(items.get(position).getName());
        Glide.with(mContext)
                .load(items.get(position).getImage_url())
                .error(R.drawable.header)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.imageView);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
