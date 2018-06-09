package org.devfleet.zkillboard.zkilla.eve;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ESIClient {

    interface UniverseApi {

        /**
         * Get names and categories for a set of ID&#39;s
         * Resolve a set of IDs to names and categories. Supported ID&#39;s for resolving are: Characters, Corporations, Alliances, Stations, Solar Systems, Constellations, Regions, Types.  ---
         * @param ids The ids to resolve (required)
         * @param datasource The server name you would like data from (optional, default to tranquility)
         * @return Call&lt;List<PostUniverseNames200Ok>&gt;
         */

        @POST("v2/universe/names/")
        Call<List<ESIName>> postUniverseNames(
                @Body List<Long> ids,
                @Query("datasource") String datasource);

    }

    private static final Logger LOG = LoggerFactory.getLogger(ESIClient.class);

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final UniverseApi api;
    private final String datasource;

    public ESIClient(
            final File cache,
            final long cacheSize,
            final long timeout) {
        this("tranquility", cache, cacheSize, timeout);
    }

    public ESIClient(
            final String datasource,
            final File cache,
            final long cacheSize,
            final long timeout) {
        this.datasource = datasource;
        OkHttpClient.Builder builder =
                new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .addInterceptor(chain -> {
                            Request.Builder rb = chain
                                    .request()
                                    .newBuilder()
                                    .addHeader("User-Agent", "org.devfleet.zkillboard.zkilla");
                            return chain.proceed(rb.build());
                        })

                        .addInterceptor(chain -> {
                            final Response r = chain.proceed(chain.request());
                            final String warning = r.header("Warning");
                            if (StringUtils.isNotBlank(warning)) {
                                LOG.warn("ESI warning: {}; url=", warning, chain.request().url());
                            }
                            return r;
                        });

        if (LOG.isDebugEnabled()) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(log);
        }

        if (timeout != -1) {
            builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
            builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        }

        if (null != cache) {
            builder.cache(new Cache(cache, cacheSize));
        }

        final OkHttpClient httpClient = builder
                .certificatePinner(
                    new CertificatePinner.Builder()
                            .add("esi.evetech.net", "sha256/+c+jwvHBmxzEAAW5Sht+t2O36FiazhlJg2kyrymYBJA=")
                            .add("esi.evetech.net", "sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA=")
                            .add("esi.evetech.net", "sha256/KwccWaCgrnaw6tsrrSO61FgLacNgG2MMLq8GE6+oP5I=")
                            .build())
                .build();
        final Retrofit rf =
                new Retrofit.Builder()
                        .baseUrl("https://esi.evetech.net/")
                        .addConverterFactory(JacksonConverterFactory.create(MAPPER))
                        .client(httpClient)
                        .build();

        this.api = rf.create(UniverseApi.class);
    }

    public List<ESIName> findNames(final List<Long> names) {
        try {
            final retrofit2.Response<List<ESIName>> r = api.postUniverseNames(names, this.datasource).execute();
            if (r.isSuccessful()) {
                return r.body();
            }
            LOG.error(r.message());
            return Collections.emptyList();
        }
        catch (IOException e) {
            LOG.debug(e.getLocalizedMessage(), e);
            LOG.error(e.getLocalizedMessage());
            return Collections.emptyList();
        }
    }
}
