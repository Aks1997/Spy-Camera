package com.aks.spycamera;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class Videos extends AppCompatActivity implements CustomAdapter.Observer{

    private RecyclerView recyclerView;
    CustomAdapter customAdapter;
    ArrayList<Uri> arrayList=new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        findIds();
        setData();
        setAdapter();
    }

    private void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapter = new CustomAdapter(this,arrayList,this);
        recyclerView.setAdapter(customAdapter);
    }

    private void setData() {
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"spyCamera");
        Log.d("Files", "Path: " + file);
        File[] files = file.listFiles();
        Log.d("Files", "Size: "+ files.length);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(files[0]), "video/mp4");
//        startActivity(intent);
        for (int i = 0; i < files.length; i++)
        {
            arrayList.add(Uri.fromFile(files[i]));
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

    private void findIds() {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_id);
    }

    @Override
    public void cl(int pos) {
        arrayList.remove(pos);
        customAdapter.notifyDataSetChanged();
    }
}
