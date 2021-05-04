package com.naqelexpress.naqelpointer.Models.Request;

public  class AlertRequest {

    private int alertType;
    private boolean isCancelable;
    private String alrttitle;
    private String alrtmessage;
    private boolean isFinish;


    // Getter Methods

    public int getAlertType() {
        return alertType;
    }

    public boolean getIsCancelable() {
        return isCancelable;
    }

    public String getAlrttitle() {
        return alrttitle;
    }

    public String getAlrtMessage() {
        return alrtmessage;
    }

    public boolean getIsFinish() {
        return isFinish;
    }

    // Setter Methods

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public void setIsCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    public void setAlrttitle(String title) {
        this.alrttitle = title;
    }

    public void setAlrtmessage(String message) {
        this.alrtmessage = message;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

//    public  void killActivity() {
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
}


