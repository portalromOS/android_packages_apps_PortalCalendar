package com.portal.calendar.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.portal.calendar.R;

import java.util.ArrayList;

public class ColorItemAdapter extends BaseAdapter  {

    private Context context;
    private ArrayList<String> colors;

    LayoutInflater layoutInflater;
/*
    public ColorItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.colors = objects;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }
*/
    public ColorItemAdapter(Context applicationContext, ArrayList<String> objects) {
        this.context = applicationContext;
        this.colors = objects;
        this.context = context;
        layoutInflater = (LayoutInflater.from(applicationContext));
    }
/*
    public ColorItemAdapter(Context context, ArrayList<String> colors ){
        this.colors = colors;
        this.context = context;
    }
*/

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_color, null);

        String colorNameId = colors.get(pos);
        int colorId =  CalendarUtils.getColorIdByName(context, colorNameId);
        String colorHex = context.getResources().getString(colorId);

        View colorDiv = rootView.findViewById(R.id.color);


        colorDiv.setBackgroundColor(Color.parseColor(colorHex));
        //colorDiv.setBackgroundColor(colorId);
        //colorDiv.setBackgroundColor(Color.GREEN);
        //colorDiv.setBackgroundTintList(ColorStateList.valueOf(colorId));
        //colorDiv.setColorFilter(colorId);
/*

        TextView colorName = rootView.findViewById(R.id.text);
        colorName.setText(context.getResources().getString(colorId));
*/

        return rootView;
        /*
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_color, null, true);

        View div = rootView.findViewById(R.id.color);
        int colorId = context.getResources().getIdentifier(colors.get(pos), "color", context.getPackageName());
        div.setBackgroundColor(colorId);

        return super.getView(pos, convertView, parent);
        */
        /*
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_color, viewGroup, false);

        View div = rootView.findViewById(R.id.color);

        int colorId = context.getResources().getIdentifier(colors[pos], "color", context.getPackageName());
        div.setBackgroundColor(colorId);

        return rootView;
        */
    }   /*

    @Override
    public View getDropDownView(int pos, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.custom_spinner_color, null, true);

        TextView div = convertView.findViewById(R.id.color);
        String colorNameId = colors.get(pos);
        int colorId = context.getResources().getIdentifier(colorNameId, "color", context.getPackageName());
        div.setBackgroundColor(colorId);
        div.setText(colorNameId);
        return convertView;
    }
    */
}
