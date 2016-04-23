/*
 *
 *  * Copyright (C) 2016 Kuliev Eduard, http://github.com/1esmin/artists
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.morlins.artists;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class ArtistProvider extends AsyncTask<Void, Void, Void> {
    private ListView list;      //список, на который устанавливается адаптер
    private Context context;    //контекст, в котором вызывается
    private LinkedList<Artist> artists; //список исполнителей
    private ImageLoader imageLoader;    /* объект для работы с изображениями
                                            (кэширование, отображение и тд) */
    private DisplayImageOptions displayImageOptions; //настройки для imageLoader
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //компаратор по которому производится сортировка списка
    private Comparator<Artist> comparator = new Comparator<Artist>() {
        @Override
        public int compare(Artist lhs, Artist rhs) {
            return Collator.getInstance()
                    .compare(lhs.getName(), rhs.getName());
        }
    };

    //url, на который делается запрос json
    private String JSON_URL;
    //layout для item'а ListView
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item;

    //конструктор
    public ArtistProvider(Context context, ListView list, ImageLoader imageLoader,
                          DisplayImageOptions displayImageOptions,
                          SwipeRefreshLayout mSwipeRefreshLayout) {
        this.list        = list;
        this.context     = context;
        this.imageLoader = imageLoader;
        this.displayImageOptions = displayImageOptions;
        this.artists     = new LinkedList<>();
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public LinkedList<Artist> getArtists() {
        return artists;
    }


    //переопределения для реализации методов AsyncTask
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
        JSON_URL = context.getString(R.string.json_url);

        //получение и парсинг строки. если ответа нет, то
        //берется кэшированная версия

        try {
            //получаем и парсим json в jsonarray
            JSONArray json = new JSONArray(getString(new URL(JSON_URL)));

            //заполняем список artists
            for (int i = 0; i < json.length(); i++)
                artists.addLast(new Artist(json.getJSONObject(i)));

            //сортировка по алфавиту
            Collections.sort(artists, comparator);
        } catch (JSONException | IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);

        //через адаптер записываем все в ListView (отрисовка по мере надобности)
        ArtistAdapter adapter = new ArtistAdapter(context,
                STYLE_ITEM_LIST, artists, imageLoader, displayImageOptions);
        list.setAdapter(adapter);
    }

    private String getString(URL url) throws IOException {
        //открываем соединение
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        //cобираем строку по строчно
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }
}
