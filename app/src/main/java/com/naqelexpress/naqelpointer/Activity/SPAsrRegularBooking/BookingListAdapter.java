package com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingListAdapter extends
        RecyclerView.Adapter<BookingListAdapter.ViewHolder> implements Filterable {
    // BaseAdapter implements Filterable {
    private Activity context;
    private ArrayList<BookingModel> itemList;
    private String class_;
    private List<BookingModel> itemListFiltered;
    private BookingList listener;

    public BookingListAdapter(Activity context, ArrayList<BookingModel> itemList, String class_, BookingList listener) {
        this.context = context;
        this.itemList = itemList;
        this.class_ = class_;
        itemListFiltered = itemList;
        this.listener = listener;
    }

//    @Override
//    public int getCount() {
//        return itemListFiltered.size();
//    }

//    @Override
//    public BookingModel getItem(int position) {
//        return itemListFiltered.get(position);
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    protected static class ViewHolderItems {
        private TextView ItemNo;
        private TextView TypeID;
    }
//
//    @Override
//    public int getViewTypeCount() {
//        // menu type count
//        return 2;
//    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.spasrregular_bookinglist, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        //final ViewHolder holder = (ViewHolder) viewHolder.getTag();
        final BookingModel item = itemListFiltered.get(position);

        holder.byPickup.setText(String.valueOf(item.getBKHeader()));

        holder.sno.setText(String.valueOf(item.getsNo()));


        if (item.getisSPL()) {


            if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".")
                    && Double.parseDouble(item.Lat) > 0)
                holder.location.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.marker, 0);
            else
                holder.location.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


            holder.puidawbno.setText("Pu ID / AWB : " + item.getPickupSheetID());
            holder.location.setText(item.getSPOfficeName());

            holder.mno1.setText(item.getSPMobile());
            //holder.numberofwaybills.setText(String.valueOf(item.getWaybillcount()));
            //holder.numberofwaybillsll.setVisibility(View.INVISIBLE);
            holder.mno2ll.setVisibility(View.GONE);
            holder.clientll.setVisibility(View.GONE);
            holder.contactll.setVisibility(View.GONE);
            // holder.tableLayout.setVisibility(View.VISIBLE);
            holder.ibwats1.setVisibility(View.GONE);
            holder.ibwats2.setVisibility(View.GONE);

            holder.totalpickwaybill.setVisibility(View.VISIBLE);
            holder.pickedupcount.setVisibility(View.VISIBLE);
            holder.exceptioncount.setVisibility(View.VISIBLE);

            String tpkCount = "Total for PU :" + String.valueOf(item.getWaybillcount());
            // holder.totalpickwaybill.setText("Total for PU :" + String.valueOf(item.getWaybillcount()));
            SpannableString content = new SpannableString(tpkCount);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.totalpickwaybill.setText(content);
            holder.pickedupcount.setText("Picked Up :" + String.valueOf(item.getPickupCount()));
            holder.pickedupcount.setTextColor(context.getResources().getColor(R.color.color_green));
            //int excount = item.getWaybillcount() -  item.getPickupCount();
            holder.exceptioncount.setText("Exception :" + String.valueOf(item.getExceptionCount()));

        } else {

            holder.locationll.setVisibility(View.GONE);
            //holder.numberofwaybillsll.setVisibility(View.INVISIBLE);
            holder.consname.setText(item.getConsigneeName());
            holder.clientname.setText(item.getClientName());

            if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") &&
                    Double.parseDouble(item.Lat) > 0)
                holder.consname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.marker, 0);
            else
                holder.consname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            String first = "Pu ID / AWB : ";
            String next = "<font color='#003E7E'>" + String.valueOf(item.getWaybillNo()) + "</font>";
            holder.puidawbno.setText(Html.fromHtml(first + next));

            //holder.puidawbno.setText("Pu ID / AWB : " + String.valueOf(item.getWaybillNo()));
            holder.mno1.setText(item.getPhoneNo());
            holder.mno2.setText(item.getMobileNo());

            holder.clientname.setVisibility(View.VISIBLE);
            holder.consname.setVisibility(View.VISIBLE);
            holder.mno2.setVisibility(View.VISIBLE);
            holder.clientll.setVisibility(View.VISIBLE);
            holder.contactll.setVisibility(View.VISIBLE);
            holder.mno2ll.setVisibility(View.VISIBLE);

            holder.totalpickwaybill.setVisibility(View.GONE);
            holder.pickedupcount.setVisibility(View.GONE);
            holder.exceptioncount.setVisibility(View.GONE);
            // holder.tableLayout.setVisibility(View.INVISIBLE);
        }


        if (item.getIsPickedup() == 1)
            holder.attempedstatus.setText("Attempted");
        else if (item.getIsPickedup() == 2)
            holder.attempedstatus.setText("Picked Up");
        else
            holder.attempedstatus.setText("Not Attempt");


        holder.ibmno1.setFocusable(false);
        holder.ibmno1.setFocusableInTouchMode(false);
        holder.ibwats1.setFocusable(false);
        holder.ibwats1.setFocusableInTouchMode(false);
        holder.ibmno2.setFocusable(false);
        holder.ibmno2.setFocusableInTouchMode(false);
        holder.ibwats2.setFocusable(false);
        holder.ibwats2.setFocusableInTouchMode(false);

        holder.ibmno1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVar.GV().makeCall(item.getPhoneNo(), context.getWindow().getDecorView().getRootView(), context);
            }
        });

        holder.ibmno2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVar.GV().makeCall(item.getMobileNo(), context.getWindow().getDecorView().getRootView(), context);
            }
        });

        holder.ibwats1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!item.getisSPL())
                    showPopup(item.getPhoneNo(), item.getLat(), String.valueOf(item.getWaybillNo()), item.getClientName());
