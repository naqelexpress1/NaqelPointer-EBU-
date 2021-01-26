package com.naqelexpress.naqelpointer.Retrofit.Models;

import com.google.gson.annotations.SerializedName;

public class OnlineValidationOffset {

    @SerializedName("C1")
    private int ID;
    @SerializedName("C2")
    private int WaybillNo;
    @SerializedName("C3")
    private int WaybillDestID;
    @SerializedName("C4")
    private int IsMultiPiece;
    @SerializedName("C5")
    private int IsStopped;
    @SerializedName("C6")
    private int IsDeliveryRequest;
    @SerializedName("C7")
    private int IsRTORequest;
    @SerializedName("C8")
    private int NoOfAttempts;

    private int IsRelabel;
    private int IsWrongDest;
    private int IsDestNotBelongToNcl;
    private int IsPiecesAvailable = 1;
    private int IsConflict;
    private int IsManifested = 1;
    private int IsCITCComplaint;
    private int IsNoBayanNo;
    private boolean IsNotInFile;
    private boolean IsDestChanged;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public void setWaybillDestID(int waybillDestID) {
        WaybillDestID = waybillDestID;
    }

    public void setIsMultiPiece(int isMultiPiece) {
        IsMultiPiece = isMultiPiece;
    }

    public void setIsStopped(int isStopped) {
        IsStopped = isStopped;
    }

    public void setIsDeliveryRequest(int isDeliveryRequest) {
        IsDeliveryRequest = isDeliveryRequest;
    }

    public void setIsRTORequest(int isRTORequest) {
        IsRTORequest = isRTORequest;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        NoOfAttempts = noOfAttempts;
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

    public void setIsPiecesAvailable(int isPiecesAvailable) {
        IsPiecesAvailable = isPiecesAvailable;
    }

    public void setIsConflict(int isConflict) {
        IsConflict = isConflict;
    }

    public void setIsManifested(int isManifested) {
        IsManifested = isManifested;
    }

    public void setIsCITCComplaint(int isCITCComplaint) {
        IsCITCComplaint = isCITCComplaint;
    }

    public void setIsNoBayanNo(int isNoBayanNo) {
        IsNoBayanNo = isNoBayanNo;
    }

    public void setNotInFile(boolean notInFile) {
        IsNotInFile = notInFile;
    }

    public void setDestChanged(boolean destChanged) {
        IsDestChanged = destChanged;
    }

    public int getID() {
        return ID;
    }

    public int getWaybillNo() {
        return WaybillNo;
    }

    public int getWaybillDestID() {
        return WaybillDestID;
    }

    public int getIsMultiPiece() {
        return IsMultiPiece;
    }

    public int getIsStopped() {
        return IsStopped;
    }

    public int getIsDeliveryRequest() {
        return IsDeliveryRequest;
    }

    public int getIsRTORequest() {
        return IsRTORequest;
    }

    public int getNoOfAttempts() {
        return NoOfAttempts;
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

    public int getIsPiecesAvailable() {
        return IsPiecesAvailable;
    }

    public int getIsConflict() {
        return IsConflict;
    }

    public int getIsManifested() {
        return IsManifested;
    }

    public int getIsCITCComplaint() {
        return IsCITCComplaint;
    }

    public int getIsNoBayanNo() {
        return IsNoBayanNo;
    }

    public boolean isNotInFile() {
        return IsNotInFile;
    }

    public boolean isDestChanged() {
        return IsDestChanged;
    }
}
