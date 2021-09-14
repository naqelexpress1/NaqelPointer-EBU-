package com.naqelexpress.naqelpointer.Activity.GoogleBarcode;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;

//import com.google.android.gms.vision.Detector;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.barcode.Barcode;



public class CustomBarCodeDetector extends Detector<Barcode> {

    public SparseArray<Barcode> detect(Frame frame) {
        return null;
    }

    public boolean isOperational() {
        return super.isOperational();
    }
}
