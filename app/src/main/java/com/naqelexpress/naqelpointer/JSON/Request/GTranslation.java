package com.naqelexpress.naqelpointer.JSON.Request;

import java.lang.annotation.Target;

/**
 * Created by sofan on 12/03/2018.
 */

public class GTranslation
{
    public String message = "";
    public String targetLanguage = "";

    public GTranslation(String Message, String TargetLanguage)
    {
        this.message = Message;
        this.targetLanguage = TargetLanguage;
    }
}