package com.utillity.pdfgenerator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.utillity.pdfgenerator.R;
import com.utillity.pdfgenerator.interfaces.OnFilterItemClickedListener;
import com.utillity.pdfgenerator.pdfModel.FilterItem;
import com.utillity.pdfgenerator.util.ImageUtils;

public class AdapterImageFilters extends RecyclerView.Adapter<AdapterImageFilters.ViewHolder> {

    private final ArrayList<FilterItem> mFilterItem;
    private final OnFilterItemClickedListener mOnFilterItemClickedListener;
    private final Context  mContext;
    private int selectedItemPosition = 0;

    public AdapterImageFilters(ArrayList<FilterItem> filterItems, Context context,
                               OnFilterItemClickedListener listener) {
        mFilterItem = filterItems;
        mContext = context;
        mOnFilterItemClickedListener = listener;
    }

    public void setSelectedItemPosition(int position) {
        int previousSelectedItemPosition = selectedItemPosition;
        selectedItemPosition = position;
        notifyItemChanged(previousSelectedItemPosition);
        notifyItemChanged(selectedItemPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageId = mFilterItem.get(position).getImageId();
        Bitmap roundBitmap = BitmapFactory.decodeResource(mContext.getResources(), imageId);
        if (roundBitmap != null) {
            holder.img.setImageBitmap(ImageUtils.getInstance().getRoundBitmap(roundBitmap));
        } else
            holder.img.setImageResource(imageId);
        holder.name.setText(mFilterItem.get(position).getName());

        if (position == selectedItemPosition) {
            holder.name.setTextColor(Color.parseColor("#00A9FF"));
        } else {
            holder.name.setTextColor(Color.parseColor("#80000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mFilterItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.filter_preview)
        ImageView img;
        @BindView(R.id.filter_Name)
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnFilterItemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }
}
