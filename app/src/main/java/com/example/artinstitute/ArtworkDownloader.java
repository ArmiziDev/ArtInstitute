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

public class ArtworkDownloader {
    private static final String TAG = "ArtworkDownloader";
    private static RequestQueue queue;
    private static final String artworkSearchURL = "https://api.artic.edu/api/v1/artworks/search";
    private static final String artworkResultURL = "https://api.artic.edu/api/v1/artworks";
    private static final String page_limit = "15";
    private static final String page = "1";

    public void Search(String search_request, MainActivity activity)
    {
        queue = Volley.newRequestQueue(activity);

        // Build the URL
        Uri.Builder buildURL = Uri.parse(artworkSearchURL).buildUpon();
        buildURL.appendQueryParameter("q", search_request);
        buildURL.appendQueryParameter("limit", page_limit);
        buildURL.appendQueryParameter("page", page);
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
                        activity.updateRecyclerView(artworks); // This method should update your RecyclerView
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ArtworkDownloader, Error fetching artwork data " + error);
                    }
                }
        );

        queue.add(request);
    }

    private ArrayList<Artwork> parseJSON(JSONObject response) {
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


    public static void getImage(String thumbnailUrl, ImageView imageView) {
        Response.Listener<Bitmap> listener = imageView::setImageBitmap;

        Response.ErrorListener errorListener = error ->
                Log.d(TAG, "getImage: Image download error: " + error.getMessage());

        ImageRequest imageRequest = new ImageRequest(
                thumbnailUrl,
                listener,
                0, 0,  // Width and height (0 means use actual size)
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                errorListener
        );

        queue.add(imageRequest);
    }

}
