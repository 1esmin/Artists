package com.morlins.artists;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


public class ArtistDatabase extends AsyncTask<Void, Void, String> {
    private ListView list;
    private Context context;
    private String resultJson;
    private LinkedList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;
    private Parcelable state;

    SharedPreferences preferences;
    private String JSON_URL
            = "http://cache-default02f.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    //layout для item'а ListView
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item;


    public ArtistDatabase(Context context, ListView list, ImageLoader imageLoader,
                          DisplayImageOptions displayImageOptions) {
        this.list = list;
        resultJson = "";
        artists = new LinkedList<>();
        this.context = context;
        this.imageLoader = imageLoader;
        this.displayImageOptions = displayImageOptions;
        this.state = null;
    }
    public ArtistDatabase(Context context, ListView list, ImageLoader imageLoader,
                          DisplayImageOptions displayImageOptions, Parcelable state) {
        this(context, list, imageLoader, displayImageOptions);
        this.state = state;
    }

    public LinkedList<Artist> getArtists() {
        return artists;
    }

    //переопределения для реализации методов AsyncTask
    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
        try {
            URL url = new URL(JSON_URL);
            //TODO: Do caching
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            resultJson = builder.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        try {
            JSONArray json = new JSONArray(strJson);

            for (int i = 0; i < json.length(); i++) artists
                    .addLast(new Artist(json.getJSONObject(i)));

            //TODO: make sort
            Collections.sort(artists, new Comparator<Artist>() {
                @Override
                public int compare(Artist lhs, Artist rhs) {
                    return Collator.getInstance()
                            .compare(lhs.getName(), rhs.getName());
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();

        }

        //через адаптер записываем все в ListView (отрисовка по мере надобности)
        ArtistAdapter adapter = new ArtistAdapter(context,
                STYLE_ITEM_LIST, artists, imageLoader, displayImageOptions);
        list.setAdapter(adapter);
    }
}
