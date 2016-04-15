package com.morlins.artists;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    //константы
    private static final String ARTIST = "artist";
    private static final int REFRESHTIME = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ArtistDatabase ArtistDB = new ArtistDatabase(this, list, imageLoader, options);
        ArtistDB.execute();

        artists = ArtistDB.getArtists();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
        }, REFRESHTIME);

        ArtistAdapter adapter = new ArtistAdapter(this,
                R.layout.simple_list_item, artists, imageLoader, options);
        list.setAdapter(adapter);
    }

}
