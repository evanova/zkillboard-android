package org.devfleet.zkillboard.zkilla.eve;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ZKillClient {
    private static final Logger LOG = LoggerFactory.getLogger(ZKillClient.class);

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private final PublishSubject<String> subject = PublishSubject.create();

    private WebSocket ws = null;

    @CallSuper
    public void open(final String channel) {
        closeImpl();
        openImpl(channel);
    }

    @CallSuper
    public void close() {
        closeImpl();
    }

    @WorkerThread
    protected void onMessage(final ZKillData data) {}

    protected void onOpen() {}

    protected void onClose() {}

    protected void onFailure(Throwable t) {}

    private synchronized void closeImpl() {
        if (null != this.ws) {
            this.ws.close(1000, "");
            this.ws = null;
        }
    }

    private synchronized void openImpl(final String channel) {
        if (null != this.ws) {
            return;
        }

        final Disposable disposable = this.subject
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(s -> MAPPER.readValue(s, ZKillData.class))
                    .subscribe(d -> ZKillClient.this.onMessage(d));

        final OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30,  TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Request request = new Request.Builder()
                .url("wss://zkillboard.com:2096")
                .build();
        this.ws = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                LOG.debug("onOpen");
                ZKillClient.this.onOpen();

                ws.send("{\"action\":\"sub\",\"channel\":\"" + channel + "\"}");
            }

            @Override
            public void onMessage(final WebSocket webSocket, final String text) {
                LOG.debug(text);
                subject.onNext(text);
            }

            @Override
            public void onMessage(final WebSocket webSocket, final ByteString bytes) {

            }

            @Override
            public void onClosing(final WebSocket webSocket, final int code, final String reason) {
                LOG.debug("onClosing");
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public void onClosed(final WebSocket webSocket, final int code, final String reason) {
                LOG.debug("onClosed");
                ZKillClient.this.onClose();
            }

            @Override
            public void onFailure(final WebSocket webSocket, final Throwable t, @Nullable final Response response) {
                LOG.debug("onFailure", t);
                ZKillClient.this.onFailure(t);
            }
        });
    }
}
