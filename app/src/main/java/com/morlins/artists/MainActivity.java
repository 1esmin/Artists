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
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private ListView list;  // ListView, в который выводятся все исполнители через адаптер
    private ArtistProvider ArtistDB; //работает с сетью и кэшем
    private SwipeRefreshLayout mSwipeRefreshLayout; //необходимо для реализации SwipeRefresh
    private LinkedList<Artist> artists; //список артистов
    private ImageLoader imageLoader;    /* объект для работы с изображениями
                                            (кэширование, отображение и тд) */
    private DisplayImageOptions options; //настройки imageLoader

    private static final String ARTIST = "artist";    //строка-ключ для интента
    private static final int REFRESH_TIME = 1000;     //время жизни прогресс бара SwipeRefresh
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

        //конфигурация
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

        ArtistProvider ArtistDB = new ArtistProvider(this, list,
                imageLoader, options);
        ArtistDB.execute();

        artists = ArtistDB.getArtists();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(SWIPE_CONTAINER);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(
                HOLO_BLUE_BRIGHT,
                HOLO_GREEN_LIGHT,
                HOLO_ORANGE_LIGHT,
                HOLO_RED_LIGHT
        );

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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //// TODO: 19.04.2016 make max-age
                HttpResponseCache cache = HttpResponseCache.getInstalled();
                if (cache != null)
                    try {
                        cache.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                mSwipeRefreshLayout.setRefreshing(false);
                ArtistDB = new ArtistProvider(getBaseContext(), list,
                        imageLoader, options);
                ArtistDB.execute();
            }
        }, REFRESH_TIME);
    }
}