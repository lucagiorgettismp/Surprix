package com.lucagiorgetti.collectionhelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucagiorgetti.collectionhelper.model.Producer;
import com.lucagiorgetti.collectionhelper.model.Set;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 24/10/2017.
 */

public class SetRecyclerAdapter extends RecyclerView.Adapter<SetRecyclerAdapter.SetViewHolder> implements Filterable{
    private ArrayList<Set> sets = new ArrayList<>();
    ArrayList<Set> mStringFilterList;

    public SetRecyclerAdapter(ArrayList<Set> setsList) {
        sets = setsList;
        mStringFilterList = setsList;
    }

    @Override
    public int getItemCount() {
        return mStringFilterList.size();
    }

    @Override
    public void onBindViewHolder(SetViewHolder holder, int position) {
        Set set = sets.get(position);
        holder.vName.setText(set.getName());
        holder.vProducer.setText(set.getProducer().getName());
        holder.vYear.setText(String.valueOf(set.getYear()));
        holder.vSeason.setText(set.getSeason());

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(set.getImg_path());
        ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Successfully downloaded data to local file
                // ...
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });

        if(localFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            holder.vImage.setImageBitmap(myBitmap);
        }
    }

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sets_element, parent,  false);
        return new SetViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mStringFilterList = sets;
                } else {

                    ArrayList<Set> filteredList = new ArrayList<>();

                    for (Set set : sets) {

                        if (set.getName().toLowerCase().contains(charString) || set.getName().toLowerCase().contains(charString)) {

                            filteredList.add(set);
                        }
                    }

                    mStringFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mStringFilterList;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mStringFilterList = (ArrayList) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class SetViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vSeason;
        protected TextView vYear;
        protected TextView vProducer;
        protected ImageView vImage;

        public SetViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txv_setname);
            vSeason = (TextView)  v.findViewById(R.id.txv_season);
            vYear = (TextView)  v.findViewById(R.id.txv_setyear);
            vProducer = (TextView) v.findViewById(R.id.txv_producer);
            vImage = (ImageView) v.findViewById(R.id.imgSet);
        }
    }
}
