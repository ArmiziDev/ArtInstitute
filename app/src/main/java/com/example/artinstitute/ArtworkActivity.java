package com.example.artinstitute;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ArtworkActivity extends AppCompatActivity {
    private static final String TAG = "ArtworkActivity";
    Artwork current_artwork;
    private ActivityResultLauncher<Intent> activityResultLauncher;

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
            setArtworkPage((Artwork) Objects.requireNonNull(intent.getSerializableExtra("artwork")));
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    private void handleResult(ActivityResult result) {}

    public void setArtworkPage(Artwork artwork)
    {
        current_artwork = artwork;

        TextView title = findViewById(R.id.titleText);
        TextView dateDisplay = findViewById(R.id.dateDisplayText);
        TextView artist = findViewById(R.id.artistDetailsText);
        TextView department = findViewById(R.id.departmentTitleText);
        TextView artistInfo = findViewById(R.id.artistDisplayText);
        TextView galleryTitle = findViewById(R.id.galleryTitleText);
        TextView placeOrigion = findViewById(R.id.placeOrigionText);
        TextView artworkTypeTitle = findViewById(R.id.artworkTypeTitleText);
        TextView dimensions = findViewById(R.id.dimensionsText);
        TextView creditLine = findViewById(R.id.creditLineText);
        ImageView artistImage = findViewById(R.id.artistImageView);

        title.setText(artwork.title);
        if (artwork.title.equals("null") || artwork.title.isEmpty()) { title.setVisibility(GONE); }

        department.setText(artwork.departmentTitle);
        if (artwork.departmentTitle.equals("null") || artwork.departmentTitle.isEmpty()) { department.setVisibility(GONE); }

        dateDisplay.setText(artwork.dateDisplay);
        if (artwork.dateDisplay.equals("null") || artwork.dateDisplay.isEmpty()) { dateDisplay.setVisibility(GONE); }

        artist.setText(artwork.artist);
        if (artwork.artist.equals("null") || artwork.artistDetails.isEmpty()) { artist.setVisibility(GONE); }

        artistInfo.setText(artwork.artistDetails);
        if (artwork.artistDetails.equals("null") || artwork.artistDetails.isEmpty()) { artistInfo.setVisibility(GONE); }

        galleryTitle.setText(artwork.galleryTitle);
        if (artwork.galleryTitle.equals("null") || artwork.galleryTitle.isEmpty()) { galleryTitle.setVisibility(GONE); }

        placeOrigion.setText(artwork.placeOfOrigin);
        if (artwork.placeOfOrigin.equals("null") || artwork.placeOfOrigin.isEmpty()) { placeOrigion.setVisibility(GONE); }

        String artworkType = artwork.artworkTypeTitle + " - " + artwork.mediumDisplay;
        artworkTypeTitle.setText(artworkType);
        if (artwork.artworkTypeTitle.equals("null") || artwork.artworkTypeTitle.isEmpty()) { artworkTypeTitle.setVisibility(GONE); }

        String dimension = artwork.dimensions;
        if (artwork.dimensions.equals("null") || artwork.dimensions.isEmpty()) { dimensions.setVisibility(GONE); }
        else {
            int startIndex = dimension.indexOf(":") + 1;
            int endIndex = dimension.indexOf(";");
            if (startIndex > 0 && endIndex > startIndex) {
                dimension = dimension.substring(startIndex, endIndex);
            }
        }
        dimensions.setText(dimension);

        creditLine.setText(artwork.creditLine);
        if (artwork.creditLine.equals("null") || artwork.creditLine.isEmpty()) { creditLine.setVisibility(GONE); }

        String imageUrl = "https://www.artic.edu/iiif/2/" + artwork.imageId + "/full/843,/0/default.jpg";
        ArtworkDownloader.getImage(imageUrl, artistImage);
    }

    private void backInvoked() {
        finish();
    }

    public void imagePressed(View v)
    {
        Intent intent= new Intent(this, ImageActivity.class);
        intent.putExtra("artwork", current_artwork);

        activityResultLauncher.launch(intent);
    }

    public void onGalleryPressed(View v)
    {
        String url = "https://www.artic.edu/galleries/" + current_artwork.galleryId;
        Uri webpage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }
}
