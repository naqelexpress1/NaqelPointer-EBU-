package com.naqelexpress.naqelpointer.OnlineValidation;

public class OnLineValidation {

    private int ID;
    private String PieceBarcode;
    private int DestID;
    private int NoOfAttempts;
    private int IsMultiPiece;
    private int IsStopShipment;
    private int IsRTORequest;
    private int IsDeliveryRequest;
    private int IsRelabel;
    private int IsWrongDest;
    private int IsDestNotBelongToNcl;
    private int IsPiecesAvailable = 1;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setPieceBarcode(String pieceBarcode) {
        PieceBarcode = pieceBarcode;
    }

    public void setDestID(int destID) {
        DestID = destID;
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

    public void setIsWrongDest(int isWrongDest) {
        IsWrongDest = isWrongDest;
    }

    public void setIsDestNotBelongToNcl(int isDestBelongToNcl) {
        IsDestNotBelongToNcl = isDestBelongToNcl;
    }

    public void setIsRelabel(int isRelabel) {
        IsRelabel = isRelabel;
    }

    public void setIsPiecesAvailable(int isPiecesAvailable) {
        IsPiecesAvailable = isPiecesAvailable;
    }

    public int getID() {
        return ID;
    }

    public String getPieceBarcode() {
        return PieceBarcode;
    }

    public int getDestID() {
        return DestID;
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

    public int getIsWrongDest() {
        return IsWrongDest;
    }

    public int getIsDestNotBelongToNcl() {
        return IsDestNotBelongToNcl;
    }

    public int getIsPiecesAvailable() {
        return IsPiecesAvailable;
    }
}
