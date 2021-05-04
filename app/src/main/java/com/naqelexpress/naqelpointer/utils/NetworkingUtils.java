package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Retrofit.IPointerAPI;
import com.naqelexpress.naqelpointer.Retrofit.RetrofitAdapter;

public class NetworkingUtils {

    private static IPointerAPI iPointerAPI;

    public static IPointerAPI getUserApiInstance() {
        if (iPointerAPI == null)
            iPointerAPI = RetrofitAdapter.getInstance().create(IPointerAPI.class);

        return iPointerAPI;
    }
}
