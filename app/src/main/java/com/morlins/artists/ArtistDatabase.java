package com.morlins.artists;

import android.content.Context;
import android.os.AsyncTask;
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
import java.util.LinkedList;


public class ArtistDatabase extends AsyncTask<Void, Void, String> {
    ListView list;
    private Context context;
    private String resultJson;
    private LinkedList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;

    //константы
    private static final String JSON_URL
            = "http://cache-default02f.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    public ArtistDatabase(Context context, ListView list, 
                          ImageLoader imageLoader, DisplayImageOptions displayImageOptions) {
        this.list = list;
        resultJson = "";
        artists = new LinkedList<>();
        this.context = context;
        this.imageLoader = imageLoader;
        this.displayImageOptions = displayImageOptions;
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
            for (int i = 0; i < json.length(); i++) {
                JSONObject obj = json.getJSONObject(i);
                artists.addLast(new Artist(obj));
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        //через адаптер записываем все в ListView
        ArtistAdapter adapter = new ArtistAdapter(context,
                R.layout.simple_list_item, artists, imageLoader, displayImageOptions);
        list.setAdapter(adapter);
    }
}
