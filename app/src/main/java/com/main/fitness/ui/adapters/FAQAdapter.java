package com.main.fitness.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.main.fitness.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQHolder> {
    private String[][] faq;

    public FAQAdapter(String[][] faq){
        this.faq = faq;
    }

    @NonNull
    @Override
    public FAQHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_faq, parent, false);
        return new FAQAdapter.FAQHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQHolder holder, int position) {
        String question = faq[position][0];
        String answer = faq[position][1];

        holder.questionView.setText(question);
        holder.answerView.setText(answer);
    }

    @Override
    public int getItemCount() {
        return faq.length;
    }

    static class FAQHolder extends RecyclerView.ViewHolder {
        public TextView questionView, answerView;
        public FAQHolder(@NonNull View itemView) {
            super(itemView);
            this.questionView = itemView.findViewById(R.id.row_faq_question);
            this.questionView.setOnClickListener(v -> {
                if (this.answerView.getVisibility() == View.VISIBLE){
                    this.answerView.setVisibility(View.GONE);
                }
                else{
                    this.answerView.setVisibility(View.VISIBLE);
                }
            });
            this.answerView = itemView.findViewById(R.id.row_faq_answer);
        }
    }
}
