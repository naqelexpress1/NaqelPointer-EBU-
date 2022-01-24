package com.naqelexpress.naqelpointer.Retrofit.Models;


import com.naqelexpress.naqelpointer.JSON.Results.DefaultResult;

public class OnLineValidation extends DefaultResult {

    private int ID;
    private String Barcode;
    private int CustomerWaybillDestID;
    private int WaybillDestID;
    private int NoOfAttempts;
    private int IsMultiPiece;
    private int IsStopped;
    private int IsRTORequest = 0;
    private int IsDeliveryRequest = 0;
    private int IsRelabel;
    private int IsWrongDest;
    private int IsDestNotBelongToNcl;
    private int IsPiecesAvailable = 1;
    private int IsConflict;
    private int IsManifested = 1;
    private int IsCITCComplaint = 0;
    private int WaybillNo;
    private int IsNoBayanNo;
    private boolean IsNotInFile;
    private boolean IsDestChanged;
    private boolean isHV;
    private int IsCAFRequest = 0;
    private int ReasonID = 0;
    private int ReasonDetailID = 0;
    private int ReasonDDetailID = 0;
    private boolean isManifestedalert = false;
    private boolean isStoppedalert = false;
    private boolean isCITCalert = false;
    private boolean isMultiPiecealert = false;
    private boolean isNoofAttemptsalert = false;
    private boolean isWrongDestalert = false;
    private boolean NoValidation = false;
    private boolean isCAFlert = false;
    private boolean isDLalert = false;

    public boolean isConflictalert() {
        return isConflictalert;
    }

    public void setConflictalert(boolean conflictalert) {
        isConflictalert = conflictalert;
    }

    private boolean isConflictalert = false;

    public boolean isDLalert() {
        return isDLalert;
    }

    public void setDLalert(boolean DLalert) {
        isDLalert = DLalert;
    }

    public boolean isCAFlert() {
        return isCAFlert;
    }

    public void setCAFlert(boolean CAFlert) {
        isCAFlert = CAFlert;
    }

    public boolean isNoValidation() {
        return NoValidation;
    }

    public void setNoValidation(boolean noValidation) {
        NoValidation = noValidation;
    }

    public boolean isRTOalert() {
        return isRTOalert;
    }

    public void setRTOalert(boolean RTOalert) {
        isRTOalert = RTOalert;
    }

    private boolean MultipiecePopup = false;
    private boolean isRTOalert = false;
    private String ClassName = "";

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public boolean isMultipiecePopup() {
        return MultipiecePopup;
    }

    public void setMultipiecePopup(boolean multipiecePopup) {
        MultipiecePopup = multipiecePopup;
    }
//private boolean ischangedDestalert = false;

    public boolean getisWrongDestalert() {
        return isWrongDestalert;
    }

    public void setWrongDestalert(boolean wrongDestalert) {
        isWrongDestalert = wrongDestalert;
    }

    public boolean getisNoofAttemptsalert() {
        return isNoofAttemptsalert;
    }

    public void setNoofAttemptsalert(boolean noofAttemptsalert) {
        isNoofAttemptsalert = noofAttemptsalert;
    }

    public boolean getisStoppedalert() {
        return isStoppedalert;
    }

    public void setStoppedalert(boolean stoppedalert) {
        isStoppedalert = stoppedalert;
    }

    public boolean getisManifestedalert() {
        return isManifestedalert;
    }

    public void setManifestedalert(boolean manifestedalert) {
        isManifestedalert = manifestedalert;
    }

    public boolean getisCITCalert() {
        return isCITCalert;
    }

    public void setCITCalert(boolean CITCalert) {
        isCITCalert = CITCalert;
    }

    public boolean getisMultiPiecealert() {
        return isMultiPiecealert;
    }

    public void setMultiPiecealert(boolean multiPiecealert) {
        isMultiPiecealert = multiPiecealert;
    }

//    public boolean getIschangedDestalert() {
//        return ischangedDestalert;
//    }
//
//    public void setIschangedDestalert(boolean ischangedDestalert) {
//        this.ischangedDestalert = ischangedDestalert;
//    }

    public int getReasonID() {
        return ReasonID;
    }

    public void setReasonID(int reasonID) {
        ReasonID = reasonID;
    }

