package com.naqelexpress.naqelpointer.utils;

import com.naqelexpress.naqelpointer.GlobalVar;
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

    public static ArrayList<String> FuelModelstoList(List list) {
        //Iterator<Map.Entry<String, String>> iterator = list.iterator();
        //for ()
        ArrayList<String> toList = new ArrayList<>();
        int init = 0;

        for (int i = 0; i < list.size(); i++) {
            toList.add(((com.naqelexpress.naqelpointer.Models.FuelTypeModel.FuelType) list.get(i)).getFName());
        }
        return toList;

    }

    public static ArrayList<String> SupplierModelstoList(List list) {
        //Iterator<Map.Entry<String, String>> iterator = list.iterator();
        //for ()
        ArrayList<String> toList = new ArrayList<>();
        int init = 0;

        for (int i = 0; i < list.size(); i++) {
            toList.add(((com.naqelexpress.naqelpointer.Models.FuelTypeModel.SupplierType) list.get(i)).getFName());
        }
        return toList;

    }

    public String findwaybillno(String WaybillNo) {
        String wno = "";

        if (WaybillNo.length() >= 8 && GlobalVar.WaybillNoStartSeries.contains(WaybillNo.substring(0, 1)))
            wno = WaybillNo.substring(0, 8);
        else if (WaybillNo.length() > 8)
            wno = WaybillNo.substring(0, GlobalVar.ScanWaybillLength);
        else
            wno = WaybillNo;//.substring(0, GlobalVar.ScanWaybillLength);

        return wno;
    }

}


