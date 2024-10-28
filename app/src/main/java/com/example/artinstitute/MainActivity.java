package com.example.artinstitute;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    EditText search;
    private RecyclerView recyclerView;
    private ArtworkAdapter artworkAdapter;
    private LinearLayoutManager linearLayoutManager;
    private final List<Artwork> artworkList = new ArrayList<>();
    private ArtworkDownloader artworkDownloader;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    ProgressBar searchProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        search = findViewById(R.id.searchText);

        // Recycler Setup
        recyclerView = findViewById(R.id.recyclerView);
        artworkAdapter = new ArtworkAdapter(artworkList, this);
        recyclerView.setAdapter(artworkAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Activity Data Transfer
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

        // Networking
        artworkDownloader = new ArtworkDownloader();

        searchProgressBar = findViewById(R.id.searchProgressBar);
    }

    private void handleResult(ActivityResult result)
    {}
    public void onSearch(View v)
    {
        String search_text = String.valueOf(search.getText());
        searchProgressBar.setVisibility(View.VISIBLE);
        artworkDownloader.Search(search_text, this);
    }
    public void onRandom(View v)
    {
        Toast.makeText(this, "Random!", Toast.LENGTH_SHORT).show();
        search.setText("");
    }

    public void updateRecyclerView(ArrayList<Artwork> artworks)
    {
        artworkList.clear();
        artworkList.addAll(artworks);
        artworkAdapter.notifyDataSetChanged();

        if (artworkList.isEmpty())
        {
            recyclerView.setBackgroundResource(R.drawable.bwlions);
        } else {
            recyclerView.setBackgroundResource(0);
        }

        searchProgressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v)
    {
        int pos = recyclerView.getChildLayoutPosition(v);
        Artwork artwork = artworkList.get(pos);

        enterArtwork(artwork);
    }

    public void enterArtwork(Artwork art)
    {
        Intent intent= new Intent(MainActivity.this, ArtworkActivity.class);
        intent.putExtra("artwork", art);

        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}