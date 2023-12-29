package com.androidmarket.pdfcreator;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.List;

import androidmarket.R;

public class CustomAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> items;
    private int selectedItem = -1;
    public SharedPreferences preferences;

    public CustomAdapter(Context context, List<String> items) {
        super(context, R.layout.radio_button_layout, items);
        this.context = context;
        this.items = items;
//        preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        selectedItem = preferences.getInt("selectedItem", -1);
    }

//    public void setSelectedItem(int position) {
//        selectedItem = position;
//        preferences.edit().putInt("selectedItem", position).apply();
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.radio_button_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemTextView = convertView.findViewById(R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemTextView.setText(items.get(position));

//        if (position == selectedItem) {
//            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_blue20));
//            viewHolder.itemTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_blue));
//        } else {
//            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
//            viewHolder.itemTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
//        }
        return convertView;
    }

    private static class ViewHolder {
        TextView itemTextView;
    }
}
