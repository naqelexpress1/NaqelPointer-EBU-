package com.naqelexpress.naqelpointer.Activity.NCLAutoSave;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
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
import com.naqelexpress.naqelpointer.Activity.Print.PdfDocumentAdapter;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Ncl;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclWaybillDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.PrintJobMonitorService;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NclShipmentActivity extends AppCompatActivity {

    ScanNclNoFragment firstFragment;
    ScanNclWaybillFragment secondFragment;
    private Bundle bundle;
    DateTime TimeIn;
    public static String NclNo = "0";
    public boolean IsMixed;
    public List<Integer> destList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nclshipment);
        TimeIn = DateTime.now();
        bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.Nclcontainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
            case R.id.mnuSave:
                SaveData();
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
                        ErrorAlert("Contact System Admin For Storage Permission");
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

//            slicetable = new PdfPCell(myTable);
//            slicedata.addCell(slicetable);
//            slicedata.addCell(slicetable);

            //document.add(slicedata);


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

//            slicetable = new PdfPCell(myTable);
//            slicedata.addCell(slicetable);
//            slicedata.addCell(slicetable);
            //document.add(slicedata);

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


//            slicetable = new PdfPCell(myTable);
//            slicedata.addCell(slicetable);
//            slicedata.addCell(slicetable);
            //document.add(slicedata);

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

//            slicetable = new PdfPCell(myTable);
//            slicedata.addCell(slicetable);
//            slicedata.addCell(slicetable);
            //document.add(slicedata);


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

//            slicetable = new PdfPCell(myTable);
//            slicedata.addCell(slicetable);
//            slicedata.addCell(slicetable);
            //document.add(slicedata);

            myCell = new PdfPCell();
            parse = new Phrase("Total Waybill Weight", smallBold);
            myCell.addElement(parse);
            myCell.setBorder(PdfPCell.NO_BORDER);
            myTable.addCell(myCell);

//            double totalweight = 0.0;
//            for (int i = 0; i < secondFragment.PieceCodeList.size(); i++) {
//                if (i == 0)
//                    totalweight = Math.round(secondFragment.PieceCodeList.get(i).Weight * 100.0) / 100.0;
//                else
//                    totalweight = totalweight + Math.round(secondFragment.PieceCodeList.get(i).Weight * 100.0) / 100.0;
//
//            }

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

            //document.add(slicedata);


            // Add all above details into Document
            //document.add(prHead);
            //document.add(myTable);

            // document.add(myTable);

            PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
            Barcode128 barcode = new Barcode128();
            barcode.setCodeType(Barcode128.CODE128);
            barcode.setCode(NclNo);
            Image code128Image = barcode.createImageWithBarcode(pdfContentByte, null, null);
            //code128Image.setAbsolutePosition(10, 10);
            code128Image.scalePercent(200);

            slicetable = new PdfPCell(code128Image);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);
            //document.add(slicedata);

            //document.add(code128Image);


            // Now Start another New Paragraph
            Paragraph prPersinalInfo = new Paragraph();
            prPersinalInfo.setFont(catFont);

//        for (int i = 0; i < ScannedASNOList.size(); i++) {
//            prPersinalInfo.add(String.valueOf(i + 1) + " . " + ScannedASNOList.get(i) + "\n\n");
//
//        }

            //prPersinalInfo.setAlignment(Element.ALIGN_LEFT);

            //document.add(prPersinalInfo);


            Paragraph prProfile = new Paragraph();
            prProfile.setFont(smallBold);
            prProfile.add("\n \n Created by : " + GlobalVar.GV().EmployID);
            prProfile.setFont(normal);
            prProfile
                    .add("\n");

            prProfile.setFont(smallBold);


            // document.add(prProfile);

            slicetable = new PdfPCell(prProfile);
            slicetable.setHorizontalAlignment(Element.ALIGN_LEFT);
            slicetable.setBorder(PdfPCell.NO_BORDER);
            slicedata.addCell(slicetable);
            slicedata.addCell(slicetable);


            document.add(slicedata);


            //i++;
            // if ( i == 1)
            //     addTitlePage(document, pdfWriter);
        }

        // Create new Page in PDF
        //document.newPage();
    }


    public void addMetaData(Document document) {
        document.addTitle("NCL");
        document.addSubject("Naqel Consolidation Label");
        document.addKeywords(DateTime.now().toLocalDate().toString());
        //document.addAuthor("TAG");
        document.addCreator(String.valueOf(GlobalVar.GV().EmployID));
    }

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(NclShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


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
                                    com.naqelexpress.naqelpointer.service.NclService.class));
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
        dbConnections.close();


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
                        secondFragment = new ScanNclWaybillFragment();
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
}
