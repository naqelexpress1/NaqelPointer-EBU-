package com.naqelexpress.naqelpointer.Classes;

/**
 * Created by sofan on 03/08/2017.
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeDeserializer
        implements JsonDeserializer<DateTime>
{
    Boolean isInDotNetFormat;

    private DateTimeDeserializer() {
    }

    public DateTimeDeserializer(Boolean isInDotNetFormat) {
        this();
        this.isInDotNetFormat = isInDotNetFormat;
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        String jsonString = json.getAsJsonPrimitive().getAsString();
        if (isInDotNetFormat) {
            String Regexp = "\\/Date\\((\\-?\\d*?)([\\+\\-]\\d*)?\\)\\/";
            Pattern MyPattern = Pattern.compile(Regexp);
            Matcher MyMatcher = MyPattern.matcher(jsonString);
            MyMatcher.matches();
            Long time = new Long(MyMatcher.group(1));
            if (Strings.isNullOrWhiteSpace(MyMatcher.group(2))) {
                return new DateTime(time, DateTimeZone.UTC);
            } else {
                Integer offsetHours = Integer.parseInt(MyMatcher.group(2).substring(0, 3));
                Integer offsetMinutes = Integer.parseInt(MyMatcher.group(2).substring(3, MyMatcher.group(2).length()));
                int offset = DateTimeZone.forOffsetHoursMinutes(offsetHours, offsetMinutes).getOffsetFromLocal(time);
                return new DateTime(time + offset).toDateTime(DateTimeZone.UTC);
            }

        } else {
            DateTime t = DateTime.parse(jsonString.substring(0, jsonString.length()));
            if (t.getZone() != DateTimeZone.UTC) {
                int offset = t.getZone().getOffsetFromLocal(t.getMillis());
                t = t.toDateTime(DateTimeZone.UTC).plus(offset);
            }
            return t;
        }
    }
}
