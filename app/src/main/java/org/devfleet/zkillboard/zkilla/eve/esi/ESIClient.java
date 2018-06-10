package org.devfleet.zkillboard.zkilla.eve.esi;

import com.annimon.stream.Stream;
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
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ESIClient {

    private static final Logger LOG = LoggerFactory.getLogger(ESIClient.class);

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final ESIApi api;
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

        this.api = rf.create(ESIApi.class);
    }

    public List<ESIType> findTypes(final List<Long> ids, final String language) {
        return Stream.of(ids).map(id -> {
            try {
                final retrofit2.Response<ESIType> r = api.getUniverseType(id, this.datasource, language).execute();
                if (r.isSuccessful()) {
                    return r.body();
                }
                LOG.error(r.message());
                return null;
            }
            catch (IOException e) {
                LOG.debug(e.getLocalizedMessage(), e);
                LOG.error(e.getLocalizedMessage());
                return null;
            }
        })
        .filter(t -> null != t)
        .toList();
    }

    //FIXME (ESI) language not supported on names endpoint...
    public List<ESIName> findNames(final List<Long> names, final String language) {
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
