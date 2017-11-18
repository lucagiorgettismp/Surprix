package com.lucagiorgetti.collectionhelper.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lucagiorgetti.collectionhelper.R;
import com.lucagiorgetti.collectionhelper.model.Colors;
import com.lucagiorgetti.collectionhelper.model.Producer;
import com.lucagiorgetti.collectionhelper.model.Year;
import java.util.ArrayList;
/**
 * Created by Luca on 24/10/2017.
 */

public class YearRecyclerAdapter extends RecyclerView.Adapter<YearRecyclerAdapter.SetViewHolder>{
    private ArrayList<Year> years = new ArrayList<>();
    Context ctx;

    public YearRecyclerAdapter(Context context, ArrayList<Year> yearsList) {
        years = yearsList;
        ctx = context;
    }

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.year_select_element, parent,  false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SetViewHolder holder, int position) {
        Year year = years.get(position);
        holder.vName.setText(String.valueOf(year.getYear()));

        String color = null;
        switch (position % 5){
            case 0:
                color = Colors.BLUE;
                break;
            case 1:
                color = Colors.GREEN;
                break;
            case 2:
                color = Colors.ORANGE;
                break;
            case 3:
                color = Colors.RED;
                break;
            case 4:
                color = Colors.PURPLE;
                break;
        }
        holder.vLayout.setBackgroundColor(ContextCompat.getColor(ctx, Colors.getHexColor(color)));
    }

    @Override
    public int getItemCount() {
        return years.size();
    }


    public Year getItemAtPosition(int position) {
        return this.years.get(position);
    }

    public static class SetViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected View vLayout;

        public SetViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txv_year_number);
            vLayout = (View) v.findViewById(R.id.layout_year_select);

        }
    }
}