//                GlobalVar.GV().sendMessageToWhatsAppContact(item.getPhoneNo(), "Please provide the text , we will update this " +
//                        item.getPhoneNo(), context);
            }
        });

        holder.ibwats2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!item.getisSPL())
                    showPopup(item.getPhoneNo(), item.getLat(), String.valueOf(item.getWaybillNo()), item.getClientName());
//                GlobalVar.GV().sendMessageToWhatsAppContact(item.getMobileNo(), "Please provide the text , we will update this " +
//                        item.getPhoneNo(), context);
            }
        });

        holder.locationll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") && Double.parseDouble(item.Lat) > 0) {
                    Location location = GlobalVar.getLastKnownLocation(context.getApplicationContext());
//                if (item.getisSPL()) {
//                    if (item.getSpLatLng() != null && item.getSpLatLng().length() > 0) {
//                        String latlng[] = item.getSpLatLng().split(",");
//                        GlobalVar.toGoogle(latlng[0], latlng[1], context, location);
//                    }
//                } else {
//                    if (item.Lat != null && item.Lat.length() > 0) {
//
//                        GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
//                    }
//                }

                    GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
                } else
                    alertforcommon("Error", "Invalid Location");

            }
        });

        holder.contactll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") && Double.parseDouble(item.Lat) > 0) {
                    Location location = GlobalVar.getLastKnownLocation(context.getApplicationContext());
//                if (item.getisSPL()) {
//                    if (item.getSpLatLng() != null && item.getSpLatLng().length() > 0) {
//                        String latlng[] = item.getSpLatLng().split(",");
//                        GlobalVar.toGoogle(latlng[0], latlng[1], context, location);
//                    }
//                } else {
//                    if (item.Lat != null && item.Lat.length() > 0) {
//
//                        GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
//                    }
//                }

                    GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
                } else
                    alertforcommon("Error", "Invalid Location");

            }
        });


        //GlobalVar.toGoogle(ConsigneeLatitude, ConsigneeLongitude, WaybillPlanActivity.this, location);

