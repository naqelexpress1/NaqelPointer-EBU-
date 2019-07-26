package com.naqelexpress.naqelpointer.Classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

class DateTimeSerializer
        implements JsonSerializer<DateTime>
{
    private Boolean toDotNetFormat;

//    private DateTimeSerializer()
//    {
//    }

    DateTimeSerializer(Boolean isInDotNetFormat)
    {
        this.toDotNetFormat = isInDotNetFormat;
    }

    @Override
    public JsonElement serialize(DateTime t, Type type, JsonSerializationContext jsc) {
        if (t.getZone() != DateTimeZone.UTC) {
            int offset = t.getZone().getOffsetFromLocal(t.getMillis());
            t = t.toDateTime(DateTimeZone.UTC).plus(offset);
        }
        return toDotNetFormat ? new JsonPrimitive(Strings.format("/Date({0})/", t.getMillis())) : new JsonPrimitive(t.toString(ISODateTimeFormat.dateTime()));
    }
}
