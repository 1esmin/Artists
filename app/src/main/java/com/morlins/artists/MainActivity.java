package com.morlins.artists;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ListView list;
    private ArtistDatabase ArtistDB;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinkedList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    //строка-ключ для интента
    private static final String ARTIST = "artist";
    //время, которое крутится кружок во время обновления
    private static final int REFRESH_TIME = 1000;
    //layout для item'а ListView
    private static final int STYLE_ITEM_LIST = R.layout.simple_list_item;
    //цвета для кружочка обновления
    private static final int HOLO_BLUE_BRIGHT   = android.R.color.holo_blue_bright;
    private static final int HOLO_GREEN_LIGHT   = android.R.color.holo_green_light;
    private static final int HOLO_ORANGE_LIGHT  = android.R.color.holo_orange_light;
    private static final int HOLO_RED_LIGHT     = android.R.color.holo_red_light;
    private static final int SWIPE_CONTAINER    = R.id.swipe_container;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.v("Create Activity");
        list = (ListView) findViewById(R.id.list);

        LogUtil.v("Configure ImageLoader");
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        LogUtil.v("ArtistDB.execute()");
        ArtistDatabase ArtistDB = new ArtistDatabase(this, list, imageLoader, options);
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
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                ArtistDB = new ArtistDatabase(getBaseContext(), list, imageLoader, options);
                ArtistDB.execute();
            }
        }, REFRESH_TIME);

        LogUtil.v("setAdapter");
        ArtistAdapter adapter = new ArtistAdapter(this,
                STYLE_ITEM_LIST, artists, imageLoader, options);
        list.setAdapter(adapter);
    }
}