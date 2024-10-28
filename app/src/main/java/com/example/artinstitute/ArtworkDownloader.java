package com.example.artinstitute;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ArtworkDownloader {
    private static final String TAG = "ArtworkDownloader";
    private static RequestQueue queue;

    public void Search(String search_request, MainActivity activity)
    {
        queue = Volley.newRequestQueue(activity);

        // Build the URL
        Uri.Builder buildURL = Uri.parse(activity.getString(R.string.artworkSearchURL)).buildUpon();
        buildURL.appendQueryParameter("q", search_request);
        buildURL.appendQueryParameter("limit", activity.getString(R.string.page_limit));
        buildURL.appendQueryParameter("page", "1");
        buildURL.appendQueryParameter("fields", "title,date_display,artist_display,medium_display," +
                "artwork_type_title,image_id,dimensions,department_title,credit_line,place_of_origin," +
                "gallery_title,gallery_id,id,api_link");

        String url = buildURL.build().toString();

        // Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Artwork> artworks = parseJSON(response);
                        activity.updateRecyclerView(artworks); // update recycler view
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ArtworkDownloader, Error fetching artwork data " + error);
                        activity.connectionError();
                    }
                }
        );

        queue.add(request);
    }

    private static ArrayList<Artwork> parseJSON(JSONObject response) {
        ArrayList<Artwork> artworks = new ArrayList<>();
        try {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject artworkJson = dataArray.getJSONObject(i);

                String title = artworkJson.optString("title", "No Title");
                String dateDisplay = artworkJson.optString("date_display", "Unknown Date");
                String artistDisplay = artworkJson.optString("artist_display", "Unknown Artist");
                String mediumDisplay = artworkJson.optString("medium_display", "Unknown Medium");
                String artworkTypeTitle = artworkJson.optString("artwork_type_title", "Unknown Type");
                String imageId = artworkJson.optString("image_id", "");
                String dimensions = artworkJson.optString("dimensions", "");
                String departmentTitle = artworkJson.optString("department_title", "");
                String creditLine = artworkJson.optString("credit_line", "");
                String placeOfOrigin = artworkJson.optString("place_of_origin", "");
                String galleryTitle = artworkJson.optString("gallery_title", "Not on Display");
                String galleryId = artworkJson.optString("gallery_id", null);
                String apiLink = artworkJson.optString("api_link", "");
                String id = artworkJson.optString ("id", "0");

                Artwork artwork = new Artwork(id, title, dateDisplay, artistDisplay, mediumDisplay, artworkTypeTitle,
                         imageId, dimensions, departmentTitle, creditLine, placeOfOrigin, galleryTitle, galleryId, apiLink);
                artworks.add(artwork);
            }
        } catch (JSONException e) {
            Log.e("ArtworkDownloader", "Error parsing JSON data", e);
        }
        return artworks;
    }

    private static ArrayList<String> parseGalleries(JSONObject response) {
        ArrayList<String> galleryNumbers = new ArrayList<>();
        try {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject galleryJson = dataArray.getJSONObject(i);
                String galleryNumber = galleryJson.optString("number", "Unknown");
                galleryNumbers.add(galleryNumber);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON data", e);
        }
        return galleryNumbers;
    }

    public static void searchGallery(String gallery, MainActivity activity)
    {
        Uri.Builder buildURL = Uri.parse(activity.getString(R.string.artworkSearchURL)).buildUpon();
        buildURL.appendQueryParameter("gallery_id", gallery);
        buildURL.appendQueryParameter("limit", activity.getString(R.string.page_limit));
        buildURL.appendQueryParameter("page", "1");
        buildURL.appendQueryParameter("fields", "title,date_display,artist_display,medium_display," +
                "artwork_type_title,image_id,dimensions,department_title,credit_line,place_of_origin," +
                "gallery_title,gallery_id,id,api_link");

        String url = buildURL.build().toString();

        // Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Artwork> artworks = parseJSON(response);
                        Random random = new Random();
                        Artwork artwork = artworks.get(random.nextInt(artworks.size()));
                        activity.enterArtwork(artwork);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ArtworkDownloader, Error fetching artwork data " + error);
                        activity.connectionError();
                    }
                }
        );

        queue.add(request);
    }

    public static void Random(MainActivity context)
    {
        Artwork artwork;
        queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, context.getString(R.string.gallerySearchURL), null,
                response -> {
                    ArrayList<String> galleries = parseGalleries(response);
                    if (!galleries.isEmpty()) {
                        Random random = new Random();
                        String gallery = galleries.get(random.nextInt(galleries.size()));
                        searchGallery(gallery, context);
                    } else {
                        Log.d(TAG, "No galleries found.");
                    }
                },
                error -> Log.d(TAG, "Error fetching galleries: " + error.getMessage())
        );

        queue.add(request);
    }

    public static void getImage(String url, ImageView imageView, Runnable onImageLoaded) {
        long start = System.currentTimeMillis();  // Start the timer

        Picasso.get()
                .load(url)
                .config(Bitmap.Config.RGB_565)
                .centerInside()
                .fit()
                .error(R.drawable.not_available)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        long time = System.currentTimeMillis() - start;  // Calculate the time taken
                        Log.d(TAG, "onSuccess: Image loaded in " + time + " ms");

                        onImageLoaded.run();  // Execute the callback
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: Image download error: " + e.getMessage());
                        onImageLoaded.run();
                    }
                });
    }
}
