package org.devfleet.zkillboard.zkilla.eve;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public final class ZKillProvider extends android.content.ContentProvider {

    private static Logger LOG = LoggerFactory.getLogger(ZKillProvider.class);

    private static final String AUTHORITY = "org.devfleet.zkillboard";
    private static final int CONTENT_NAMES = 0;
    private static final int CONTENT_CHARACTERS = 1;
    private static final int CONTENT_CORPORATIONS = 2;
    private static final int CONTENT_ALLIANCES = 3;

    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri NAMES = URI.buildUpon().appendPath("names").build();
    public static final Uri CHARACTERS = URI.buildUpon().appendPath("characters").build();
    public static final Uri CORPORATIONS = URI.buildUpon().appendPath("corporations").build();
    public static final Uri ALLIANCES = URI.buildUpon().appendPath("alliances").build();

    interface ZKillService {

        @GET("/autocomplete/{search}/")
        Call<List<ZKillEntry>> searchNames(@Path("search") final String search);

        @GET("/autocomplete/{type}/{search}/")
        Call<List<ZKillEntry>> filterTypes(
                @Path("search") final String search,
                @Path("type") final String type);
    }

    public static class ZKillEntry implements Serializable {
        public static final String CHARACTER = "character";
        public static final String CORPORATION = "corporation";
        public static final String ALLIANCE = "alliance";
        //{"id":162334834,"name":"Jita Mercentile and Investments","type":"corporation","image":"Corporation\/162334834_32.png"}

        @JsonProperty
        private Long id;

        @JsonProperty
        private String name;

        @JsonProperty
        private String type;

        @JsonProperty
        private String image;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getImage() {
            return image;
        }

        public ZKillEntry setId(final Long id) {
            this.id = id;
            return this;
        }

        public ZKillEntry setName(final String name) {
            this.name = name;
            return this;
        }

        public ZKillEntry setType(final String type) {
            this.type = type;
            return this;
        }

        public ZKillEntry setImage(final String image) {
            this.image = image;
            return this;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ZKillEntry)) {
                return false;
            }
            return name.equals(((ZKillEntry)obj).name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, "names/search_suggest_query", CONTENT_NAMES);
        uriMatcher.addURI(AUTHORITY, "names/search_suggest_query/*", CONTENT_NAMES);

        uriMatcher.addURI(AUTHORITY, "characters/search_suggest_query", CONTENT_CHARACTERS);
        uriMatcher.addURI(AUTHORITY, "characters/search_suggest_query/*", CONTENT_CHARACTERS);

        uriMatcher.addURI(AUTHORITY, "corporations/search_suggest_query", CONTENT_CORPORATIONS);
        uriMatcher.addURI(AUTHORITY, "corporations/search_suggest_query/*", CONTENT_CORPORATIONS);

        uriMatcher.addURI(AUTHORITY, "alliances/search_suggest_query", CONTENT_ALLIANCES);
        uriMatcher.addURI(AUTHORITY, "alliances/search_suggest_query/*", CONTENT_ALLIANCES);
    }

    private ZKillService service;

    @Override
    public boolean onCreate() {
        final OkHttpClient.Builder bob = new OkHttpClient.Builder();
        this.service =
                new Retrofit.Builder()
                .client(bob.build())
                .baseUrl("https://zkillboard.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(ZKillService.class);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String term = uri.getLastPathSegment();
        term = (null == term) ? "" : term.trim();
        if (term.length() < 4) {
            return new MatrixCursor(new String[] { "_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2});
        }

        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case CONTENT_NAMES:
                cursor = searchNames(term, null);
                break;
            case CONTENT_CHARACTERS:
                cursor = searchNames(term, "characterID");
                break;
            case CONTENT_CORPORATIONS:
                cursor = searchNames(term, "corporationID");
                break;
            case CONTENT_ALLIANCES:
                cursor = searchNames(term, "allianceID");
                break;
            default:
                break;
        }

        return (null == cursor) ?
                new MatrixCursor(new String[] { "_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2}) : cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            //We do not export our data
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    public static Cursor searchAll(final ContentResolver cr, final String search) {
        return searchImpl(cr, NAMES, search);
    }

    public static Cursor searchCharacters(final ContentResolver cr, final String search) {
        return searchImpl(cr, CHARACTERS, search);
    }

    public static Cursor searchCorporations(final ContentResolver cr, final String search) {
        return searchImpl(cr, CORPORATIONS, search);
    }

    public static Cursor searchAlliances(final ContentResolver cr, final String search) {
        return searchImpl(cr, ALLIANCES, search);
    }

    private static Cursor searchImpl(final ContentResolver cr, final Uri uri, final String search) {
        return cr.query(
                uri
                .buildUpon()
                .appendPath("search_suggest_query")
                .appendPath(search)
                .build(), null, null, null, null);
    }

    private Cursor searchNames(final String param, final String type) {
        final MatrixCursor cursor = new MatrixCursor(
                new String[]{"_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2});

        try {
            final Response<List<ZKillEntry>> search = (StringUtils.isBlank(type)) ?
                    this.service.searchNames(param).execute() : this.service.filterTypes(param, type).execute();
            if (!search.isSuccessful()) {
                return cursor;
            }

            int count = 0;
            for (ZKillEntry e : search.body()) {
                cursor.addRow(new Object[]{
                        e.getId(),
                        e.getName(),
                        e.getType()});
                count = count + 1;
            }
        }
        catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return cursor;
    }
}
