package com.naqelexpress.naqelpointer.OnlineValidation;

public class OnLineValidation {

    private int ID;
    private String Barcode;
    private int CustomerWaybillDestID;
    private int WaybillDestID;
    private int NoOfAttempts;
    private int IsMultiPiece;
    private int IsStopShipment;
    private int IsRTORequest;
    private int IsDeliveryRequest;
    private int IsRelabel;
    private int IsWrongDest;
    private int IsDestNotBelongToNcl;
    private int IsPiecesAvailable = 1;
    private int IsConflict;
    private int IsManifested = 1;
    private int IsCITCComplain;
    private int WaybillNo;
    private boolean IsNotInFile;


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

    public void setIsStopShipment(int isStopShipment) {
        IsStopShipment = isStopShipment;
    }

    public void setIsRTORequest(int isRTORequest) {
        IsRTORequest = isRTORequest;
    }

    public void setIsDeliveryRequest(int isDeliveryRequest) {
        IsDeliveryRequest = isDeliveryRequest;
    }

    public void setIsCITCComplain(int isCITCComplain) {
        IsCITCComplain = isCITCComplain;
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

    public int getIsStopShipment() {
        return IsStopShipment;
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

    public int getIsCITCComplain() {
        return IsCITCComplain;
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
                ", IsStopShipment=" + IsStopShipment +
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
