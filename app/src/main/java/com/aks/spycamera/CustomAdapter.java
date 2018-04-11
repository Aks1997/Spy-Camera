package com.aks.spycamera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Akhil on 31-10-2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    Context context;
    ArrayList<Uri> arrayList;
    Observer observer;

    public CustomAdapter(Context context,ArrayList<Uri> arrayList,Observer observer){
        this.context=context;
        this.arrayList=arrayList;
        this.observer=observer;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.text.setText(arrayList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView text;

        public CustomViewHolder(View itemView) {
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.file_txt_id);
            text.setOnClickListener(this);
            itemView.setClickable(true);
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    builder.setCancelable(true);
                    builder.setTitle("Delete this video");
                    builder.setInverseBackgroundForced(true);
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    File file=new File(arrayList.get(getAdapterPosition()).getPath());
                                    boolean deleted = file.delete();
                                    Log.d("Fi",deleted+"");
                                    observer.cl(getAdapterPosition());
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(arrayList.get(getAdapterPosition()), "video/mp4");
            context.startActivity(intent);
        }
    }

    public interface Observer{
        public void cl(int pos);
    }
}
