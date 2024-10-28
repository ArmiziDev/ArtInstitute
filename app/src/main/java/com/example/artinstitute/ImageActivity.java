package com.example.artinstitute;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.Objects;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    private TextView scaleText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

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

        PhotoView photo = findViewById(R.id.artistImageView2);
        scaleText = findViewById(R.id.scaleText);
        updateScaleText(photo.getScale());

        photo.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> updateScaleText(photo.getScale()));
    }

    private void updateScaleText(float scale) {
        int scalePercentage = Math.round(scale * 100);
        scaleText.setText(String.format("Scale: %d%%", scalePercentage));
    }

    private void backInvoked() {
        finish();
    }
    private void setImagePage(Artwork artwork)
    {
        TextView title = findViewById(R.id.titleTextArtwork);
        TextView artist = findViewById(R.id.artistNameText);
        TextView artistInfo = findViewById(R.id.artistInfoText);
        PhotoView artistImage = findViewById(R.id.artistImageView2);
        ProgressBar progressBar = findViewById(R.id.imageProgressBar);

        title.setText(artwork.title);
        artist.setText(artwork.artist);
        artistInfo.setText(artwork.artistDetails);

        String imageUrl = "https://www.artic.edu/iiif/2/" + artwork.imageId + "/full/843,/0/default.jpg";
        ArtworkDownloader.getImage(imageUrl, artistImage, () -> progressBar.setVisibility(View.INVISIBLE));

        artistImage.setMaximumScale(12.25f);
        artistImage.setMediumScale(3.5f);
        artistImage.setMinimumScale(1f);
    }

    public void LeaveImageActivity(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