//        holder.sno.setText(String.valueOf(item.getsNo()));
//        holder.waybillno.setText(String.valueOf(item.getWaybillNo()));
//
//
//        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        try {
//            Date dt = formatter.parse(item.getDate());
//            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            String dte = dfmt.format(dt);
//
//
//            holder.date.setText(dte);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        holder.consname.setText(item.getConsigneeName());
//        holder.orgstation.setText(item.OrgCode);
//        holder.deststation.setText(item.getDestCode());
//        holder.billtype.setText(item.getCode());
//
//        if (item.isPickedup == 0) {
//            holder.ispickedup.setText("Not Attempted");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.NaqelRed));
//        } else if (item.isPickedup == 1) {
//            holder.ispickedup.setText("Attempted");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_orange_color));
//        } else if (item.isPickedup == 2) {
//            holder.ispickedup.setText("Picked Up");
//            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_green_color));
//        }
//
//        if (item.getLat() != null && item.getLat().length() > 3 && !item.getLat().equals("0"))
//            holder.islocation.setVisibility(View.VISIBLE);
//        else
//            holder.islocation.setVisibility(View.GONE);

        final int acceptposition = position;


        // return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 2;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<BookingModel> filteredList = new ArrayList<>();
                    for (BookingModel row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.WaybillNo).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFiltered = (ArrayList<BookingModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    /* @SuppressLint({"SetTextI18n", "WrongConstant"})
     @Override
     public View getView(final int position, View convertView, ViewGroup parent) {
         if (convertView == null) {
             //convertView = View.inflate(context, R.layout.bookingitempickupsheet, null);
             convertView = View.inflate(context, R.layout.spasrregular_bookinglist, null);
             new ViewHolder(convertView);
         } else {
             // holdeconvertView = (ViewHolder) convertView.getTag();
         }

         final ViewHolder holder = (ViewHolder) convertView.getTag();
         final BookingModel item = getItem(position);

         holder.byPickup.setText(String.valueOf(item.getBKHeader()));

         holder.sno.setText(String.valueOf(item.getsNo()));


         if (item.getisSPL()) {


             if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".")
                     && Double.parseDouble(item.Lat) > 0)
                 holder.location.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.marker, 0);
             else
                 holder.location.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


             holder.puidawbno.setText("Pu ID / AWB : " + item.getPickupSheetID());
             holder.location.setText(item.getSPOfficeName());

             holder.mno1.setText(item.getSPMobile());
             //holder.numberofwaybills.setText(String.valueOf(item.getWaybillcount()));
             //holder.numberofwaybillsll.setVisibility(View.INVISIBLE);
             holder.mno2ll.setVisibility(View.GONE);
             holder.clientll.setVisibility(View.GONE);
             holder.contactll.setVisibility(View.GONE);
             // holder.tableLayout.setVisibility(View.VISIBLE);
             holder.ibwats1.setVisibility(View.GONE);
             holder.ibwats2.setVisibility(View.GONE);

             holder.totalpickwaybill.setVisibility(View.VISIBLE);
             holder.pickedupcount.setVisibility(View.VISIBLE);
             holder.exceptioncount.setVisibility(View.VISIBLE);

             String tpkCount = "Total for PU :" + String.valueOf(item.getWaybillcount());
             // holder.totalpickwaybill.setText("Total for PU :" + String.valueOf(item.getWaybillcount()));
             SpannableString content = new SpannableString(tpkCount);
             content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
             holder.totalpickwaybill.setText(content);
             holder.pickedupcount.setText("Picked Up :" + String.valueOf(item.getPickupCount()));
             holder.exceptioncount.setText("Exception :" + String.valueOf(item.getExceptionCount()));
         } else {

             holder.locationll.setVisibility(View.GONE);
             //holder.numberofwaybillsll.setVisibility(View.INVISIBLE);
             holder.consname.setText(item.getConsigneeName());
             holder.clientname.setText(item.getClientName());

             if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") &&
                     Double.parseDouble(item.Lat) > 0)
                 holder.consname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.marker, 0);
             else
                 holder.consname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

             String first = "Pu ID / AWB : ";
             String next = "<font color='#003E7E'>" + String.valueOf(item.getWaybillNo()) + "</font>";
             holder.puidawbno.setText(Html.fromHtml(first + next));

             //holder.puidawbno.setText("Pu ID / AWB : " + String.valueOf(item.getWaybillNo()));
             holder.mno1.setText(item.getPhoneNo());
             holder.mno2.setText(item.getMobileNo());

             holder.clientname.setVisibility(View.VISIBLE);
             holder.consname.setVisibility(View.VISIBLE);
             holder.mno2.setVisibility(View.VISIBLE);
             holder.clientll.setVisibility(View.VISIBLE);
             holder.contactll.setVisibility(View.VISIBLE);
             holder.mno2ll.setVisibility(View.VISIBLE);

             holder.totalpickwaybill.setVisibility(View.GONE);
             holder.pickedupcount.setVisibility(View.GONE);
             holder.exceptioncount.setVisibility(View.GONE);
             // holder.tableLayout.setVisibility(View.INVISIBLE);
         }


         if (item.getIsPickedup() == 1)
             holder.attempedstatus.setText("Attempted");
         else if (item.getIsPickedup() == 2)
             holder.attempedstatus.setText("Picked Up");
         else
             holder.attempedstatus.setText("Not Attempt");


         holder.ibmno1.setFocusable(false);
         holder.ibmno1.setFocusableInTouchMode(false);
         holder.ibwats1.setFocusable(false);
         holder.ibwats1.setFocusableInTouchMode(false);
         holder.ibmno2.setFocusable(false);
         holder.ibmno2.setFocusableInTouchMode(false);
         holder.ibwats2.setFocusable(false);
         holder.ibwats2.setFocusableInTouchMode(false);

         holder.ibmno1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 GlobalVar.GV().makeCall(item.getPhoneNo(), context.getWindow().getDecorView().getRootView(), context);
             }
         });

         holder.ibmno2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 GlobalVar.GV().makeCall(item.getMobileNo(), context.getWindow().getDecorView().getRootView(), context);
             }
         });

         holder.ibwats1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (!item.getisSPL())
                     showPopup(item.getPhoneNo(), item.getLat(), String.valueOf(item.getWaybillNo()), item.getClientName());
 //                GlobalVar.GV().sendMessageToWhatsAppContact(item.getPhoneNo(), "Please provide the text , we will update this " +
 //                        item.getPhoneNo(), context);
             }
         });

         holder.ibwats2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (!item.getisSPL())
                     showPopup(item.getPhoneNo(), item.getLat(), String.valueOf(item.getWaybillNo()), item.getClientName());
 //                GlobalVar.GV().sendMessageToWhatsAppContact(item.getMobileNo(), "Please provide the text , we will update this " +
 //                        item.getPhoneNo(), context);
             }
         });

         holder.locationll.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") && Double.parseDouble(item.Lat) > 0) {
                     Location location = GlobalVar.getLastKnownLocation(context.getApplicationContext());
 //                if (item.getisSPL()) {
 //                    if (item.getSpLatLng() != null && item.getSpLatLng().length() > 0) {
 //                        String latlng[] = item.getSpLatLng().split(",");
 //                        GlobalVar.toGoogle(latlng[0], latlng[1], context, location);
 //                    }
 //                } else {
 //                    if (item.Lat != null && item.Lat.length() > 0) {
 //
 //                        GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
 //                    }
 //                }

                     GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
                 } else
                     alertforcommon("Error", "Invalid Location");

             }
         });

         holder.contactll.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (item.Lat != null && !item.Lat.equals("null") && item.Lat.length() > 1 && item.Lat.contains(".") && Double.parseDouble(item.Lat) > 0) {
                     Location location = GlobalVar.getLastKnownLocation(context.getApplicationContext());
 //                if (item.getisSPL()) {
 //                    if (item.getSpLatLng() != null && item.getSpLatLng().length() > 0) {
 //                        String latlng[] = item.getSpLatLng().split(",");
 //                        GlobalVar.toGoogle(latlng[0], latlng[1], context, location);
 //                    }
 //                } else {
 //                    if (item.Lat != null && item.Lat.length() > 0) {
 //
 //                        GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
 //                    }
 //                }

                     GlobalVar.toGoogle(item.Lat, item.Lng, context, location);
                 } else
                     alertforcommon("Error", "Invalid Location");

             }
         });


         //GlobalVar.toGoogle(ConsigneeLatitude, ConsigneeLongitude, WaybillPlanActivity.this, location);

 //        holder.sno.setText(String.valueOf(item.getsNo()));
 //        holder.waybillno.setText(String.valueOf(item.getWaybillNo()));
 //
 //
 //        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
 //        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 //        try {
 //            Date dt = formatter.parse(item.getDate());
 //            DateFormat dfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 //            String dte = dfmt.format(dt);
 //
 //
 //            holder.date.setText(dte);
 //        } catch (ParseException e) {
 //            e.printStackTrace();
 //        }
 //        holder.consname.setText(item.getConsigneeName());
 //        holder.orgstation.setText(item.OrgCode);
 //        holder.deststation.setText(item.getDestCode());
 //        holder.billtype.setText(item.getCode());
 //
 //        if (item.isPickedup == 0) {
 //            holder.ispickedup.setText("Not Attempted");
 //            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.NaqelRed));
 //        } else if (item.isPickedup == 1) {
 //            holder.ispickedup.setText("Attempted");
 //            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_orange_color));
 //        } else if (item.isPickedup == 2) {
 //            holder.ispickedup.setText("Picked Up");
 //            holder.ispickedup.setTextColor(context.getResources().getColor(R.color.main_green_color));
 //        }
 //
 //        if (item.getLat() != null && item.getLat().length() > 3 && !item.getLat().equals("0"))
 //            holder.islocation.setVisibility(View.VISIBLE);
 //        else
 //            holder.islocation.setVisibility(View.GONE);

         final int acceptposition = position;


         return convertView;
     }
 */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView byPickup, puidawbno, location, mno1, mno2, consname, clientname, attempedstatus,
                totalpickwaybill, pickedupcount, exceptioncount, sno;//, numberofwaybills;
        LinearLayout clientll, locationll, contactll, mno2ll; //, numberofwaybillsll;
        ImageButton ibmno1, ibwats1, ibmno2, ibwats2;
        //TableLayout tableLayout;
//                , waybillno, date, consname, orgstation,
//                deststation, billtype, sno, ispickedup;
//        ImageView islocation;
        //TextView panel;

        public ViewHolder(View view) {
            super(view);
            //tableLayout = (TableLayout) view.findViewById(R.id.tl_sp);
            totalpickwaybill = (TextView) view.findViewById(R.id.totalforpickup);
            sno = (TextView) view.findViewById(R.id.sno);
            pickedupcount = (TextView) view.findViewById(R.id.pickedup);
            exceptioncount = (TextView) view.findViewById(R.id.exception);

            byPickup = (TextView) view.findViewById(R.id.byPickup);
            puidawbno = (TextView) view.findViewById(R.id.puidawbno);
            location = (TextView) view.findViewById(R.id.location);
            clientll = (LinearLayout) view.findViewById(R.id.clientll);
            locationll = (LinearLayout) view.findViewById(R.id.locationll);
            mno2ll = (LinearLayout) view.findViewById(R.id.mno2ll);
            contactll = (LinearLayout) view.findViewById(R.id.contactll);
            mno1 = (TextView) view.findViewById(R.id.mno1);
            mno2 = (TextView) view.findViewById(R.id.mno2);
            clientname = (TextView) view.findViewById(R.id.clientname);
            consname = (TextView) view.findViewById(R.id.contactname);
            // numberofwaybills = (TextView) view.findViewById(R.id.numberofwaybills);
            attempedstatus = (TextView) view.findViewById(R.id.attempedstatus);
            // numberofwaybillsll = (LinearLayout) view.findViewById(R.id.numberofwaybillsll);

            ibmno1 = (ImageButton) view.findViewById(R.id.ibmno1);
            ibwats1 = (ImageButton) view.findViewById(R.id.ibwats1);
            ibmno2 = (ImageButton) view.findViewById(R.id.ibmno2);
            ibwats2 = (ImageButton) view.findViewById(R.id.ibwats2);

//            islocation = (ImageView) view.findViewById(R.id.islocation);
//            sno = (TextView) view.findViewById(R.id.sno);
//            ispickedup = (TextView) view.findViewById(R.id.ispickedup);
//            waybillno = (TextView) view.findViewById(R.id.waybillno);
//            date = (TextView) view.findViewById(R.id.date);
//            consname = (TextView) view.findViewById(R.id.consname);
//            orgstation = (TextView) view.findViewById(R.id.orgstation);
//            deststation = (TextView) view.findViewById(R.id.deststation);
//            // contactnumber  = (TextView) view.findViewById(R.id.lblContactNo);
//            billtype = (TextView) view.findViewById(R.id.billtype);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(itemListFiltered.get(getAdapterPosition()), getAdapterPosition());
                }
            });

            view.setTag(this);
        }
    }


    //update the status

    PopupWindow popup;

    private void showPopup(final String mobileno, final String ConsigneeLatitude, final String Waybillno,
                           final String ClientName) {


        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.messagedrafts, viewGroup);

        final TextView customerlocation = (TextView) layout.findViewById(R.id.customerlocation);
        final TextView frontofthedoor = (TextView) layout.findViewById(R.id.frontofthedoor);
        final TextView cssupport = (TextView) layout.findViewById(R.id.cssupport);
        final TextView resndotp = (TextView) layout.findViewById(R.id.resendotp);
        resndotp.setVisibility(View.GONE);

        final String arabic = "?????????? ????????????, n\n  ???????? ???? ?????????????? ?????????? ?????? ???????????? ???????????? ?????????? ?????????? ???????????? ??????????.(" + " " + Waybillno
                + ") ????)" + ClientName;

        customerlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConsigneeLatitude != null && !ConsigneeLatitude.equals("null") && ConsigneeLatitude.length() > 1
                        && ConsigneeLatitude.contains(".") && Double.parseDouble(ConsigneeLatitude) > 0
                ) //&& ConsigneeLatitude != null && ConsigneeLatitude.length() == 0
