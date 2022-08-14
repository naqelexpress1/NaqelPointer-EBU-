package com.naqelexpress.naqelpointer.Activity.InCabNew;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.ItemClickListener;
import com.naqelexpress.naqelpointer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.naqelexpress.naqelpointer.Activity.InCabNew.IncCabChecklist.selectedreason;


public class InCabAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ItemClickListener clickListener;
    private Activity activity;
    ArrayList<HashMap<String, String>> reason;
    HashSet<Integer> headerint = new HashSet<>();

    public InCabAdapter(ArrayList<HashMap<String, String>> pocdetails, Activity activity, HashSet<Integer> hashSet) {
        this.reason = pocdetails;
        this.activity = activity;
        this.headerint = hashSet;
    }

    //String header = "";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view;
        if (headerint.contains(i)) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incabnewheader, viewGroup, false);
            return new SectionViewHeader(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incabnewitem, viewGroup, false);
            return new SectionItemHolder(view);
        }

    }


    public class SectionViewHeader extends RecyclerView.ViewHolder {
        private TextView headertext;

        public SectionViewHeader(View itemView) {
            super(itemView);
            headertext = (TextView) itemView.findViewById(R.id.header);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        boolean isHeader = false;
        if (headerint.contains(i))
            isHeader = true;

        if (!isHeader) {
            SectionItemHolder item = (SectionItemHolder) viewHolder;
            // if (reason.get(i).get("isComplete").equals("0")) {
            item.header.setText(reason.get(i).get("Name"));
//        if (reason.get(i).get("ischecked").equals("1"))
//            viewHolder.ok.setChecked(true);
//        else
//            viewHolder.ok.setChecked(false);

            CheckBoxChecked updatemessages = new CheckBoxChecked(item.ok);
            StartAsyncTaskInParallel(updatemessages, reason.get(i).get("ischecked"));

            item.ok.setTag(Integer.parseInt(reason.get(i).get("ID")));
            item.ok.setId(i);

            item.notok.setTag(Integer.parseInt(reason.get(i).get("ID")));
            item.notok.setId(i);


        } else {
            SectionViewHeader item = (SectionViewHeader) viewHolder;

            item.headertext.setText(reason.get(i).get("Name"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        boolean isHeader = false;
        if (headerint.contains(position))
            isHeader = true;

        if (isHeader) {
            return 0;
        } else {
            return 1;
        }
    }

    private void StartAsyncTaskInParallel(CheckBoxChecked asynthread, String keys) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys);
        else
            asynthread.execute(keys);

    }

    public void removeItem(int position) {
        reason.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reason.size());
    }

    @Override
    public int getItemCount() {
        return reason.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class SectionItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // private TextView txtBarCode;
        private CheckBox ok;
        private CheckBox notok;
        private TextView header;

        private SectionItemHolder(View view) {
            super(view);

            header = (TextView) view.findViewById(R.id.header);
            ok = (CheckBox) view.findViewById(R.id.check1);
            notok = (CheckBox) view.findViewById(R.id.check2);

            ok.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
//                                int id = (Integer) buttonView.getId();
//                                reason.get(id).put("ischecked", "1");
//                                selectedreason.add((Integer) buttonView.getTag());
                                notok.setChecked(false);
//                                int id = (Integer) buttonView.getId();
//                                //int pos = (Integer) buttonView.getTag();
//                                reason.get(id).put("ischecked", "0");
//                                selectedreason.remove(new Integer((Integer) buttonView.getTag()));


                            } else {
//                                int id = (Integer) buttonView.getId();
//                                //int pos = (Integer) buttonView.getTag();
//                                reason.get(id).put("ischecked", "0");
//                                selectedreason.remove(new Integer((Integer) buttonView.getTag()));
                            }
                        }
                    });

            notok.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                int id = (Integer) buttonView.getId();
                                //int pos = (Integer) buttonView.getTag();
                                reason.get(id).put("ischecked", "1");
                                selectedreason.add((Integer) buttonView.getTag());
                                ok.setChecked(false);

                            } else {
                                int id = (Integer) buttonView.getId();
                                //int pos = (Integer) buttonView.getTag();
                                reason.get(id).put("ischecked", "0");
                                selectedreason.remove(new Integer((Integer) buttonView.getTag()));
                            }
                        }
                    });

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    class CheckBoxChecked extends AsyncTask<String, Void, Integer> {
        private final WeakReference<CheckBox> checkBoxWeakReference;

        public CheckBoxChecked(CheckBox checkBox) {
            checkBoxWeakReference = new WeakReference<CheckBox>(checkBox);
        }

        @Override
        protected Integer doInBackground(String... params) {


            return Integer.parseInt(params[0]);
        }

        @Override
        protected void onPostExecute(Integer checked) {
            if (isCancelled()) {
                checked = null;
            }

            if (checkBoxWeakReference != null) {
                CheckBox checkBox = checkBoxWeakReference.get();
                if (checkBox != null) {
                    if (checked != null) {
                        if (checked == 1)
                            checkBox.setChecked(true);
                        else
                            checkBox.setChecked(false);

                    }
                }
            }
        }
    }

}