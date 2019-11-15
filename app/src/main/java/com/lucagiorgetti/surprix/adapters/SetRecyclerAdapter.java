package com.lucagiorgetti.surprix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.SurprixApplication;
import com.lucagiorgetti.surprix.model.Colors;
import com.lucagiorgetti.surprix.model.ExtraLocales;
import com.lucagiorgetti.surprix.model.Set;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for showing a list of Sets.
 * <p>
 * Created by Luca on 24/10/2017.
 */

public class SetRecyclerAdapter extends RecyclerView.Adapter<SetRecyclerAdapter.SetViewHolder> {
    private List<Set> sets;

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_set, parent, false);
        return new SetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Set set = sets.get(position);
        holder.vName.setText(set.getName());
        Context ctx = SurprixApplication.getSurprixContext();

        String nation;
        if (ExtraLocales.isExtraLocale(set.getNation())) {
            nation = ExtraLocales.getDisplayName(set.getNation());
        } else {
            Locale l = new Locale("", set.getNation());
            nation = l.getDisplayCountry();
        }
        holder.vNation.setText(nation);
        holder.vLayout.setBackgroundColor(ContextCompat.getColor(ctx, Colors.getHexColor(set.getProducer_color())));

        String path = set.getImg_path();
        if (path.startsWith("gs")) {
            FirebaseStorage storage = SurprixApplication.getInstance().getFirebaseStorage();
            StorageReference gsReference = storage.getReferenceFromUrl(path);
            Glide.with(ctx).
                    load(gsReference).
                    apply(new RequestOptions()
                            .placeholder(R.drawable.ic_bpz_placeholder))
                    .into(holder.vImage);

        } else {
            Glide.with(ctx).
                    load(path).
                    apply(new RequestOptions()
                            .placeholder(R.drawable.ic_bpz_placeholder))
                    .into(holder.vImage);
        }
    }

    @Override
    public int getItemCount() {
        if (sets != null){
            return sets.size();
        }
        return 0;
    }

    public Set getItemAtPosition(int position) {
        return this.sets.get(position);
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView vName;
        TextView vNation;
        ImageView vImage;
        View vLayout;

        SetViewHolder(View v) {
            super(v);
            vName = v.findViewById(R.id.txv_set_elem_name);
            vImage = v.findViewById(R.id.imgSet);
            vNation = v.findViewById(R.id.txv_set_elem_nation);
            vLayout = v.findViewById(R.id.set_divider);
        }
    }
}