//                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.watsappPredefinedMsg)
//                            + " " + txtWaybillNo.getText().toString() + getString(R.string.watsappPredefinedMsg1)
//                            + txtShipperName.getText().toString() + "\n\n\n" + arabic + getString(R.string.watsappPredefinedMsg2)
//                            + txtWaybillNo.getText().toString(), getApplicationContext());
                    alertforcommon("Has Location", "This Shipment already has Location , kindly please start to deliver");

                else
                    GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, GlobalVar.GetLocationWatsMsg(context.getApplicationContext(),
                            Waybillno, ClientName), context.getApplicationContext());

                popup.dismiss();
            }
        });
        frontofthedoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.doorPredefinedMsg)
//                        + " " + txtWaybillNo.getText().toString() + getString(R.string.doorPredefinedMsg1)
//                        + txtShipperName.getText().toString() + getString(R.string.doorPredefinedMsg2)
//                        + txtWaybillNo.getText().toString() + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, GlobalVar.GV().getFrontDoorMsgAsrPickup(
                        Waybillno, ClientName), context.getApplicationContext());
                popup.dismiss();
            }
        });
        cssupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, getString(R.string.csPredefinedMsg) + "\n\n\n" + arabic, getApplicationContext());
                GlobalVar.GV().sendMessageToWhatsAppContact(mobileno, GlobalVar.GV().getCsSupportMsg(context.getApplicationContext()), context.getApplicationContext());
                popup.dismiss();
            }
        });


        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private void alertforcommon(String title, String message) {
        SweetAlertDialog eDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(true);
        eDialog.setTitleText(title);
        eDialog.setContentText(message);
        eDialog.show();

    }

    public interface FilterListener {
        void onItemSelected(BookingModel bookingModel, int position);
    }
}