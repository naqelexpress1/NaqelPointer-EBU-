package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Retrofit.IPointerAPI;
import com.naqelexpress.naqelpointer.Retrofit.RetrofitAdapter;

public class NetworkingUtils {

    private static IPointerAPI iPointerAPI;

    public static IPointerAPI getUserApiInstance() {
        if (iPointerAPI == null) {
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS);
//            Retrofit.Builder builder = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(SimpleXmlConverterFactory.create());
//
//            builder.client(httpClient.build());
//
//            Retrofit retrofit = builder.build();
            iPointerAPI = RetrofitAdapter.getInstance().create(IPointerAPI.class);
        }

        return iPointerAPI;
    }
}
