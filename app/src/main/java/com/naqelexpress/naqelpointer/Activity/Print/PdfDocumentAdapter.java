

package com.naqelexpress.naqelpointer.Activity.Print;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfDocumentAdapter extends ThreadedPrintDocumentAdapter {
  public PdfDocumentAdapter(Context ctxt) {
    super(ctxt);
  }

  @Override
  LayoutJob buildLayoutJob(PrintAttributes oldAttributes,
                           PrintAttributes newAttributes,
                           CancellationSignal cancellationSignal,
                           PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {
    return(new PdfLayoutJob(oldAttributes, newAttributes,
                            cancellationSignal, callback, extras));
  }

  @Override
  WriteJob buildWriteJob(PageRange[] pages,
                         ParcelFileDescriptor destination,
                         CancellationSignal cancellationSignal,
                         PrintDocumentAdapter.WriteResultCallback callback, Context ctxt) {
    return(new PdfWriteJob(pages, destination, cancellationSignal,
                           callback, ctxt));
  }

  private static class PdfLayoutJob extends LayoutJob {
    PdfLayoutJob(PrintAttributes oldAttributes,
                 PrintAttributes newAttributes,
                 CancellationSignal cancellationSignal,
                 PrintDocumentAdapter.LayoutResultCallback callback, Bundle extras) {
      super(oldAttributes, newAttributes, cancellationSignal, callback,
            extras);
    }

    @Override
    public void run() {
      if (cancellationSignal.isCanceled()) {
        callback.onLayoutCancelled();
      }
      else {
        PrintDocumentInfo.Builder builder=
            new PrintDocumentInfo.Builder("CHANGE ME PLEASE");

        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
               .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
               .build();

        callback.onLayoutFinished(builder.build(),
                                  !newAttributes.equals(oldAttributes));
      }
    }
  }

  private static class PdfWriteJob extends WriteJob {
    PdfWriteJob(PageRange[] pages, ParcelFileDescriptor destination,
                CancellationSignal cancellationSignal,
                PrintDocumentAdapter.WriteResultCallback callback, Context ctxt) {
      super(pages, destination, cancellationSignal, callback, ctxt);
    }

    @Override
    public void run() {
      InputStream in=null;
      OutputStream out=null;

      try {
        String path = Environment.getExternalStorageDirectory().getPath() + "/TerminalHandling/Ncl.pdf";
        File file = new File(path);
        if (!file.exists()) {
          file.mkdirs();
        }

        //in=ctxt.getAssets().open("cover.pdf");
        in= new FileInputStream(path);
        out=new FileOutputStream(destination.getFileDescriptor());

        byte[] buf=new byte[16384];
        int size;

        while ((size=in.read(buf)) >= 0
            && !cancellationSignal.isCanceled()) {
          out.write(buf, 0, size);
        }

        if (cancellationSignal.isCanceled()) {
          callback.onWriteCancelled();
        }
        else {
          callback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
        }
      }
      catch (Exception e) {
        callback.onWriteFailed(e.getMessage());
        Log.e(getClass().getSimpleName(), "Exception printing PDF", e);
      }
      finally {
        try {
          in.close();
          out.close();
        }
        catch (IOException e) {
          Log.e(getClass().getSimpleName(),
                "Exception cleaning up from printing PDF", e);
        }
      }
    }
  }
}
