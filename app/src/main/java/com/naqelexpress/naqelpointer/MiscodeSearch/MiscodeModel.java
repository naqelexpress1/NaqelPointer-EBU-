package com.naqelexpress.naqelpointer.MiscodeSearch;

/**
 * Created by ravi on 16/11/17.
 */

public class MiscodeModel {
    String countrycode;
    String citycode;
    String cityname;
    int FacilityID;

    public MiscodeModel() {
    }

    public void setFacilityID(int facilityID) {
        FacilityID = facilityID;
    }

    public int getFacilityID() {
        return FacilityID;
    }

    public String getCountryCode() {
        return countrycode;
    }

    public String getCityCode() {
        return citycode;
    }

    public String getCityName() {
        return cityname;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}
