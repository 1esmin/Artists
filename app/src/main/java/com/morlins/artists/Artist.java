package com.morlins.artists;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Artist implements Parcelable {
    private Integer id, tracks, albums;
    private String name, link, description;
    private String[] genres;
    private String[] cover;

    //ключи для получения данных из json
    private static final String ID      = "id";
    private static final String TRACKS  = "tracks";
    private static final String ALBUMS  = "albums";
    private static final String NAME    = "name";
    private static final String LINK    = "link";
    private static final String DESCRIPTION = "description";
    private static final String GENRES  = "genres";
    private static final String COVER   = "cover";
    private static final String SMALL   = "small";
    private static final String BIG     = "big";
    //окончания для "альбом" и "песня"
    private static final String[] ALBUMS_ENDS = {"", "а", "ов"};
    private static final String[] TRACKS_ENDS = {"ня", "ни", "ен"};
    ///вспомогательные строки для метода getStringAlbumsAndTracks
    private static final String ALBUM_FOR_RETURN = " альбом";
    private static final String TRACK_FOR_RETURN = " пес";

    //основной конструктор
    public Artist(Integer id, Integer tracks, Integer albums, String name,
                  String link, String description, String[] genres, String[] cover) {
        this.id = id;
        this.tracks = tracks;
        this.albums = albums;
        this.name = name;
        this.link = link;
        this.description = description;
        this.genres = genres;
        this.cover = cover;
    }

    //вспомогательный конструктор
    public Artist(Integer id, Integer tracks, Integer albums, String name,
                  String link, String description, JSONArray genres, JSONObject cover) throws JSONException {
        this(
                id, tracks, albums,
                name, link, description,
                getStringArrayFromJSONArray(genres),
                getStringArrayFromJSONObject(cover,
                        new String[]{SMALL, BIG})
        );
    }

    //вспомогательный конструктор
    public Artist(Parcel in) {
        this(
                in.readInt(),   in.readInt(),
                in.readInt(),   in.readString(),
                in.readString(),in.readString(),
                in.createStringArray(), in.createStringArray()
        );
    }

    public Artist(JSONObject obj) throws JSONException {
        this(
                (Integer) obj.get(ID), (Integer) obj.get(TRACKS),
                (Integer) obj.get(ALBUMS), (String) obj.get(NAME),
                (String) ifHasGetElseNull(obj, LINK), (String) obj.get(DESCRIPTION),
                (JSONArray) obj.get(GENRES), (JSONObject) obj.get(COVER)
        );
    }

    //конвертирует JSONArray в String[]
    private static String[] getStringArrayFromJSONArray (JSONArray jsonArray) throws JSONException {
        String[] array = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }

        return array;
    }

    //конвертирует JSONObject в String[]
    private static String[] getStringArrayFromJSONObject (JSONObject jsonObject,
                                                          String[] image_ids) {
        String[] array = new String[jsonObject.length()];

        try {
            int i = 0;

            for (String id : image_ids) {
                array[i++] = jsonObject.getString(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }

    //переопределения для реализации интерфейса Parcelable
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(tracks);
        dest.writeInt(albums);

        dest.writeString(name);
        dest.writeString(link);
        dest.writeString(description);

        dest.writeStringArray(genres);
        dest.writeStringArray(cover);
    }

    //достает из obj значение с key
    public static Object ifHasGetElseNull(JSONObject obj, String key) throws JSONException {
        return obj.has(key) ? obj.get(key) : null;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTracks() {
        return tracks;
    }

    public Integer getAlbums() {
        return albums;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String[] getGenres() {
        return genres;
    }

    public String[] getCover() {
        return cover;
    }

    /*
        правильное окончание в словосочетании с числом
        !!!массив окончаний должен содержать 3 окончания: для чисел заканчивающихся на 1;
            для чисел на 2, 3, 4; для чисел на 0, 5, 6, 7, 8, 9 и 11-19;
     */
    public String getRussianEnd(Integer number, String[] ends) {
        assert (ends.length == 3);

        String resultStr = ends[0];

        switch (number % 10) {
            case 1:
                break;

            case 2:
            case 3:
            case 4:
                resultStr = ends[1];
                break;

            case 0:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                resultStr = ends[2];
                break;
        }

        if (number / 10 % 10 == 1) {
            resultStr =  ends[2];
        }

        return resultStr;
    }

    //отдает строку количества альбомов и песен
    public String getStringAlbumsAndTracks (String delim) {
        Integer albums = getAlbums();
        Integer tracks = getTracks();
        String[] albumsEnds = ALBUMS_ENDS;
        String[] tracksEnds = TRACKS_ENDS;

        return  albums + ALBUM_FOR_RETURN + getRussianEnd(albums, albumsEnds) +
                delim  +
                tracks + TRACK_FOR_RETURN + getRussianEnd(tracks, tracksEnds);
    }

    //отдает строку жанров ('pop, dance')
    public String getStringGenres(String delim) {
        String resultStr = "";

        for (int i = 0; i < genres.length - 2; i++) resultStr += genres[i] + delim;

        return genres.length > 0 ? resultStr + genres[genres.length - 1] : "";
    }
}
