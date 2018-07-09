package taller2.ataller2.services.Picasso;


import android.content.Context;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import taller2.ataller2.services.CustomService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

/***
 *  Picasso Adapter to handle backoffice authentication
 */

public class PicassoService implements CustomService {

    static volatile Picasso mPicassoInstance = null;

    private Context mContext;

    public PicassoService(Context context) {
        mContext = context;
        createPicassoInstance(context);
    }

    public Picasso getPicasso() {
        if (mPicassoInstance == null) {
            createPicassoInstance(mContext);
        }
        return mPicassoInstance;
    }

    private void createPicassoInstance(Context context) {
        final int cacheSize = 50 * 1024 * 1024; // 50 MB
        final String authToken = ServiceLocator.get(FacebookService.class).getAuthToken();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("authorization", authToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).cache(new Cache(context.getCacheDir(), cacheSize))
                .build();

        mPicassoInstance = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }
}
