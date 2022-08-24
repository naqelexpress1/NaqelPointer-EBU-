package com.naqelexpress.naqelpointer.Retrofit.Interface;

import android.content.Context;
import android.widget.Toast;


import com.naqelexpress.naqelpointer.GlobalVar;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/***
 *
 * @author Holoteq developer (holoteqdevelopers@gmail.com)
 * @since   2019-01-11
 */

// setting up the retrofit client
public class OkRetrofitClient<T> {
    //TODO Update to live link
    String url = GlobalVar.GV().NaqelPointerAPILink;
    private T client;

    private OkRetrofitClient(){ }

    public OkRetrofitClient(Context context,
                            Class<T> interfaceClass,
                            long connectionTimeout,
                            TimeUnit connectionTimeoutUnit,
                            long readTimeout,
                            TimeUnit readTimeoutUnit,
                            long writeTimeout,
                            TimeUnit writeTimeoutUnit,
                            String baseURL){

        try {
            // set your desired log level
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeout, TimeUnit.MINUTES)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS);


            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(baseURL)//
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.client(httpClient.build()).build();
            client = (T) retrofit.create(interfaceClass);
        }catch (Exception e){
            Toast.makeText(context,e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }

    public T getClient() {
        return client;
    }

    public void setClient(T client) {
        this.client = client;
    }
}
