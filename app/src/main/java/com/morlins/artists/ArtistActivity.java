package com.morlins.artists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ArtistActivity extends AppCompatActivity {
    private ImageView image_view;
    private TextView genres_text;
    private TextView albums_and_tracks_text;
    private TextView description_text;

    private String name, description, genres;
    private Integer albums, tracks;
    private String[] cover;

    //константы
    private static final int COVER_POSITION_SMALL_IMAGE = 0;
    private static final int COVER_POSITION_BIG_IMAGE   = 1;
    private static final String DELIM_GENRES = ", ";
    private static final String DELIM_ALBUMS_AND_TRACKS = " - ";
    private static final String ARTIST = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        //инициализация загрузчика картинок
        ImageLoader imageLoader = ImageLoader.getInstance();
        //настройки (включено кэширование в ОЗУ, ПЗУ)
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(Boolean.TRUE)
                .cacheOnDisk(Boolean.TRUE)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        genres_text =           (TextView)  findViewById(R.id.artist_genres_text);
        albums_and_tracks_text =(TextView)  findViewById(R.id.artist_albums_and_tracks_text);
        description_text =      (TextView)  findViewById(R.id.artist_description_text);
        image_view       =      (ImageView) findViewById(R.id.artist_big_image);

        Intent intent = getIntent();

        Artist artist = intent.getParcelableExtra(ARTIST);

        genres  = artist.getStringGenres(DELIM_GENRES);
        name    =       artist.getName();
        albums  =       artist.getAlbums();
        tracks  =       artist.getTracks();
        description =   artist.getDescription();
        cover   =       artist.getCover();

        setTitle(name);
        imageLoader.displayImage(cover[COVER_POSITION_BIG_IMAGE], image_view, options);
        genres_text.setText(genres);
        albums_and_tracks_text.setText(artist.getStringAlbumsAndTracks(DELIM_ALBUMS_AND_TRACKS));
        description_text.setText(name + DELIM_ALBUMS_AND_TRACKS + description);
    }
}
