
package com.naqelexpress.naqelpointer.MLBarcode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.MLBarcode.ui.camera.GraphicOverlay;


public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private int mId;


    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;
    private GraphicOverlay graphicOverlay;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);
        graphicOverlay = overlay;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % overlay.getRectColors().length;
        final int selectedColor = ContextCompat.getColor(overlay.getContext(), overlay.getRectColors()[mCurrentColorIndex]);

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Barcode getBarcode() {
        return mBarcode;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }

        // Draws the bounding box around the barcode.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        if (graphicOverlay.isDrawRect())
            canvas.drawRect(rect, mRectPaint);

        // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
        if (graphicOverlay.isShowText())
            canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint);
    }
}
