package com.naqelexpress.naqelpointer.Classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

public class JsonSerializerDeserializer
{
    private static Gson handler = null;

    private static void initialize(Boolean useDotNetFormat)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeSerializer(useDotNetFormat));
        builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer(useDotNetFormat));
        handler = builder.create();
    }

    private JsonSerializerDeserializer()
    {

    }

    public static <T> String serialize(T instance, Boolean useDotNetFormat)
    {
        initialize(useDotNetFormat);
        if (useDotNetFormat) {
            return (handler.toJson(instance, instance.getClass())).replace("/", "\\/");
        } else {
            return handler.toJson(instance, instance.getClass());
        }
    }

    public static <T> T deserialize(String json, Class<T> resultType)
    {
        initialize(json.contains("\\/Date("));
        return handler.fromJson(json, resultType);
    }
}
