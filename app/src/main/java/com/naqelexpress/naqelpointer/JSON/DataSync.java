package com.naqelexpress.naqelpointer.JSON;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.GetDeliveryStatusRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataSync {
    public DataSync() {
    }

    //-------------Sending UserME Login Data to the Server ----------------
//    public void SendUserMeLoginsData(Context context,View view) {
//        DBConnections dbConnections = new DBConnections(context,null);
//        Cursor result = dbConnections.Fill("select * from UserMeLogin where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(view, GlobalVar.GV().context.getString(R.string.SendingUserLoginData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
//                int stateID = Integer.parseInt(result.getString(result.getColumnIndex("StateID")));
//                DateTime dateTime = DateTime.parse(result.getString(result.getColumnIndex("Date")));
//                int TruckID = Integer.parseInt(result.getString(result.getColumnIndex("TruckID")));
//                UserMELoginRequest userMELoginRequest = new UserMELoginRequest(ID, EmployID, stateID, TruckID, dateTime);
//
//                String jsonData = JsonSerializerDeserializer.serialize(userMELoginRequest, true);
//                jsonData = jsonData.replace("Date(-", "Date(");
//
//                String x = new SendUserMeLoginsDataToServer().doInBackground(jsonData);
//                new SendUserMeLoginsDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }

//    private class SendUserMeLoginsDataToServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing User Logins Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendUserMeLoginsDataToServer");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                //httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            if (finalJson != null) {
//                SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
//                if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                    if (GlobalVar.GV().dbConnections != null) {
//                        UserMeLogin instance = new UserMeLogin(sendingDataResult.ID);
//                        GlobalVar.GV().dbConnections.UpdateUserMeLogin(instance);
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
//                    }
//                } else if (sendingDataResult.HasError)
//                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Data Not Sync Because :" + sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//            } else
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "User login Data Not Sync Because : Something went wrong,kindly check your internet connection", GlobalVar.AlertType.Error);
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //-------------Sending OnDelivery Data to the Server -------------------
//    public void SendOnDliveryData() {
////        if (!GlobalVar.GV().HasInternetAccess)
////            return;
////
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from OnDelivery where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingDeliveryData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                OnDeliveryRequest onDeliveryRequest = new OnDeliveryRequest();
//                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                onDeliveryRequest.WaybillNo = result.getString(result.getColumnIndex("WaybillNo"));
//                onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
//
//                onDeliveryRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
//                onDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
//                onDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
//                onDeliveryRequest.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
//                onDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//                onDeliveryRequest.IsPartial = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsPartial")));
//                onDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
//                onDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("TotalReceivedAmount")));
//                //onDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
//                //onDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
//                onDeliveryRequest.POSAmount = Double.parseDouble(result.getString(result.getColumnIndex("POSAmount")));
//                onDeliveryRequest.CashAmount = Double.parseDouble(result.getString(result.getColumnIndex("CashAmount")));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from OnDeliveryDetail where DeliveryID = " + onDeliveryRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        onDeliveryRequest.OnDeliveryDetailRequestList.add(index, new OnDeliveryDetailRequest(resultDetail.getString(resultDetail.getColumnIndex("BarCode"))));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
//                System.out.println(jsonData);
//
//                jsonData = jsonData.replace("Date(-", "Date(");
//                new SendOnDeliveryDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }

//    private class SendOnDeliveryDataToServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing Delivery Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendOnDeliveryDataToServer");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception ignored) {
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            if (finalJson != null) {
//                SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
//                if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                    if (GlobalVar.GV().dbConnections != null) {
//                        OnDelivery instance = new OnDelivery(sendingDataResult.ID);
//                        GlobalVar.GV().dbConnections.UpdateOnDelivery(instance);
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
//                    }
//                } else if (sendingDataResult.HasError)
//                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Data Not Sync Because :" + sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//            } else
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Deliver data Not Sync Because : Something went wrong,kindly check your internet connection", GlobalVar.AlertType.Error);
//
//
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

//    //-------------Sending CheckPoint Data to the Server -------------------
//    public void SendCheckPointData()
//    {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from CheckPoint where IsSync = 0");
//        if(result.getCount() > 0 )
//        {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage,GlobalVar.GV().context.getString(R.string.SendingCheckPointData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do
//            {
//                SendCheckPointRequest sendCheckPointRequest = new SendCheckPointRequest();
//                sendCheckPointRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                sendCheckPointRequest.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
//                sendCheckPointRequest.CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
//                sendCheckPointRequest.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
//                sendCheckPointRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                sendCheckPointRequest.Longitude = result.getString(result.getColumnIndex("Latitude"));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from CheckPointWaybillDetails where CheckPointID = " + sendCheckPointRequest.ID);
//
//                if(resultDetail.getCount() > 0 )
//                {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do
//                    {
//                        sendCheckPointRequest.CheckPointWaybillDetailsRequestList.add(index,new CheckPointWaybillDetailRequest(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo"))));
//                        index ++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(sendCheckPointRequest, true);
//                System.out.println(jsonData);
//
//                jsonData = jsonData.replace("Date(-", "Date(");
//                new SendCheckPointDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }
//
//    private class SendCheckPointDataToServer extends AsyncTask<String,Void,String>
//    {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute()
//        {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing Check Point Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params)
//        {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try
//            {
//                URL url = new URL( GlobalVar.GV().NaqelPointerAPILink + "SendCheckPoint");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while((line = reader.readLine())!= null)
//                {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            }
//            catch (Exception ignored){}
//            finally
//            {
//                try
//                {
//                    if (ist != null)
//                        ist.close();
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//                try
//                {
//                    if (dos != null)
//                        dos.close();
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection!=null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson)
//        {
//            progressDialog.dismiss();
////            SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
////            if (sendingDataResult.IsSync && !sendingDataResult.HasError)
////            {
////                if (GlobalVar.GV().dbConnections != null)
////                {
////                    OnDelivery instance = new OnDelivery(sendingDataResult.ID);
////                    GlobalVar.GV().dbConnections.UpdateOnDelivery(instance);
////                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage,GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
////                }
////            }
////            else
////            if (sendingDataResult.HasError)
////                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage,"Data Not Sync Because :" + sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //-------------Sending NotDelivered Data to the Server -------------------
//    public void SendNotDliveryData() {
//
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from NotDelivered where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingNotDeliveredData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                final NotDelivered notDeliveredRequest = new NotDelivered();
//                notDeliveredRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                notDeliveredRequest.WaybillNo = String.valueOf(result.getString(result.getColumnIndex("WaybillNo")));
//                notDeliveredRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
//                notDeliveredRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
//                notDeliveredRequest.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
//                notDeliveredRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//                notDeliveredRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
//                notDeliveredRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                notDeliveredRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
//                notDeliveredRequest.DeliveryStatusID = Integer.parseInt(result.getString(result.getColumnIndex("DeliveryStatusID")));
//                notDeliveredRequest.Notes = result.getString(result.getColumnIndex("Notes"));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from NotDeliveredDetail where NotDeliveredID = " + notDeliveredRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        notDeliveredRequest.NotDeliveredDetails.add(index, new NotDeliveredDetail(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), notDeliveredRequest.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//
//                String jsonData = JsonSerializerDeserializer.serialize(notDeliveredRequest, true);
//                ProjectAsyncTask task = new ProjectAsyncTask("SendNotDeliveredDataToServer", "Post", jsonData);
//                task.setUpdateListener(new OnUpdateListener() {
//                    ProgressDialog progressDialog;
//
//                    public void onPostExecuteUpdate(String obj) {
//                        progressDialog.dismiss();
//
//                        SendingDataResult sendingDataResult = new SendingDataResult(obj);
//                        if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                            if (GlobalVar.GV().dbConnections != null) {
//                                NotDelivered instance = new NotDelivered(sendingDataResult.ID);
//
//                                GlobalVar.GV().dbConnections.UpdateNotDelivered(instance);
//                                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
//                            }
//                        } else
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Error);
//
//                     /*   if (obj.contains("Created"))
//                        {
//                            notDeliveredRequest.IsSync = true;
//                            for (int i =0;i<notDeliveredRequest.NotDeliveredDetails.size();i++)
//                            {
//                                notDeliveredRequest.NotDeliveredDetails.get(i).IsSync = true;
//                            }
//
//                            GlobalVar.GV().dbConnections.UpdateNotDelivered(notDeliveredRequest);
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Info);
//                        }
//                        else
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Error);
//                            */
//                    }
//
//                    public void onPreExecuteUpdate() {
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Sending Not Delivered Data", GlobalVar.AlertType.Info);
//                        progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing Not Delivered Details.", true);
//                    }
//                });
//                task.execute();
//
////
//////                jsonData = jsonData.replace("Date(-", "Date(");
////                new SendNotDeliveredDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }

//    private class SendNotDeliveredDataToServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing Not Delivered Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendNotDeliveredDataToServer");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception ignored) {
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
//            if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                if (GlobalVar.GV().dbConnections != null) {
//                    NotDelivered instance = new NotDelivered(sendingDataResult.ID);
//                    GlobalVar.GV().dbConnections.UpdateNotDelivered(instance);
//                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
//                }
//            } else if (sendingDataResult.HasError)
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //-------------Sending OnCloading For DeliverySheet Data to the Server -------------------
//    public void SendOnCloadingForDData() {
////        if (!GlobalVar.GV().HasInternetAccess)
////            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from OnCloadingForD where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingOnCloadingForDData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                OnCLoadingForDeliverySheetRequest onCLoadingForDeliverySheetRequest = new OnCLoadingForDeliverySheetRequest();
//                onCLoadingForDeliverySheetRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                onCLoadingForDeliverySheetRequest.CourierID = Integer.parseInt(result.getString(result.getColumnIndex("CourierID")));
//                onCLoadingForDeliverySheetRequest.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
//                onCLoadingForDeliverySheetRequest.CTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
//                onCLoadingForDeliverySheetRequest.PieceCount = Integer.parseInt(result.getString(result.getColumnIndex("PieceCount")));
//                onCLoadingForDeliverySheetRequest.TruckID = result.getString(result.getColumnIndex("TruckID"));
//                onCLoadingForDeliverySheetRequest.WaybillCount = Integer.parseInt(result.getString(result.getColumnIndex("WaybillCount")));
//                onCLoadingForDeliverySheetRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from OnCLoadingForDDetail where OnCLoadingForDID = " + onCLoadingForDeliverySheetRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        onCLoadingForDeliverySheetRequest.OnCLoadingForDeliverySheetPieceList.add(index, new OnCLoadingForDeliverySheetPiece(resultDetail.getString(resultDetail.getColumnIndex("BarCode"))));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                resultDetail = GlobalVar.GV().dbConnections.Fill("select * from OnCLoadingForDWaybill where OnCLoadingID = " + onCLoadingForDeliverySheetRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        onCLoadingForDeliverySheetRequest.OnCLoadingForDeliverySheetWaybillList.add(index, new OnCLoadingForDeliverySheetWaybill(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo"))));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(onCLoadingForDeliverySheetRequest, true);
//                jsonData = jsonData.replace("Date(-", "Date(");
//                new SendOnCloadingForDDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }

//    private class SendOnCloadingForDDataToServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing On Courier Loading Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendOnCLoadingForDeliverySheet");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception ignored) {
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            if (finalJson != null) {
//                SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
//                if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                    if (GlobalVar.GV().dbConnections != null) {
//                        OnCloadingForD instance = new OnCloadingForD(sendingDataResult.ID);
//                        GlobalVar.GV().dbConnections.UpdateOnCloadingForD(instance);
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SyncSuccessfully), GlobalVar.AlertType.Info);
//                    }
//                } else if (sendingDataResult.HasError)
//                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//            } else
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Your delviry sheet not sync Because : Something went wrong,kindly check your internet connection", GlobalVar.AlertType.Error);
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //-------------Sending Multi Delivery Data to the Server -------------------
//    public void SendMultiDeliveryData() {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from MultiDelivery where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingMultiDeliveryData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                final MultiDelivery multiDeliveryRequest = new MultiDelivery();
//                multiDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                multiDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
//                multiDeliveryRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
//                multiDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
//                multiDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
//                multiDeliveryRequest.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
//                multiDeliveryRequest.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
//                multiDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//                multiDeliveryRequest.WaybillsCount = Integer.parseInt(result.getString(result.getColumnIndex("WaybillsCount")));
//                multiDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                multiDeliveryRequest.Longitude = result.getString(result.getColumnIndex("StationID"));
//                multiDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("ReceivedAmt")));
//                multiDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
//                multiDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MultiDeliveryWaybillDetail where MultiDeliveryID = " + multiDeliveryRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        multiDeliveryRequest.multiDeliveryWaybillDetails.add(index, new MultiDeliveryWaybillDetail(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")), multiDeliveryRequest.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MultiDeliveryDetail where MultiDeliveryID = " + multiDeliveryRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        multiDeliveryRequest.multiDeliveryDetails.add(index, new MultiDeliveryDetail(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), multiDeliveryRequest.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(multiDeliveryRequest, true);
//
//                ProjectAsyncTask task = new ProjectAsyncTask("MultiDelivery", "Post", jsonData);
//                task.setUpdateListener(new OnUpdateListener() {
//                    public void onPostExecuteUpdate(String obj) {
//
//
//                        if (obj.contains("Created")) {
//                            multiDeliveryRequest.IsSync = true;
//                            for (int i = 0; i < multiDeliveryRequest.multiDeliveryDetails.size(); i++) {
//                                multiDeliveryRequest.multiDeliveryDetails.get(i).IsSync = true;
//                            }
//
//                            for (int i = 0; i < multiDeliveryRequest.multiDeliveryWaybillDetails.size(); i++) {
//                                multiDeliveryRequest.multiDeliveryWaybillDetails.get(i).IsSync = true;
//                            }
//
//                            GlobalVar.GV().dbConnections.UpdateMultiDelivery(multiDeliveryRequest);
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Info);
//                        } else
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Error);
//                    }
//
//                    public void onPreExecuteUpdate() {
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Sending Multi Delivery Data", GlobalVar.AlertType.Info);
//
//                    }
//                });
//                task.execute();
//            }
//            while (result.moveToNext());
//        }
//    }

    //-------------Sending Multi Delivery Data to the Server -------------------
