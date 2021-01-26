package com.naqelexpress.naqelpointer.Retrofit.Models;

public class OnLineValidationGWT {


    private int ID;
    private String Barcode;
    private int WaybillDestID;
    private int IsStopped;
    private int IsRTORequest;
    private int IsDeliveryRequest;
    private int IsRelabel;
    private int IsWrongDest;
    private int IsDestNotBelongToNcl;
    private int IsManifested = 1;
    private int IsCITCComplaint;
    private int WaybillNo;
    private int IsNoBayanNo;
    private boolean IsDestChanged;


    public void setID(int ID) {
        this.ID = ID;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public void setWaybillDestID(int waybillDestID) {
        WaybillDestID = waybillDestID;
    }

    public void setIsStopped(int isStopped) {
        IsStopped = isStopped;
    }

    public void setIsRTORequest(int isRTORequest) {
        IsRTORequest = isRTORequest;
    }

    public void setIsDeliveryRequest(int isDeliveryRequest) {
        IsDeliveryRequest = isDeliveryRequest;
    }

    public void setIsRelabel(int isRelabel) {
        IsRelabel = isRelabel;
    }

    public void setIsWrongDest(int isWrongDest) {
        IsWrongDest = isWrongDest;
    }

    public void setIsDestNotBelongToNcl(int isDestNotBelongToNcl) {
        IsDestNotBelongToNcl = isDestNotBelongToNcl;
    }

    public void setIsManifested(int isManifested) {
        IsManifested = isManifested;
    }

    public void setIsCITCComplaint(int isCITCComplaint) {
        IsCITCComplaint = isCITCComplaint;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public void setIsNoBayanNo(int isNoBayanNo) {
        IsNoBayanNo = isNoBayanNo;
    }

    public void setDestChanged(boolean destChanged) {
        IsDestChanged = destChanged;
    }

    public int getID() {
        return ID;
    }

    public String getBarcode() {
        return Barcode;
    }

    public int getWaybillDestID() {
        return WaybillDestID;
    }

    public int getIsStopped() {
        return IsStopped;
    }

    public int getIsRTORequest() {
        return IsRTORequest;
    }

    public int getIsDeliveryRequest() {
        return IsDeliveryRequest;
    }

    public int getIsRelabel() {
        return IsRelabel;
    }

    public int getIsWrongDest() {
        return IsWrongDest;
    }

    public int getIsDestNotBelongToNcl() {
        return IsDestNotBelongToNcl;
    }

    public int getIsManifested() {
        return IsManifested;
    }

    public int getIsCITCComplaint() {
        return IsCITCComplaint;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public int getIsNoBayanNo() {
        return IsNoBayanNo;
    }

    public boolean isDestChanged() {
        return IsDestChanged;
    }
}

