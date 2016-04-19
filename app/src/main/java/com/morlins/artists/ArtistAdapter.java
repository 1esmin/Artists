package com.morlins.artists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Iterator;
import java.util.LinkedList;

public class ArtistAdapter extends BaseAdapter {
    private int item_style;
    private Context context;
    private ImageLoader imageLoader;
    private LayoutInflater lInflater;
    private LinkedList<Artist> artists;
    private DisplayImageOptions displayImageOptions;

    //разделитель для строки жанров
    private static final String DELIM_GENRES = ", ";
    //разделитель для строки количества альбомов и треков
    private static final String DELIM_ALBUMS_AND_TRACKS = ", ";
    //индекс маленького изображения из массива cover
    private static final int COVER_POSITION_SMALL_IMAGE = 0;
    //индекс большого изображения из  массива cover
    private static final int COVER_POSITION_BIG_IMAGE   = 1;

    //основной конструктор
    public ArtistAdapter(Context context, int item_style, LinkedList<Artist> artists,
                         ImageLoader imageLoader, DisplayImageOptions displayImageOptions) {
        this.context = context;
        this.item_style = item_style;
        this.artists = artists;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
        this.displayImageOptions = displayImageOptions;

    }

    //возвращает исполнителя по позиции из artists
    public Artist getArtist(int position) {
        return ((Artist) getItem(position));
    }

    //реализации методов для интерфейса BaseAdapter
    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(item_style, parent, false);
        }

        Artist artist = getArtist(position);

        ImageView image_view = (ImageView) view.findViewById(R.id.item_small_image);

        imageLoader.displayImage(artist.getCover()[COVER_POSITION_SMALL_IMAGE],
                image_view, displayImageOptions);

        ((TextView) view.findViewById(R.id.item_name))
                .setText(artist.getName());
        ((TextView) view.findViewById(R.id.item_genres))
                .setText(artist.getStringGenres(DELIM_GENRES));
        ((TextView) view.findViewById(R.id.item_albums_and_tracks))
                .setText(artist.getStringAlbumsAndTracks(DELIM_ALBUMS_AND_TRACKS));

        return view;
    }
}
