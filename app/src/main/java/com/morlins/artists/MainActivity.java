/*
 * Copyright (C) 2016 Kuliev Eduard, http://github.com/1esmin/artists
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.morlins.artists;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private ListView list;  // ListView, в который выводятся все исполнители через адаптер
    private ArtistProvider artistProvider; //работает с сетью и кэшем
    private SwipeRefreshLayout mSwipeRefreshLayout; //необходимо для реализации SwipeRefresh
    private LinkedList<Artist> artists; //список артистов
    private ImageLoader imageLoader;    /* объект для работы с изображениями
                                            (кэширование, отображение и тд) */
    private DisplayImageOptions options; //настройки imageLoader

    private static final String ARTIST = "artist";    //строка-ключ для интента
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item; //layout для item'а ListView

    //цвета для SwipeRefresh
    private static final int HOLO_BLUE_BRIGHT   = android.R.color.holo_blue_bright;
    private static final int HOLO_GREEN_LIGHT   = android.R.color.holo_green_light;
    private static final int HOLO_ORANGE_LIGHT  = android.R.color.holo_orange_light;
    private static final int HOLO_RED_LIGHT     = android.R.color.holo_red_light;
    private static final int SWIPE_CONTAINER    = R.id.swipe_container;

    //пока картинка не загрузилась, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_ONLOADING  = R.color.gray;
    //если загрузить не получилось, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_ON_FAIL    = COLOR_IMAGE_ONLOADING;
    //если uri оказался пуст, ImageView закрашивается этим цветом
    private static final int COLOR_IMAGE_IF_EMPTY_URI  = COLOR_IMAGE_ONLOADING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(SWIPE_CONTAINER);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //цветовая схема кружочка обновления
        mSwipeRefreshLayout.setColorSchemeResources(
                HOLO_BLUE_BRIGHT,
                HOLO_GREEN_LIGHT,
                HOLO_ORANGE_LIGHT,
                HOLO_RED_LIGHT
        );

        //конфигурация imageLoader
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(COLOR_IMAGE_IF_EMPTY_URI)
                .showImageOnFail(COLOR_IMAGE_ON_FAIL)
                .showImageOnLoading(COLOR_IMAGE_ONLOADING)
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        //кэширование ответов сервера
        installCache(300 * 1024);

        artistProvider = new ArtistProvider(this, list,
                imageLoader, options, mSwipeRefreshLayout);
        artistProvider.execute();

        artists = artistProvider.getArtists();

        //события на нажатия пунктов списка
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, ArtistActivity.class);
                intent.putExtra(ARTIST, artists.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    //создание меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            //кнопка перехода на новое активити
            case R.id.action_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            //очистка кэша
            case R.id.clear_cache:
                imageLoader.clearDiskCache();
                imageLoader.clearMemoryCache();
                (new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            clearCache(HttpResponseCache.getInstalled());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("cache cleared", "false");
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Log.d("cache cleared", "true");
                    }
                }).execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //код исполняемый при обновлении
    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    clearCache(HttpResponseCache.getInstalled());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                artistProvider = new ArtistProvider(getBaseContext(), list,
                        imageLoader, options, mSwipeRefreshLayout);
                artistProvider.execute();
            }
        });
    }

    //очищаем кэш
    private void clearCache(HttpResponseCache cache) throws IOException {
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();

        if (cache != null) cache.delete();
        installCache(300 * 1024);
    }

    ///инициализируем кэш
    private void installCache(long cacheSize){
        final File httpCacheDir = new File(this.getCacheDir(), "http");
        try {
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, cacheSize);
            Log.v("cache", "cache set up");
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.v("cache", "android.net.http.HttpResponseCache not available, " +
                    "probably because we're running on a pre-ICS version of Android. " +
                    "Using com.integralblue.httpresponsecache.HttpHttpResponseCache.");
        }
        try {
            HttpResponseCache.install(httpCacheDir, cacheSize);
        } catch(Exception e) {
            Log.v("cache", "Failed to set up ");
            e.printStackTrace();
        }
    }
}