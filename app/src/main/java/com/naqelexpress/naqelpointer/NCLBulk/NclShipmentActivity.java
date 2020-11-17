package com.naqelexpress.naqelpointer.NCLBulk;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.naqelexpress.naqelpointer.Activity.OFDPieceLevel.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.Print.PdfDocumentAdapter;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Ncl;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclWaybillDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.OnlineValidation.AsyncTaskCompleteListener;
import com.naqelexpress.naqelpointer.OnlineValidation.OnlineValidationAsyncTask;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.NclService;
import com.naqelexpress.naqelpointer.service.NclServiceBulk;
import com.naqelexpress.naqelpointer.service.PrintJobMonitorService;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Error.ErrorReporter;

public class NclShipmentActivity extends AppCompatActivity implements INclShipmentActivity , AsyncTaskCompleteListener {

    ScanNclNoFragment firstFragment;
    ScanNclWaybillFragmentRemoveValidation_CITC secondFragment;

    private Bundle bundle;
    private DateTime TimeIn;
    public static String NclNo = "0";
    public boolean IsMixed;
    public List<Integer> destList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());


        setContentView(R.layout.nclshipment);
        TimeIn = DateTime.now();
        bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!isValidOnlineValidationFile()) {
            Log.d("test" , "File is NOT valid");
            OnlineValidationAsyncTask onlineValidationAsyncTask = new OnlineValidationAsyncTask(getApplicationContext() , NclShipmentActivity.this , this);
            onlineValidationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , String.valueOf(GlobalVar.NclAndArrival));
        } else {
            Log.d("test" , "File is valid");
        }



        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.Nclcontainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        NclNo = "0";
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1: {
                        secondFragment.lbNclNo.setText("NCL NO : " + NclNo);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.finish:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are you sure want to Finish the Job?", 0);
                } else
                    GlobalVar.RedirectSettings(NclShipmentActivity.this);
                return true;
            case R.id.manual:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    UploadManul("Info", "Are you sure want to upload Manual?");
                } else
                    GlobalVar.RedirectSettings(NclShipmentActivity.this);
                return true;

            case R.id.print:
                if (IsValid())
                    askPermission();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void alertPermission() {
        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(NclShipmentActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage("External storage permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale
                        (NclShipmentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    try {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                    } catch (Exception e) {
                        ErrorAlert("Info", "Contact System Admin For Storage Permission");
                    }
                } else {
                    ActivityCompat.requestPermissions(NclShipmentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        android.app.AlertDialog alert = alertBuilder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            alertPermission();


        } else {
            printDocumnent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    printDocumnent();
                } else {
                    alertPermission();
                }
                break;
        }

    }


    private void printDocumnent() {

        final ProgressDialog progresRing = ProgressDialog.show(NclShipmentActivity.this,
                "Info", "please Wait connecting to printer...", true);
        progresRing.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                String path = Environment.getExternalStorageDirectory().getPath() + "/TerminalHandling";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (file.exists()) {
                    File pathfile = new File(path, "Ncl.pdf");
                    Document document = new Document(); //PageSize.A4
                    document.setPageSize(PageSize.A4.rotate());
                    try {
                        //PdfWriter.getInstance(document, new FileOutputStream(pathfile));

                        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pathfile));

                        // Open Document for Writting into document
                        document.open();

                        // User Define Method
                        addMetaData(document);
                        addTitlePage(document, pdfWriter);

                        print("Ncl PDF",
                                new PdfDocumentAdapter(getApplicationContext()),
                                new PrintAttributes.Builder().build());

                    } catch (FileNotFoundException e) {
                        if (progresRing != null && progresRing.isShowing())
                            progresRing.dismiss();
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        if (progresRing != null && progresRing.isShowing())
                            progresRing.dismiss();
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Close Document after writting all content
                    if (progresRing != null && progresRing.isShowing())
                        progresRing.dismiss();
                    document.close();


                }
            }
        }).start();
    }

    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        PrintManager mgr = (PrintManager) getSystemService(PRINT_SERVICE);
        startService(new Intent(this, PrintJobMonitorService.class));

        return (mgr.print(name, adapter, attrs));
    }

    int i = 0;

    public void addTitlePage(Document document, PdfWriter pdfWriter) throws DocumentException {

        if (i == 0 || i == 1) {
            // Font Style for Document
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD);
//        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD
//                | Font.UNDERLINE, BaseColor.BLUE);
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD, BaseColor.BLUE);

            Font subtitile = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD, BaseColor.BLUE);


            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

            SimpleDateFormat fmt =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            PdfPTable slicedata = new PdfPTable(2);
            slicedata.setWidthPercentage(100.0f);
            slicedata.setHorizontalAlignment(Element.ALIGN_LEFT);

            // load image
            try {
                // get input stream
                InputStream ims = getAssets().open("naqellogo.jpeg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 500) / image.getWidth()) * 100;

                image.scalePercent(scaler);

                PdfPCell slicetable = new PdfPCell(image);
                slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
                slicetable.setBorder(PdfPCell.NO_BORDER);
                slicedata.addCell(slicetable);
                slicedata.addCell(slicetable);
                // document.add(image);
            } catch (IOException ex) {
                System.out.println(ex.toString());
                //return;
            }

            //   Start New Paragraph
            Paragraph prHead = new Paragraph();
            prHead.setAlignment(Element.ALIGN_LEFT);
            // Set Font in this Paragraph
            prHead.setFont(titleFont);
            // Add item into Paragraph
            prHead.add("NCL");


            // Start New Paragraph
            Paragraph prHead1 = new Paragraph();
            prHead1.setAlignment(Element.ALIGN_LEFT);
            // Set Font in this Paragraph
            prHead1.setFont(catFont);
            // Add item into Paragraph
            prHead1.add("");

            PdfPCell slicetable = new PdfPCell(prHead);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);

            //document.add(slicedata);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Start New Paragraph
            Paragraph header1 = new Paragraph();
            // Set Font in this Paragraph
            header1.setFont(catFont);
            header1.setAlignment(Element.ALIGN_LEFT);
            // Add item into Paragraph
            header1.add("Naqel Consolidation Label");

            // Add all above details into Document
            slicetable = new PdfPCell(header1);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);

            //document.add(slicedata);
            header1.add(Chunk.NEWLINE);
            header1.add(Chunk.NEWLINE);

            // document.add(header1);

            // Create Table into Document with 1 Row
            PdfPTable myTable = new PdfPTable(2);
            myTable.setSpacingAfter(50);

            // 100.0f mean width of table is same as Document size
            myTable.setWidthPercentage(50.0f);
            myTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            // Create New Cell into Table

            PdfPCell myCell = new PdfPCell();
            Phrase parse = new Phrase("Date", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

            myCell = new PdfPCell();
            parse = new Phrase(fmt.format(new Date()), smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);


            myCell = new PdfPCell();
            parse = new Phrase("Orgin", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

            myCell = new PdfPCell();
            parse = new Phrase(firstFragment.txtOrgin.getText().toString(), smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);


            String mix = "";
            if (IsMixed) {
                mix = " ( MIX ) ";
            }

            myCell = new PdfPCell();
            parse = new Phrase("Destinaton", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

            myCell = new PdfPCell();
            parse = new Phrase(firstFragment.txtDestination.getText().toString() + " " + mix, smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);


            myCell = new PdfPCell();
            parse = new Phrase("Total Waybills", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

            myCell = new PdfPCell();
            parse = new Phrase(String.valueOf(secondFragment.WaybillList.size()), smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);


            myCell = new PdfPCell();
            parse = new Phrase("Total Pieces", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

            myCell = new PdfPCell();
            parse = new Phrase(String.valueOf(secondFragment.PieceCodeList.size()), smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);


            myCell = new PdfPCell();
            parse = new Phrase("Total Waybill Weight", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);


            myCell = new PdfPCell();
            parse = new Phrase(String.valueOf(Math.round(ScanNclWaybillFragment.waybillweight * 100.0) / 100.0), smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            // Add Cell into Table
            myTable.addCell(myCell);

            slicetable = new PdfPCell(myTable);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);


            PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
            Barcode128 barcode = new Barcode128();
            barcode.setCodeType(Barcode128.CODE128);
            barcode.setCode(NclNo);
            Image code128Image = barcode.createImageWithBarcode(pdfContentByte, null, null);
            code128Image.scalePercent(200);

            slicetable = new PdfPCell(code128Image);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);


            // Now Start another New Paragraph
            Paragraph prPersinalInfo = new Paragraph();
            prPersinalInfo.setFont(catFont);

            Paragraph prProfile = new Paragraph();
            prProfile.setFont(smallBold);
            prProfile.add("\n \n Created by : " + GlobalVar.GV().EmployID);
            prProfile.setFont(normal);
            prProfile
                    .add("\n");

            prProfile.setFont(smallBold);


            slicetable = new PdfPCell(prProfile);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);

            document.add(slicedata);

        }
    }


    public void addMetaData(Document document) {
        document.addTitle("NCL");
        document.addSubject("Naqel Consolidation Label");
        document.addKeywords(DateTime.now().toLocalDate().toString());
        //document.addAuthor("TAG");
        document.addCreator(String.valueOf(GlobalVar.GV().EmployID));
    }

    private void ErrorAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(NclShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void SavedSucessfully(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(NclShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.show();
    }


    private void ErrorAlert(final String title, String message, int dummy) {
        AlertDialog alertDialog = new AlertDialog.Builder(NclShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit without Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StartService();
                        dialog.dismiss();

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit with Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDataCompleteJob(1);
                        dialog.dismiss();

                    }
                });

        alertDialog.show();
    }


    private void UploadManul(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(NclShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        //Save locally and call api
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save & Upload",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDataCompleteJob(2);
                        dialog.dismiss();

                    }
                });

        //Call api within activity
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Without Save & Upload",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NCLbyManual();
                        dialog.dismiss();

                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void SaveDataCompleteJob(int close) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {

            requestLocation();


            DateTime TimeIn = DateTime.now();
            ArrayList<String> waybill = new ArrayList<String>();

            Ncl ncl = new Ncl();
            ncl.NclNo = NclShipmentActivity.NclNo;
            ncl.Date = TimeIn;
            ncl.UserID = GlobalVar.GV().UserID;
            ncl.PieceCount = ScanNclWaybillFragmentRemoveValidation_CITC.PieceCodeList.size();

            ncl.EmployID = GlobalVar.GV().EmployID;
            ncl.StationID = GlobalVar.GV().StationID;
            ncl.AppVersion = GlobalVar.GV().AppVersion;
            ncl.Latitude = String.valueOf(Latitude);
            ncl.Longitude = String.valueOf(Longitude) ;

            Log.d("test" , "Piece code List size " + ScanNclWaybillFragmentRemoveValidation_CITC.PieceCodeList.size());


            String Origin[] = ScanNclNoFragment.txtOrgin.getText().toString().split(":");
            String Dest[] = ScanNclNoFragment.txtDestination.getText().toString().split(":");
            if (Origin.length > 1)
                ncl.OrgDest = Origin[0];
            if (Dest.length > 1)
                ncl.OrgDest = ncl.OrgDest + " / " + Dest[0];

            ncl.IsSync = false;

            for (int i = 0; i < ScanNclWaybillFragmentRemoveValidation_CITC.PieceCodeList.size(); i++) {
                ScanNclWaybillFragmentRemoveValidation_CITC.PieceDetail pieceDetail =  ScanNclWaybillFragmentRemoveValidation_CITC.PieceCodeList.get(i);
                ncl.ncldetails.add(i,
                        new NclDetail(pieceDetail.Barcode, 0 ,pieceDetail.IsDestChanged , pieceDetail.DestinationStationID ));
//                ncl.ncldetails.add(i,
//                        new NclDetail(ScanNclWaybillFragmentRemoveValidation_CITC.PieceCodeList.get(i).Barcode, 0));
            }
            ncl.WaybillCount = waybill.size();

            String jsonData = JsonSerializerDeserializer.serialize(ncl, true);
            jsonData = jsonData.replace("Date(-", "Date(");

            boolean issave = dbConnections.InsertNclBulk(jsonData, getApplicationContext(), ncl.PieceCount);
            if (issave) {
                if (!isMyServiceRunning(NclServiceBulk.class) && close != 2) {
                    startService(
                            new Intent(this, NclServiceBulk.class));

                }
                if (close == 1)
                    finish();
                else if (close == 2)
                    NCLbyManual();

                ScanNclWaybillFragment.PieceCodeList.clear();
                ScanNclWaybillFragment.WaybillList.clear();
            }

        }
        dbConnections.close();


    }

    private void StartService() {
        DBConnections db = new DBConnections(getApplicationContext(), null);
        Cursor result = db.Fill("select * from Ncl where IsSync = 0 Limit 1 ", getApplicationContext());
        if (result.getCount() > 0) {
            if (!isMyServiceRunning(NclService.class)) {
                startService(
                        new Intent(this, NclService.class));

            }
        }
        finish();
        db.close();
        result.close();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private boolean IsValid() {
        boolean isValid = true;
        if (NclNo == "0") {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please generate Ncl No", GlobalVar.AlertType.Error);
            isValid = false;
        }
        if (secondFragment != null && secondFragment.PieceCodeList.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }
        return isValid;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new ScanNclNoFragment();
                        firstFragment.setArguments(bundle);
                        //return firstFragment;
                    }
                    return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new ScanNclWaybillFragmentRemoveValidation_CITC();
                        secondFragment.setArguments(bundle);
                    }
                    return secondFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.ncl_No);
                case 1:
                    return getResources().getString(R.string.ncl_Piece);
            }
            return null;
        }
    }

    ProgressDialog progressDialog;

    private class NCLBulkbyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(NclShipmentActivity.this);
                progressDialog.setTitle("Request is being process,please wait...");
                progressDialog.setMessage("Remaining " + String.valueOf(totalsize) + " / " + String.valueOf(totalsize));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(1);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setMessage("Remaining  " + String.valueOf(totalsize - moveddata) + " / " + String.valueOf(totalsize));
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            totalsize = Integer.parseInt(params[0]);
            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor result = db.Fill("select * from Ncl where IsSync = 0 order by ID Limit 20", getApplicationContext());
            if (result.getCount() == 0) {
                result.close();
                return null;
            }
            result.moveToFirst();
            int id = 0, piececount = 0;

            do {
                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                String jsonData = "";
                // if (result.moveToFirst()) {
                id = result.getInt(result.getColumnIndex("ID"));
                piececount = result.getInt(result.getColumnIndex("PieceCount"));
                jsonData = result.getString(result.getColumnIndex("JsonData"));
                // }

                jsonData = jsonData.replace("Date(-", "Date(");


                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "NclSubmitInsertWaybillManual"); //LoadtoDestination
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                    dos = httpURLConnection.getOutputStream();
                    httpURLConnection.getOutputStream();
                    dos.write(jsonData.getBytes());

                    ist = httpURLConnection.getInputStream();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ist));

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    returnresult = String.valueOf(buffer);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ist != null)
                            ist.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (dos != null)
                            dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    returnresult = String.valueOf(buffer);
                }


                if (returnresult.contains("Created")) {

                    boolean isupdate = db.updateNCL(id, getApplicationContext());
                    if (isupdate)
                        moveddata = moveddata + piececount;
                    //db.deleteNcl(id, getApplicationContext());
                    //db.deleteNclWayBill(ncl.ID, getApplicationContext());
                    //db.deleteNclBarcode(id, getApplicationContext());
                }
                try {
                    uploaddatacount = uploaddatacount + piececount;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (result.moveToNext());

            result.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                DBConnections db = new DBConnections(getApplicationContext(), null);
                Cursor ts = db.Fill("select SUM(PieceCount) As totalRecord  from Ncl where IsSync = 0 ", getApplicationContext());
                ts.moveToFirst();
                //int totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
                int tls = 0;
                try {
                    tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    tls = 0;
                }

                if (tls > 0) {
                    ErrorAlert("Something went wrong",
                            "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again"
                    );
                    startService(
                            new Intent(NclShipmentActivity.this,
                                    NclServiceBulk.class));
                } else {

                    SavedSucessfully("No Data",
                            "All Data Synchronized Successfully");
                }
                ts.close();
                db.close();

                super.onPostExecute(String.valueOf(finalJson));


            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    }

    int totalsize = 0, uploaddatacount = 0;

    private void NCLbyManual() {

        totalsize = 0;
        stopService(
                new Intent(NclShipmentActivity.this,
                        NclServiceBulk.class));

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor ts = db.Fill("select SUM(PieceCount) As totalRecord  from Ncl where IsSync = 0 ", getApplicationContext());
            ts.moveToFirst();
            try {
                totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
            } catch (Exception e) {
                totalsize = 0;
            }
            ts.close();

            if (totalsize > 0) {
                new NCLBulkbyManual().execute(String.valueOf(totalsize));
            } else {
                ErrorAlert("No Data",
                        "All Data Synchronized Successfully"
                );
            }
            db.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private boolean isValidOnlineValidationFile() {
        boolean isValid;
        try {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            isValid = dbConnections.isValidOnlineValidationFile(GlobalVar.NclAndArrival , getApplicationContext());
            if (isValid)
                return true;
        } catch (Exception ex) {
            Log.d("test" , "NCLShipment Activity - isValidOnlineValidationFile() " + ex.toString());
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit NCL ")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        NclShipmentActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onNCLGenerated(String NCLNo , int NCLDestStationID , List<Integer> allowedDestStations) {
      /*  try {
            secondFragment.onNCLGenerated(NCLNo , NCLDestStationID , allowedDestStations);
        } catch (Exception ex) {} */
    }

    @Override
    public void onTaskComplete(boolean hasError, String errorMessage) {
      try {
          if (hasError)
              ErrorAlert("Failed Loading File" , "Kindly contact your supervisor \n \n " + errorMessage);
          else
              GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
      } catch (Exception ex) {}
    }


    public double Latitude = 0;
    public double Longitude = 0;

    private void requestLocation() {
        try {
            Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
            if (location != null) {
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
            }
        } catch (Exception ex) {
            Log.d("test" , "NCLShipmentActivity - requestLocation() " + ex.toString());
        }
    }


    /*
    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;


            Ncl ncl = new Ncl(secondFragment.WaybillList.size(), secondFragment.PieceCodeList.size(), TimeIn, this.NclNo);

            if (dbConnections.InsertNcl(ncl, getApplicationContext())) {
                int nclId = dbConnections.getMaxID("Ncl", getApplicationContext());
                for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
                    NclWaybillDetail nclWaybillDetail =
                            new NclWaybillDetail(secondFragment.WaybillList.get(i), nclId);
                    if (!dbConnections.InsertNclWaybillDetail(nclWaybillDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < secondFragment.PieceCodeList.size(); i++) {
                    NclDetail nclDetail = new NclDetail(secondFragment.PieceCodeList.get(i).Barcode,
                            nclId);
                    if (!dbConnections.InsertNclDetail(nclDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {
                    startService(
                            new Intent(NclShipmentActivity.this,
                                    NclService.class));
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
        dbConnections.close();


    }

    private class NCLbyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(NclShipmentActivity.this);
                progressDialog.setTitle("Request is being process,please wait...");
                progressDialog.setMessage("Remaining " + String.valueOf(totalsize) + " / " + String.valueOf(totalsize));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(1);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setMessage("Remaining  " + String.valueOf(totalsize - moveddata) + " / " + String.valueOf(totalsize));
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            totalsize = Integer.parseInt(params[0]);
            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor result = db.Fill("select * from Ncl where IsSync = 0 order by ID", getApplicationContext());
            result.moveToFirst();
            do {
                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                Ncl ncl = new Ncl();
                ncl.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                ncl.NclNo = result.getString(result.getColumnIndex("NclNo"));
                ncl.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                ncl.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
                ncl.PieceCount = Integer.parseInt(result.getString(result.getColumnIndex("PieceCount")));
                ncl.WaybillCount = Integer.parseInt(result.getString(result.getColumnIndex("WaybillCount")));
                ncl.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));

                Cursor resultDetail = db.Fill("select * from NclWaybillDetail where NclID = "
                        + ncl.ID, getApplicationContext());

                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        ncl.nclwaybilldetails.add(index,
                                new NclWaybillDetail(
                                        resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")), ncl.ID));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }

                resultDetail = db.Fill("select * from NclDetail where NclID = " + ncl.ID, getApplicationContext());

                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        ncl.ncldetails.add(index,
                                new NclDetail(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), ncl.ID));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }

                String jsonData = JsonSerializerDeserializer.serialize(ncl, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "NclSubmit"); //LoadtoDestination
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                    dos = httpURLConnection.getOutputStream();
                    httpURLConnection.getOutputStream();
                    dos.write(jsonData.getBytes());

                    ist = httpURLConnection.getInputStream();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ist));

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    returnresult = String.valueOf(buffer);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ist != null)
                            ist.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (dos != null)
                            dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    returnresult = String.valueOf(buffer);
                }


                if (returnresult.contains("Created")) {
                    moveddata = moveddata + ncl.PieceCount;
                    db.deleteNcl(ncl.ID, getApplicationContext());
                    //db.deleteNclWayBill(ncl.ID, getApplicationContext());
                    db.deleteNclBarcode(ncl.ID, getApplicationContext());
                }
                try {
                    uploaddatacount = uploaddatacount + ncl.PieceCount;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (result.moveToNext());

            result.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                DBConnections db = new DBConnections(getApplicationContext(), null);
                Cursor ts = db.Fill("select SUM(PieceCount) As totalRecord  from Ncl", getApplicationContext());
                ts.moveToFirst();
                //int totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
                int tls = 0;
                try {
                    tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    tls = 0;
                }

                if (tls > 0) {
                    ErrorAlert("Something went wrong",
                            "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again"
                    );
                    startService(
                            new Intent(NclShipmentActivity.this,
                                    NclService.class));
                } else {

                    SavedSucessfully("No Data",
                            "All Data Synchronized Successfully");
                }
                ts.close();
                db.close();

                super.onPostExecute(String.valueOf(finalJson));


            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    } */

}
