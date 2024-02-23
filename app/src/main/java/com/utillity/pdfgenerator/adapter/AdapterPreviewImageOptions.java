package com.utillity.pdfgenerator.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.utillity.pdfgenerator.pdfModel.PreviewImageOptionItem;

import com.utillity.pdfgenerator.R;

public class AdapterPreviewImageOptions extends RecyclerView.Adapter<AdapterPreviewImageOptions.ViewHolder> {
    private final ArrayList<PreviewImageOptionItem> mOptions;
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;


    public AdapterPreviewImageOptions(OnItemClickListener onItemClickListener,
                                      ArrayList<PreviewImageOptionItem> optionItems, Context context) {
        mOnItemClickListener = onItemClickListener;
        mOptions = optionItems;
        mContext = context;
    }

    public void setSelectedItemPosition(int position) {
        int previousSelectedItemPosition = selectedItemPosition;
        selectedItemPosition = position;
        notifyItemChanged(previousSelectedItemPosition);
        notifyItemChanged(selectedItemPosition);
    }

    @NonNull
    @Override
    public AdapterPreviewImageOptions.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_image_options,
                parent, false);
        return new AdapterPreviewImageOptions.ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull AdapterPreviewImageOptions.ViewHolder holder, int position) {
        int imageId = mOptions.get(position).getOptionImageId();
        holder.imageView.setImageDrawable(mContext.getDrawable(imageId));
        holder.textView.setText(mOptions.get(position).getOptionName());

        if (position == selectedItemPosition) {
            holder.textView.setTextColor(Color.parseColor("#00A9FF"));
        } else {
            holder.textView.setTextColor(Color.parseColor("#80000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.option_image);
            textView = itemView.findViewById(R.id.option_name);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
