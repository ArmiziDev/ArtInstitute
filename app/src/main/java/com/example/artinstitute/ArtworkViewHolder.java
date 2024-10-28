package com.example.artinstitute;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtworkViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView title;
    public ProgressBar progressBar;

    public ArtworkViewHolder(@NonNull View itemView) {
        super(itemView);

        this.image = itemView.findViewById(R.id.artImage);
        this.title = itemView.findViewById(R.id.artText);

        this.progressBar = itemView.findViewById(R.id.progressBar);
    }
}
