package org.devfleet.zkillboard.zkilla.eve;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public final class EveImages {

    public interface BitmapReceiver {
        void setBitmap(final Bitmap bitmap);
    }

    private static final Logger LOG = LoggerFactory.getLogger(EveImages.class);

    private static final String IMAGE_CHARACTER = "%s/Character/%s_512.jpg";
    private static final String ICON_CHARACTER = "%s/Character/%s_128.jpg";

    private static final String IMAGE_CORPORATION = "%s/Corporation/%s_256.png";
    private static final String ICON_CORPORATION = "%s/Corporation/%s_128.png";

    private static final String IMAGE_ALLIANCE = "%s/Alliance/%s_256.png";
    private static final String ICON_ALLIANCE = "%s/Alliance/%s_128.png";

    private static final String IMAGE_ITEM = "%s/Render/%s_512.png";
    private static final String ICON_ITEM = "%s/Type/%s_64.png";

    private static EveImages INSTANCE;

    private RequestManager glide;

    private EveImages(final Context context) {
        this.glide = Glide.with(context);
    }

    @MainThread
    public void load(final String uri, final BitmapReceiver into) {
        glide.asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull final Bitmap resource, @Nullable final Transition<? super Bitmap> transition) {
                into.setBitmap(resource);
            }
        });
    }

    @WorkerThread
    public void submit(final String uri, final BitmapReceiver into) {
        final FutureTarget<Bitmap> target = glide.asBitmap().load(uri).submit();
        try {
            into.setBitmap(target.get());
        }
        catch (InterruptedException | ExecutionException e) {
            LOG.warn(e.getLocalizedMessage(), e);
        }
    }

    public static EveImages from(final Context context) {
        if (null == INSTANCE) {
            INSTANCE = new EveImages(context.getApplicationContext());
        }
        return INSTANCE;
    }

    @MainThread
    public static void itemIcon(final long typeId, ImageView into) {
        itemIcon(typeId, into, true);
    }

    @MainThread
    public static void itemIcon(final long typeId, ImageView into, final boolean round) {
        if (round) {
            round(itemIcon(typeId), into);
        }
        else {
            load(itemIcon(typeId), into);
        }
    }

    @MainThread
    public static void characterIcon(final long charId, ImageView into) {
        characterIcon(charId, into, true);
    }

    @MainThread
    public static void characterIcon(final long charId, ImageView into, final boolean round) {
        if (round) {
            round(characterIcon(charId), into);
        }
        else {
            load(characterIcon(charId), into);
        }
    }

    @MainThread
    public static void load(final String uri, ImageView into) {
        EveImages.from(into.getContext()).glide.load(uri).into(into);
    }

    @MainThread
    public static void round(final String uri, ImageView into) {
        EveImages.from(into.getContext()).glide
                .load(uri)
                .apply(new RequestOptions().circleCrop())
                .into(into);
    }

    public static String itemImage(final long itemID) {
        return url(IMAGE_ITEM, itemID);
    }

    public static String itemIcon(final long itemID) {
        return url(ICON_ITEM, itemID);
    }

    public static String characterImage(final long charID) {
        return url(IMAGE_CHARACTER, charID);
    }

    public static String characterIcon(final long charID) {
        return url(ICON_CHARACTER, charID);
    }

    public static String corporationImage(final long corpID) {
        return url(IMAGE_CORPORATION, corpID);
    }

    public static String corporationIcon(final long corpID) {
        return url(ICON_CORPORATION, corpID);
    }

    public static String allianceImage(final long allianceID) {
        return url(IMAGE_ALLIANCE, allianceID);
    }

    public static String allianceIcon(final long allianceID) {
        return url(ICON_ALLIANCE, allianceID);
    }

    private static String url(final String urlConstant, final long typeID) {
        return String.format(urlConstant, "https://image.eveonline.com", Long.toString(typeID));
    }
}
