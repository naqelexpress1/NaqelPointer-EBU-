package com.naqelexpress.naqelpointer.Activity.Print;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * Created by Hasna on 10/24/18.
 */

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private ImageAndTextContainer imageAndTextContainer;
    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 4;

    public MyPrintDocumentAdapter(Context context, ImageAndTextContainer container) {
        this.context = context;
        imageAndTextContainer = container;

    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         PrintDocumentAdapter.LayoutResultCallback callback,
                         Bundle metadata) {
        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }


//    private int computePageCount(PrintAttributes printAttributes) {
//        int itemsPerPage = 4; // default item count for portrait mode
//
//        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
//        if (!pageSize.isPortrait()) {
//            // Six items per page in landscape orientation
//            itemsPerPage = 6;
//        }
//
//        // Determine number of print items
//        int printItemCount = getPrintItemCount();
//
//        return (int) Math.ceil(printItemCount / itemsPerPage);
//    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {
        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pageRanges, i)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);


                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                drawPage(page, i);
                myPdfDocument.finishPage(page);
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page,
                          int pagenumber) {
        Canvas canvas = page.getCanvas();

        pagenumber++; // Make sure page numbers start at 1

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(
                "Test Print Document Page " + pagenumber,
                leftMargin,
                titleBaseLine,
                paint);

        //drawText(imageAndTextContainer.getText(), canvas, contentRect);
        Rect contentRect = new Rect(10, 10, canvas.getWidth() - 10, canvas.getHeight() - 10);
        drawText(imageAndTextContainer.getText(), canvas, contentRect);
        paint.setTextSize(14);


//        canvas.drawText(imageAndTextContainer.getText(), leftMargin, titleBaseLine + 35, paint);
//
//        if (pagenumber % 2 == 0)
//            paint.setColor(Color.RED);
//        else
//            paint.setColor(Color.GREEN);
//
//        PdfDocument.PageInfo pageInfo = page.getInfo();


        //drawText(imageAndTextContainer.getText(), canvas, contentRect);

//        canvas.drawCircle(pageInfo.getPageWidth() / 2,
//                pageInfo.getPageHeight() / 2,
//                150,
//                paint);
    }

    private void drawText(String text, Canvas canvas, Rect rect) {

        Document document = new Document();
        Paragraph paragraph = new Paragraph(text);
        try {
            document.add(paragraph);

            TextPaint paint = new TextPaint();
            paint.setColor(Color.BLACK);

            StaticLayout sl = new StaticLayout(text, paint, (int) rect.width(), Layout.Alignment.ALIGN_CENTER, 1, 1, false);

            canvas.save();
            canvas.translate(rect.left, rect.top);
            sl.draw(canvas);
            canvas.restore();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
}
