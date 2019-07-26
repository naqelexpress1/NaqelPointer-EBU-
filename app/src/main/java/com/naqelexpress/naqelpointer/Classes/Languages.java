package com.naqelexpress.naqelpointer.Classes;

import com.naqelexpress.naqelpointer.GlobalVar;

/**
 * Created by sofan on 12/03/2018.
 */

public class Languages
{
    public String Code = "";
    public String Name = "";

    public Languages(String code, String name)
    {
        Code = code;
        Name = name;
        GlobalVar.GV().LanguageNameList.add(Name);
    }
}