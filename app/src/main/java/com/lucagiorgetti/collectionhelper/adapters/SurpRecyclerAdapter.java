package com.lucagiorgetti.collectionhelper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lucagiorgetti.collectionhelper.R;
import com.lucagiorgetti.collectionhelper.model.Surprise;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Luca on 28/10/2017.
 */

public class SurpRecyclerAdapter extends RecyclerView.Adapter<SurpRecyclerAdapter.SurpViewHolder>{
    private ArrayList<Surprise> surprises = new ArrayList<>();
    ArrayList<Surprise> mStringFilterList;
    Context ctx;
    public SurpRecyclerAdapter(Context context, ArrayList<Surprise> surpList){
        surprises = surpList;
        mStringFilterList = surpList;
        ctx = context;
    }

    @Override
    public SurpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.missing_surprise_element, parent, false);
        return new SurpViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SurpViewHolder holder, int position) {
        Surprise surp = surprises.get(position);
        holder.vCode.setText(surp.getCode());
        holder.vSetName.setText(surp.getSet_name());
        holder.vDescription.setText(surp.getDescription());
        holder.vProduct.setText(surp.getSet_product_name());
        holder.vYear.setText(String.valueOf(surp.getSet_year()));
        holder.vProducer.setText(surp.getSet_producer_name());

        Locale l = new Locale("", surp.getSet_nation());
        holder.vNation.setText(l.getDisplayCountry());

        Glide.with(ctx).load(surp.getImg_path()).into(holder.vImage);
    }

    @Override
    public int getItemCount() {
        return surprises.size();
    }

    public class SurpViewHolder extends RecyclerView.ViewHolder {
        protected TextView vCode;
        protected TextView vSetName;
        protected TextView vDescription;
        protected TextView vProduct;
        protected TextView vYear;
        protected TextView vProducer;
        protected TextView vNation;
        protected ImageView vImage;


        public SurpViewHolder(View v) {
            super(v);
            vCode = (TextView) v.findViewById(R.id.txv_surp_elem_code);
            vSetName = (TextView) v.findViewById(R.id.txv_surp_elem_set);
            vDescription = (TextView) v.findViewById(R.id.txv_surp_elem_desc);
            vYear = (TextView) v.findViewById(R.id.txv_surp_elem_year);
            vProduct = (TextView) v.findViewById(R.id.txv_surp_elem_product);
            vProducer = (TextView) v.findViewById(R.id.txv_surp_elem_producer);
            vNation = (TextView) v.findViewById(R.id.txv_surp_elem_nation);
            vImage = (ImageView) v.findViewById(R.id.img_surp_elem);
        }
    }
}