//    public void SendCheckPointData() {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from CheckPoint where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingMultiDeliveryData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                final CheckPoint checkPoint = new CheckPoint();
//                checkPoint.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                checkPoint.CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
//                checkPoint.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
//                checkPoint.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
//                checkPoint.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
//                checkPoint.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                checkPoint.Longitude = result.getString(result.getColumnIndex("Longitude"));
//                checkPoint.CheckPointTypeDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));
//                checkPoint.CheckPointTypeDDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDDetailID")));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from CheckPointWaybillDetails where CheckPointID = " + checkPoint.ID);
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        checkPoint.CheckPointWaybillDetails.add(index, new CheckPointWaybillDetails(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")), checkPoint.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                resultDetail = GlobalVar.GV().dbConnections.Fill("select * from CheckPointBarCodeDetails where CheckPointID = " + checkPoint.ID);
//                if (resultDetail.getCount() > 0) {
//                    resultDetail.moveToFirst();
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        checkPoint.CheckPointBarCodeDetails.add(index, new CheckPointBarCodeDetails(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), checkPoint.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(checkPoint, true);
//
//                ProjectAsyncTask task = new ProjectAsyncTask("CheckPoint", "Post", jsonData);
//                task.setUpdateListener(new OnUpdateListener() {
//                    public void onPreExecuteUpdate() {
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Sending Check Point Data", GlobalVar.AlertType.Info);
//                    }
//
//                    public void onPostExecuteUpdate(String obj) {
//                        if (obj.contains("Created")) {
//                            checkPoint.IsSync = true;
//                            for (int i = 0; i < checkPoint.CheckPointWaybillDetails.size(); i++) {
//                                checkPoint.CheckPointWaybillDetails.get(i).IsSync = true;
//                            }
//
//                            for (int i = 0; i < checkPoint.CheckPointBarCodeDetails.size(); i++) {
//                                checkPoint.CheckPointBarCodeDetails.get(i).IsSync = true;
//                            }
//
//                            GlobalVar.GV().dbConnections.UpdateCheckPoint(checkPoint);
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Info);
//                        } else
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Error);
//                    }
//
//                });
//                task.execute();
//            }
//            while (result.moveToNext());
//        }
//    }

    //-------------Check New Bookings -------------------


//    public void CheckOnlineBooking(final Context context) {
//
//        DataTableParameters dataTableParameters = new DataTableParameters();
//        dataTableParameters.AppID = GlobalVar.GV().AppID;
//        dataTableParameters.FilterString = "ID > 0 and CourierID = " + GlobalVar.GV().EmployID;
//        dataTableParameters.Length = 50;
//        dataTableParameters.Source = "ViwBooking";
//        dataTableParameters.Start = 0;
//
//        String jsonData = JsonSerializerDeserializer.serialize(dataTableParameters, true);
//        ProjectAsyncTask task = new ProjectAsyncTask("View/GetData", "Post", jsonData);
//        task.setUpdateListener(new OnUpdateListener() {
//            public void onPostExecuteUpdate(String obj) {
//                new Station(obj, GlobalVar.GV().rootViewMainPage, context);
//            }
//
//            public void onPreExecuteUpdate() {
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Loading Online Booking", GlobalVar.AlertType.Info);
//            }
//        });
//        task.execute();
//
//
//    }


//    public void GetBookingListSerer() {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//
//        BringMyBooking dataTableParameters = new BringMyBooking();
//        dataTableParameters.AppID = GlobalVar.GV().AppID;
//        //dataTableParameters.BookingMaxID =
//        dataTableParameters.EmployID = 1024;
//
//        String jsonData = JsonSerializerDeserializer.serialize(dataTableParameters, true);
//        ProjectAsyncTask task = new ProjectAsyncTask("BringBookingList", "Post", jsonData);
//        task.setUpdateListener(new OnUpdateListener() {
//            public void onPostExecuteUpdate(String obj) {
//                new Booking(obj, GlobalVar.GV().rootViewMainPage);
//            }
//
//            public void onPreExecuteUpdate() {
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Loading Online Booking", GlobalVar.AlertType.Info);
//            }
//        });
//        task.execute();
//
//
//    }
//

    //-------------Sending Multi Delivery Data to the Server -------------------
//    public void SendWaybillMeasurementDataData() {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from WaybillMeasurement where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingWaybillMeasurmentData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                final WaybillMeasurement waybillMeasurement = new WaybillMeasurement();
//                waybillMeasurement.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                waybillMeasurement.WaybillNo = Integer.parseInt(result.getString(result.getColumnIndex("WaybillNo")));
//                waybillMeasurement.TotalPieces = Integer.parseInt(result.getString(result.getColumnIndex("TotalPieces")));
//                waybillMeasurement.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
//                waybillMeasurement.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//                waybillMeasurement.CTime = DateTime.parse(result.getString(result.getColumnIndex("CTime")));
//                waybillMeasurement.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
//
//                waybillMeasurement.HHD = result.getString(result.getColumnIndex("HHD"));
//                waybillMeasurement.Weight = Double.parseDouble(result.getString(result.getColumnIndex("Weight")));
//                waybillMeasurement.NoNeedVolume = Boolean.parseBoolean(result.getString(result.getColumnIndex("NoNeedVolume")));
//                waybillMeasurement.NoNeedVolumeReasonID = Integer.parseInt(result.getString(result.getColumnIndex("NoNeedVolumeReasonID")));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from WaybillMeasurementDetail where WaybillMeasurementID = " + waybillMeasurement.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        waybillMeasurement.WaybillMeasurementDetails.add(index,
//                                new WaybillMeasurementDetail(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("PiecesCount"))),
//                                        Double.parseDouble(resultDetail.getString(resultDetail.getColumnIndex("Width"))),
//                                        Double.parseDouble(resultDetail.getString(resultDetail.getColumnIndex("Length"))),
//                                        Double.parseDouble(resultDetail.getString(resultDetail.getColumnIndex("Height"))),
//                                        waybillMeasurement.ID));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(waybillMeasurement, true);
//
//                ProjectAsyncTask task = new ProjectAsyncTask("WaybillMeasurement/Post", "Post", jsonData);
//                task.setUpdateListener(new OnUpdateListener() {
//                    public void onPostExecuteUpdate(String obj) {
//                        if (obj.contains("Created")) {
//                            waybillMeasurement.IsSync = true;
//                            for (int i = 0; i < waybillMeasurement.WaybillMeasurementDetails.size(); i++) {
//                                waybillMeasurement.WaybillMeasurementDetails.get(i).IsSync = true;
//                            }
//
//                            GlobalVar.GV().dbConnections.UpdateWaybillMeasurement(waybillMeasurement);
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Info);
//                        } else
//                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, obj, GlobalVar.AlertType.Error);
//                    }
//
//                    public void onPreExecuteUpdate() {
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Sending Waybill Measurement Data", GlobalVar.AlertType.Info);
//                    }
//                });
//                task.execute();
//            }
//            while (result.moveToNext());
//        }
//    }

    //-------------Sending PickUp Data to the Server -------------------

