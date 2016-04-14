package com.morlins.artists;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Morlins on 11.04.2016.
 */
public class ArtistArray {
    JSONArray artists;

    public ArtistArray(String href) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // получаем данные с внешнего ресурса
        try {
            URL url = new URL(href);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            artists = new JSONArray(buffer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
