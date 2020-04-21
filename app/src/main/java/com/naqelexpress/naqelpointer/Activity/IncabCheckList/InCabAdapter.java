package com.naqelexpress.naqelpointer.Activity.IncabCheckList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.naqelexpress.naqelpointer.ItemClickListener;
import com.naqelexpress.naqelpointer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import static com.naqelexpress.naqelpointer.Activity.IncabCheckList.IncCabChecklist.selectedreason;


public class InCabAdapter
        extends RecyclerView.Adapter<InCabAdapter.ViewHolder> {
    private ItemClickListener clickListener;
    private Activity activity;
    ArrayList<HashMap<String, String>> reason;

    public InCabAdapter(ArrayList<HashMap<String, String>> pocdetails, Activity activity) {
        this.reason = pocdetails;
        this.activity = activity;
    }

    @Override
    public InCabAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incabchecklist, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InCabAdapter.ViewHolder viewHolder, int i) {

        // if (reason.get(i).get("isComplete").equals("0")) {
        viewHolder.selected.setText(reason.get(i).get("Name"));
//        if (reason.get(i).get("ischecked").equals("1"))
//            viewHolder.selected.setChecked(true);
//        else
//            viewHolder.selected.setChecked(false);

        CheckBoxChecked updatemessages = new CheckBoxChecked(viewHolder.selected);
        StartAsyncTaskInParallel(updatemessages, reason.get(i).get("ischecked"));
        viewHolder.selected.setTag(Integer.parseInt(reason.get(i).get("ID")));
        viewHolder.selected.setId(i);
        //}
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // private TextView txtBarCode;
        private CheckBox selected;

        private ViewHolder(View view) {
            super(view);

            // txtBarCode = (TextView) view.findViewById(R.id.txtWaybilll);
            selected = (CheckBox) view.findViewById(R.id.check1);

            selected.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                int id = (Integer) buttonView.getId();
                                //int pos = (Integer) buttonView.getTag();
                                reason.get(id).put("ischecked", "1");
                                selectedreason.add((Integer) buttonView.getTag());

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