//    public void SendPickUpData() {
//
//        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from PickUp where IsSync = 0");
//        if (result.getCount() > 0) {
//            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.SendingPickUpData), GlobalVar.AlertType.Info);
//            result.moveToFirst();
//            do {
//                PickUpRequest pickUpRequest = new PickUpRequest();
//                pickUpRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
//                pickUpRequest.WaybillNo = result.getString(result.getColumnIndex("WaybillNo"));
//                pickUpRequest.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
//                pickUpRequest.FromStationID = Integer.parseInt(result.getString(result.getColumnIndex("FromStationID")));
//                pickUpRequest.ToStationID = Integer.parseInt(result.getString(result.getColumnIndex("ToStationID")));
//                pickUpRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PieceCount")));
//                pickUpRequest.Weight = Double.parseDouble(result.getString(result.getColumnIndex("Weight")));
//                pickUpRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
//                pickUpRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
//                pickUpRequest.UserMEID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
//                pickUpRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
//                pickUpRequest.RefNo = result.getString(result.getColumnIndex("RefNo"));
//                pickUpRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
//                pickUpRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
//                pickUpRequest.CurrentVersion = result.getString(result.getColumnIndex("CurrentVersion"));
//
//                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from PickUpDetail where PickUpID = " + pickUpRequest.ID);
//
//                if (resultDetail.getCount() > 0) {
//                    int index = 0;
//                    resultDetail.moveToFirst();
//                    do {
//                        pickUpRequest.PickUpDetailRequestList.add(index, new PickUpDetailRequest(resultDetail.getString(resultDetail.getColumnIndex("BarCode"))));
//                        index++;
//                    }
//                    while (resultDetail.moveToNext());
//                }
//
//                String jsonData = JsonSerializerDeserializer.serialize(pickUpRequest, true);
//                jsonData = jsonData.replace("Date(-", "Date(");
//                new SendPickUpDataToServer().execute(jsonData);
//            }
//            while (result.moveToNext());
//        }
//    }

