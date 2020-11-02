//Custom adapter for ability to set default name in spinner appearance

package com.coldkitchen.calcula;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import static android.graphics.Color.WHITE;

public class CustomAdapter extends ArrayAdapter {
    private Context context;
    private int textViewResourceId;
    private ArrayList<String> objects;

    public CustomAdapter(Context context, int textViewResourceId,
                         ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);

            TextView tv = (TextView) convertView;

            tv.setText("Formulas");
            tv.setTextColor(WHITE);

        return convertView;
    }
}