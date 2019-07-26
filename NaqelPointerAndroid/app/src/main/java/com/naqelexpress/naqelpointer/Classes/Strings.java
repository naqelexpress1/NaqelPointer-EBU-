package com.naqelexpress.naqelpointer.Classes;

class Strings
{
//    public static Boolean isNullOrEmpty(String value) {
//        return value == null || value.length() == 0;
//    }


    static Boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().length() == 0;
    }

    static String format(String format, Object... params)
    {
        for(int i = 0; i< params.length; i++)
        {
            format = format.replaceAll(String.format("\\{%s\\}", i), params[i].toString());
        }
        return format;
    }
}
