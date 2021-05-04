package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.Models.DistrictDataModel;
import com.naqelexpress.naqelpointer.Models.SkipRouteLineSeqWaybillnoReasonModels;

import java.util.ArrayList;
import java.util.List;

public class utilities {


    //Convert List from Models
    public static ArrayList<String> ModelstoList(List list) {
        //Iterator<Map.Entry<String, String>> iterator = list.iterator();
        //for ()
        ArrayList<String> toList = new ArrayList<>();
        int init = 0;

        for (int i = 0; i < list.size(); i++) {
            toList.add(((SkipRouteLineSeqWaybillnoReasonModels) list.get(i)).getReason());
        }
        return toList;

    }

    public static ArrayList<String> DistrictModelstoList(List list) {
        ArrayList<String> toList = new ArrayList<>();
        toList.add("Select District");
        for (int i = 0; i < list.size(); i++) {
            toList.add(((DistrictDataModel) list.get(i)).getName());
        }
        return toList;

    }
}


