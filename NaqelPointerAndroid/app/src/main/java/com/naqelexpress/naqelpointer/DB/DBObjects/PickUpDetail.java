package com.naqelexpress.naqelpointer.DB.DBObjects;

public class PickUpDetail
{
        public int ID = 0;
        public String BarCode = "";
        public boolean IsSync = false;
        public int PickUpID = 0;

        public PickUpDetail(String barCode, int pickUpID)
        {
            IsSync = false;
            BarCode = barCode;
            PickUpID = pickUpID;
        }
}