//    private class SendPickUpDataToServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Syncing PickUp Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendPickUpDataToServer");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                System.out.println(e.toString());
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            if (finalJson != null) {
//                SendingDataResult sendingDataResult = new SendingDataResult(finalJson);
//                if (sendingDataResult.IsSync && !sendingDataResult.HasError) {
//                    if (GlobalVar.GV().dbConnections != null) {
//                        PickUp instance = new PickUp(sendingDataResult.ID);
//                        GlobalVar.GV().dbConnections.UpdatePickUp(instance);
//                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().context.getString(R.string.PickUpSyncSuccessfully), GlobalVar.AlertType.Info);
//                    }
//                } else if (sendingDataResult.HasError)
//                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Data Not Sync Because :" + sendingDataResult.ErrorMessage, GlobalVar.AlertType.Error);
//            } else
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, "Pick up Data Not Sync Because : Something went wrong,kindly check your internet connection", GlobalVar.AlertType.Error);
//
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //------------Get UserME Details --------------------------------------


    //-------------Get Station Data From Server ----------------
//    public void GetStation() {
//
//        GetStationRequest getStationRequest = new GetStationRequest();
//        String jsonData = JsonSerializerDeserializer.serialize(getStationRequest, true);
//        new GetStationFromServer().execute(jsonData);
//    }
//
//    private class GetStationFromServer extends AsyncTask<String, Void, String> {
//        //private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            //progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Bringing Master Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetStation");
//                //URL url = new URL( "https://infotrack.naqelexpress.com/NaqelAPIServices/InfoTrackWebAPI/6.0/API/Station/Get");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            //progressDialog.dismiss();
//            new StationResult(finalJson);
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }

    //-------------Get Delivery Status Data From Server ----------------
    Context context;
    View view;

    public void GetDeliveryStatus(Context context, View view) {
        this.context = context;
        this.view = view;
        GetDeliveryStatusRequest getDeliveryStatusRequest = new GetDeliveryStatusRequest();
        String jsonData = JsonSerializerDeserializer.serialize(getDeliveryStatusRequest, true);
        new GetDeliveryStatusFromServer().execute(jsonData);
    }

    private class GetDeliveryStatusFromServer extends AsyncTask<String, Void, String> {
        //private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            //progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Bringing Master Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetDeliveryStatus");
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            //progressDialog.dismiss();
            if (finalJson != null)
                new DeliveryStatus(finalJson, view, context);
            GlobalVar.dsl = true;
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    //-------------Get Check Point Type Data From Server ----------------
//    public void GetCheckPointType()
//    {
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;
//        GetDeliveryStatusRequest getDeliveryStatusRequest = new GetDeliveryStatusRequest();
//        String jsonData = JsonSerializerDeserializer.serialize(getDeliveryStatusRequest, true);
//        new GetCheckPointTypeFromServer().execute(jsonData);
//    }

//    private class GetCheckPointTypeFromServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Bringing Master Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetCheckPointTypeFromServer");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            progressDialog.dismiss();
//            new CheckPointTypeResult(finalJson, GlobalVar.GV().rootViewMainPage, context);
//            super.onPostExecute(String.valueOf(finalJson));
//        }
//    }
//
//    //--------------Get Student Shipments -------------------------------
//    public void GetShipmentForPicking() {
//
//        GlobalVar.GV().GetShipmentForPickingResultList = new ArrayList<>();
//        GetShipmentForPickingRequest getShipmentForPickingRequest = new GetShipmentForPickingRequest();
//        String jsonData = JsonSerializerDeserializer.serialize(getShipmentForPickingRequest, true);
//        new GetShipmentForPickingFromServer().execute(jsonData);
//    }
//
//    private class GetShipmentForPickingFromServer extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Downloading Shipments Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetShipmentForPicking");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//            new GetShipmentForPickingResult(finalJson);
//            super.onPostExecute(String.valueOf(finalJson));
//            progressDialog.dismiss();
//        }
//    }
}