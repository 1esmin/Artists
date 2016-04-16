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
    private ListView list;
    private Context context;
    private String resultJson;
    private LinkedList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;

    //адрес с которого получаем json файл
    private static final String JSON_URL
            = "http://cache-default02f.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    //layout для item'а ListView
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item;


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
        LogUtil.v("ArtistDatabase.doInBackground() start");
        // получаем данные с внешнего ресурса
        try {
            LogUtil.v("Parse URL");
            URL url = new URL(JSON_URL);
            LogUtil.v("Download json");
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

            LogUtil.v("received json");
            resultJson = builder.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.v("ArtistDatabase.doInBackground() end");
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        LogUtil.v("ArtistDatabase.onPostExecute() start");
        super.onPostExecute(strJson);
        try {
            LogUtil.v("Parse JSON");
            JSONArray json = new JSONArray(strJson);
            LogUtil.v("Parse JSONArray");
            for (int i = 0; i < json.length(); i++) artists
                    .addLast(new Artist(json.getJSONObject(i)));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        LogUtil.v("setAdapter");
        //через адаптер записываем все в ListView (отрисовка по мере надобности)
        ArtistAdapter adapter = new ArtistAdapter(context,
                STYLE_ITEM_LIST, artists, imageLoader, displayImageOptions);
        list.setAdapter(adapter);
        LogUtil.v("ArtistDatabase.onPostExecute() end");
    }
}
