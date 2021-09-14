package com.naqelexpress.naqelpointer.MLBarcode.MobilevisionBarcode;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.MLBarcode.BarcodeGraphic;

import java.util.List;


public interface BarcodeRetriever {
    void onRetrieved(Barcode barcode);

    void onRetrievedMultiple(Barcode closetToClick, List<BarcodeGraphic> barcode);

    void onBitmapScanned(SparseArray<Barcode> sparseArray);

    void onRetrievedFailed(String reason);

    void onPermissionRequestDenied();
}
