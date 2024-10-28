package com.example.artinstitute;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkViewHolder> {
    private static final String TAG = "ArtworkAdapter";
    private final List<Artwork> artworkList;
    private final MainActivity mainAct;

    ArtworkAdapter(List<Artwork> empList, MainActivity ma)
    {
        this.artworkList = empList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public ArtworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making New MyViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new ArtworkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtworkViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Filling New View Holder Artwork " + position);
        Artwork artwork = artworkList.get(position);

        holder.title.setText(artwork.title);

        String thumbnailUrl = "https://www.artic.edu/iiif/2/" + artwork.imageId + "/full/200,/0/default.jpg";
        ArtworkDownloader.getImage(thumbnailUrl, holder.image,  () -> holder.progressBar.setVisibility(View.INVISIBLE));
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }
}
