package com.utillity.pdfgenerator.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.utillity.pdfgenerator.R;
import com.utillity.pdfgenerator.interfaces.OnItemClickListener;
import com.utillity.pdfgenerator.pdfModel.FAQItem;

public class AdapterFAQ extends RecyclerView.Adapter<AdapterFAQ.FAQViewHolder> {

    private final List<FAQItem> mFaqs;
    private final OnItemClickListener mOnItemClickListener;

    public AdapterFAQ(List<FAQItem> faqs, OnItemClickListener mOnItemClickListener) {
        this.mFaqs = faqs;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_faq, viewGroup, false);
        return new AdapterFAQ.FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder viewHolder, int position) {
        FAQItem faqItem = mFaqs.get(position);
        viewHolder.question.setText(faqItem.getQuestion());
        viewHolder.answer.setText(faqItem.getAnswer());
        boolean isExpanded = faqItem.isExpanded();
        viewHolder.expandableView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mFaqs.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.question)
        TextView question;
        @BindView(R.id.answer)
        TextView answer;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;

        FAQViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            question.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
