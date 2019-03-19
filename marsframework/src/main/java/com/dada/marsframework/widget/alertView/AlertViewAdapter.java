package com.dada.marsframework.widget.alertView;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dada.marsframework.R;
import com.dada.marsframework.utils.DensityUtils;


/**
 * Created by laidayuan on 2018/3/23.
 */

public class AlertViewAdapter extends BaseAdapter {
    private List<String> mDatas;
    private List<String> mDestructive;

    public AlertViewAdapter(List<String> datas, List<String> destructive) {
        this.mDatas = datas;
        this.mDestructive = destructive;
    }

    private boolean alignLeft = false;

    public void setAlignLeft() {
        alignLeft = true;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = mDatas.get(position);
        Holder holder = null;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_alertbutton, null);

            holder = creatHolder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (alignLeft) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvAlert.getLayoutParams();
            params.leftMargin = DensityUtils.dip2px(parent.getContext(), 10);
            holder.tvAlert.setLayoutParams(params);
            holder.tvAlert.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        } else {
            holder.tvAlert.setGravity(Gravity.CENTER);
        }

        holder.UpdateUI(parent.getContext(), data, position);
        return view;
    }

    public Holder creatHolder(View view) {
        return new Holder(view);
    }

    class Holder {
        public TextView tvAlert;

        public Holder(View view) {
            tvAlert = (TextView) view.findViewById(R.id.tvAlert);
        }

        public void UpdateUI(Context context, String data, int position) {
            tvAlert.setText(data);
            if (mDestructive != null && mDestructive.contains(data)) {
                tvAlert.setTextColor(context.getResources().getColor(
                        R.color.textColor_alert_button_destructive));
            } else {
                tvAlert.setTextColor(context.getResources().getColor(
                        R.color.textColor_alert_button_others));
            }
        }
    }
}