    public int getReasonDetailID() {
        return ReasonDetailID;
    }

    public void setReasonDetailID(int reasonDetailID) {
        ReasonDetailID = reasonDetailID;
    }

    public int getReasonDDetailID() {
        return ReasonDDetailID;
    }

    public void setReasonDDetailID(int reasonDDetailID) {
        ReasonDDetailID = reasonDDetailID;
    }

    public boolean setisHV() {
        return isHV;
    }

    public void setHV(boolean HV) {
        isHV = HV;
    }

    public int getIsCAFRequest() {
        return IsCAFRequest;
    }

    public void setIsCAFRequest(int isCAFRequest) {
        IsCAFRequest = isCAFRequest;
    }


    public boolean getisHV() {
        return isHV;
    }

    public void setisHV(boolean HV) {
        isHV = HV;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
    }

    public void setNotInFile(boolean notInFile) {
        IsNotInFile = notInFile;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        NoOfAttempts = noOfAttempts;
    }

    public void setIsMultiPiece(int isMultiPiece) {
        IsMultiPiece = isMultiPiece;
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

    public void setIsCITCComplaint(int IsCITCComplaint) {
        this.IsCITCComplaint = IsCITCComplaint;
    }

    public void setIsNoBayanNo(int isNoBayanNo) {
        IsNoBayanNo = isNoBayanNo;
    }

    public void setWaybillNo(int waybillNo) {
        WaybillNo = waybillNo;
    }

    public void setIsManifested(int isManifested) {
        IsManifested = isManifested;
    }

    public void setIsWrongDest(int isWrongDest) {
        IsWrongDest = isWrongDest;
    }

    public void setIsDestNotBelongToNcl(int isDestBelongToNcl) {
        IsDestNotBelongToNcl = isDestBelongToNcl;
    }

    public void setCustomerWaybillDestID(int customerWaybillDestID) {
        CustomerWaybillDestID = customerWaybillDestID;
    }

    public void setWaybillDestID(int waybillDestID) {
        WaybillDestID = waybillDestID;
    }

    public void setIsRelabel(int isRelabel) {
        IsRelabel = isRelabel;
    }

    public void setIsPiecesAvailable(int isPiecesAvailable) {
        IsPiecesAvailable = isPiecesAvailable;
    }

    public void setDestChanged(boolean destChanged) {
        IsDestChanged = destChanged;
    }

    public void setIsConflict(int isConflict) {
        IsConflict = isConflict;
    }

    public int getID() {
        return ID;
    }

    public String getBarcode() {
        return Barcode;
    }

    public int getCustomerWaybillDestID() {
        return CustomerWaybillDestID;
    }

    public int getWaybillDestID() {
        return WaybillDestID;
    }

    public int getNoOfAttempts() {
        return NoOfAttempts;
    }

    public int getIsMultiPiece() {
        return IsMultiPiece;
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

    public int getIsManifested() {
        return IsManifested;
    }

    public int getWaybillNo() {
        return WaybillNo;
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

    public int getIsCITCComplaint() {
        return IsCITCComplaint;
    }

    public int getIsNoBayanNo() {
        return IsNoBayanNo;
    }


    public boolean isDestChanged() {
        return IsDestChanged;
    }

    public boolean isNotInFile() {
        return IsNotInFile;
    }

    @Override
    public String toString() {
        return "OnLineValidation{" +
                "ID=" + ID +
                ", Barcode='" + Barcode + '\'' +
                ", CustomerWaybillDestID=" + CustomerWaybillDestID +
                ", WaybillDestID=" + WaybillDestID +
                ", NoOfAttempts=" + NoOfAttempts +
                ", IsMultiPiece=" + IsMultiPiece +
                ", IsStopShipment=" + IsStopped +
                ", IsRTORequest=" + IsRTORequest +
                ", IsDeliveryRequest=" + IsDeliveryRequest +
                ", IsRelabel=" + IsRelabel +
                ", IsWrongDest=" + IsWrongDest +
                ", IsDestNotBelongToNcl=" + IsDestNotBelongToNcl +
                ", IsPiecesAvailable=" + IsPiecesAvailable +
                ", IsConflict=" + IsConflict +
                ", IsManifested=" + IsManifested +
                ", WaybillNo=" + WaybillNo +
                '}';
    }
}
