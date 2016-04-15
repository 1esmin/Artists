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

import java.util.LinkedList;

public class ArtistAdapter extends BaseAdapter {
    //private int item_style;
    //private Context context;
    private ImageLoader imageLoader;
    private LayoutInflater lInflater;
    private LinkedList<Artist> artists;
    private DisplayImageOptions displayImageOptions;

    //константы
    private static final String DELIM_ALBUMS_AND_TRACKS = ", ";
    private static final int COVER_POSITION_SMALL_IMAGE = 0;
    private static final int COVER_POSITION_BIG_IMAGE   = 1;

    //основной конструктор
    public ArtistAdapter(Context context, int item_style, LinkedList<Artist> artists,
                         ImageLoader imageLoader, DisplayImageOptions displayImageOptions) {
        //this.context = context;
        //this.item_style = item_style;
        this.artists = artists;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
        this.displayImageOptions = displayImageOptions;
    }

    //возвращает исполнителя по позиции в artists
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
            view = lInflater.inflate(R.layout.simple_list_item, parent, false);
        }

        Artist artist = getArtist(position);

        ImageView image_view = (ImageView) view.findViewById(R.id.item_small_image);

        ((TextView) view.findViewById(R.id.item_name))
                .setText(artist.getName());
        ((TextView) view.findViewById(R.id.item_genres))
                .setText(artist.getStringGenres(DELIM_ALBUMS_AND_TRACKS));
        ((TextView) view.findViewById(R.id.item_albums_and_tracks))
                .setText(artist.getStringAlbumsAndTracks(DELIM_ALBUMS_AND_TRACKS));

        imageLoader.displayImage(artist.getCover()[COVER_POSITION_SMALL_IMAGE],
                image_view, displayImageOptions);

        return view;
    }
}
