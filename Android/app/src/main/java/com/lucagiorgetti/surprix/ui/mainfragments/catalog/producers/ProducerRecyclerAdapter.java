package com.lucagiorgetti.surprix.ui.mainfragments.catalog.producers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.model.Producer;

import java.util.List;

/**
 * Adapter for showing a list of Producers.
 * <p>
 * Created by Luca on 24/10/2017.
 */

public class ProducerRecyclerAdapter extends RecyclerView.Adapter<ProducerRecyclerAdapter.SetViewHolder> {
    private List<Producer> producers;

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_producer, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Producer producer = producers.get(position);
        holder.vName.setText(producer.getName());
        holder.vProduct.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (producers == null) {
            return 0;
        }
        return producers.size();
    }

    public Producer getItemAtPosition(int position) {
        return this.producers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    void setYears(List<Producer> producers) {
        this.producers = producers;
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView vName;
        TextView vProduct;

        SetViewHolder(View v) {
            super(v);
            vName = v.findViewById(R.id.txv_title_prd_select);
            vProduct = v.findViewById(R.id.txv_subtitle_prd_select);
        }
    }
}
