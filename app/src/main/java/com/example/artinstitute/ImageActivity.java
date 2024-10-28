package com.example.artinstitute;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artwork_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "onCreate: Registering OnBackInvokedCallback");
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    this::backInvoked
            );
        } else {
            getOnBackPressedDispatcher().addCallback(
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            backInvoked();
                        }
                    }
            );
        }

        Intent intent = getIntent();
        if (intent.hasExtra("artwork"))
        {
            setImagePage((Artwork) Objects.requireNonNull(intent.getSerializableExtra("artwork")));
        }
    }

    private void backInvoked() {
        finish();
    }

    private void setImagePage(Artwork artwork)
    {
        TextView title = findViewById(R.id.titleTextArtwork);
        TextView artist = findViewById(R.id.artistNameText);
        TextView artistInfo = findViewById(R.id.artistInfoText);
        ImageView artistImage = findViewById(R.id.artistImageView2);

        title.setText(artwork.title);
        artist.setText(artwork.artist);
        artistInfo.setText(artwork.artistDetails);

        String imageUrl = "https://www.artic.edu/iiif/2/" + artwork.imageId + "/full/843,/0/default.jpg";
        ArtworkDownloader.getImage(imageUrl, artistImage);
    }
}
