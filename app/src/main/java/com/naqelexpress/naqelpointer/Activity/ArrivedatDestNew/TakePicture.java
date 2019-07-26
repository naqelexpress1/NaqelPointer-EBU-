package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNew;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.naqelexpress.naqelpointer.Activity.ArrivedDest.PalletAdapter;
import com.naqelexpress.naqelpointer.Activity.Incident.Incident;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class TakePicture extends Fragment implements View.OnClickListener {
    View rootView;

    static PalletAdapter adapter;
    private Intent intent;
    public HashMap<Integer, String> sendimages;
    HashMap<Integer, String> tempimages;
    ProgressDialog progressDialog;
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.dmagepieces, container, false);

                image1 = (ImageView) rootView.findViewById(R.id.image1);
                image1.setOnClickListener(this);

                image2 = (ImageView) rootView.findViewById(R.id.image2);
                image2.setOnClickListener(this);

                image3 = (ImageView) rootView.findViewById(R.id.image3);
                image3.setOnClickListener(this);

                image4 = (ImageView) rootView.findViewById(R.id.image4);
                image4.setOnClickListener(this);

                image5 = (ImageView) rootView.findViewById(R.id.image5);
                image5.setOnClickListener(this);

                image6 = (ImageView) rootView.findViewById(R.id.image6);
                image6.setOnClickListener(this);

                image7 = (ImageView) rootView.findViewById(R.id.image7);
                image7.setOnClickListener(this);

                image8 = (ImageView) rootView.findViewById(R.id.image8);
                image8.setOnClickListener(this);

                image9 = (ImageView) rootView.findViewById(R.id.image9);
                image9.setOnClickListener(this);

                image10 = (ImageView) rootView.findViewById(R.id.image10);
                image10.setOnClickListener(this);

                tempimages = new HashMap<>();
                sendimages = new HashMap<>();
            }

            return rootView;
        }
    }

    ArrayList<String> images = new ArrayList<>();
    int onclickposition = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                onclickposition = 0;
                showPopup();

                break;
            case R.id.image2:
                onclickposition = 1;
                showPopup();

                break;
            case R.id.image3:
                onclickposition = 2;
                showPopup();

                break;
            case R.id.image4:
                onclickposition = 3;
                showPopup();

                break;
            case R.id.image5:
                onclickposition = 4;
                showPopup();

                break;
            case R.id.image6:
                onclickposition = 5;
                showPopup();

                break;
            case R.id.image7:
                onclickposition = 6;
                showPopup();

                break;
            case R.id.image8:
                onclickposition = 7;
                showPopup();

                break;
            case R.id.image9:
                onclickposition = 8;
                showPopup();

                break;
            case R.id.image10:
                onclickposition = 9;
                showPopup();

                break;

        }
    }

    protected void CreatefileName() {

        if (filename.length() > 0) {
            tempimages.put(onclickposition, filename);
            callcameraIntent(filename, 0);
        }

    }

    protected void callcameraIntent(String imagename, int camerareqid) {

        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, camerareqid);

        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.ErrorWhileSaving),
                    GlobalVar.AlertType.Error);
            try {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(),
                        null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    EditText txtBarCode;

    private void showPopup() {

        LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.tripplanpopup, viewGroup);
        final PopupWindow popup = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        txtBarCode = (EditText) layout.findViewById(R.id.palletbarcode);
        txtBarCode.setHint("Scan Piece Code");
//        ImageButton imageButton = layout.findViewById(R.id.next);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Long timpstamp = System.currentTimeMillis() / 1000;
//                String id = String.valueOf(GlobalVar.GV().EmployID);
//
//                if (id.length() > 0) {
//                    filename = id + "_" + timpstamp.toString() + "_" + txtBarCode.getText().toString() + ".png";
//                }
//
//                CreatefileName();
//                popup.dismiss();
//            }
//        });

        txtBarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtBarCode != null && txtBarCode.getText().length() == 13) {
                    Long timpstamp = System.currentTimeMillis() / 1000;
                    String id = String.valueOf(GlobalVar.GV().EmployID);

                    if (id.length() > 0) {
                        filename = id + "_" + timpstamp.toString() + "_" + txtBarCode.getText().toString() + ".png";
                    }

                    CreatefileName();
                    popup.dismiss();
                }
            }
        });


        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private class uploadImages extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(getActivity(),
                        "Please wait.", "Uploading Images to Server.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", sendimages.get(0));
                jsonObject.put("FileName", convertimage(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonData = jsonObject.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "upload");
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
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception ignored) {
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {

                        delete = true;
                    }

                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(0));

                    if (sourceFile.exists() && delete) {
                        deletefile(position);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            }


        }
    }

    private String convertimage(int position) {
        String image = "";
        String imagename = GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position);

        File imgfile = new File(imagename);


        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT > 23)
            outputFileUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider",
                    imgfile);
        else
            outputFileUri = Uri.fromFile(imgfile);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputFileUri);
            Bitmap lastBitmap = null;
            lastBitmap = bitmap;
            //encoding image to string
            image = getStringImage(lastBitmap);
            Log.d("image", image);
            //passing the image to volley


        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    String filename;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:

                if (requestCode == 0
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + tempimages.get(onclickposition));

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image);
                            else
                                ci = false;
                        }
                        findimagePosition().setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));

                        if (sendimages.containsKey(onclickposition)) {
                            deletefile(onclickposition);
                            sendimages.remove(onclickposition);
                            sendimages.put(onclickposition, tempimages.get(onclickposition));
                        } else
                            sendimages.put(onclickposition, tempimages.get(onclickposition));

                    } else
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;
        }
    }

    protected ImageView findimagePosition() {
        ImageView imageView = null;
        if (onclickposition == 0)
            imageView = image1;
        else if (onclickposition == 1)
            imageView = image2;
        else if (onclickposition == 2)
            imageView = image3;
        else if (onclickposition == 3)
            imageView = image4;
        else if (onclickposition == 4)
            imageView = image5;
        else if (onclickposition == 5)
            imageView = image6;
        else if (onclickposition == 6)
            imageView = image7;
        else if (onclickposition == 7)
            imageView = image8;
        else if (onclickposition == 8)
            imageView = image9;
        else if (onclickposition == 9)
            imageView = image10;


        return imageView;
    }

    public void compressimage(File imageDir) {
        Bitmap bm = null;
        try {
            Uri outputFileUri = Uri.fromFile(imageDir);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inJustDecodeBounds = true;
            options.inSampleSize = 2;

            bm = BitmapFactory.decodeStream(getActivity().getContentResolver()
                    .openInputStream(outputFileUri), null, options);
            FileOutputStream out = new FileOutputStream(imageDir);
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out);

            bm.recycle();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void deletefile(int position) {
        try {
            File deletefile = new File(GlobalVar.naqelvehicleimagepath + "/"
                    + sendimages.get(position));
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }

}