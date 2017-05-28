package com.example.neba.cool;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by NEBA on 14-Aug-16.
 */
public class MessageAdapter extends BaseAdapter {
    private Context context;
    public static ArrayList<Option> options;
    private DBHelper dbhelper;
    private Integer status;
    private static int post;
    private Activity activity;

    public MessageAdapter(Context context, ArrayList<Option> options) {
        this.context = context;
        this.options = options;
        dbhelper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.message_adapter, null);

        LinearLayout RL = (LinearLayout) convertView.findViewById(R.id.LLmsg);
        if (options.get(position).getSeen().equals(0)) {
            RL.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eeeeee")));
        }

        TextView tvtitle = (TextView) convertView.findViewById(R.id.tvtitle);
        TextView tvmessage = (TextView) convertView.findViewById(R.id.tvmsg);
        tvtitle.setText(options.get(position).getTitle() + " [" + options.get(position).getDate() + "]");
        tvmessage.setText(options.get(position).getMessage());

        return convertView;

    }
}
