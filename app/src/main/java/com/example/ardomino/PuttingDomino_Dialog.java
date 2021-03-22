package com.example.ardomino;
import  android.app.Activity;
import  android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import java.util.ArrayList;

public class PuttingDomino_Dialog {
    private Activity activity;
    private AlertDialog dialog;
    PuttingDomino_Dialog(Activity myActivity){
        activity=myActivity;
    }
    void LoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        builder.setPositiveButton("Return To Drawing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Help! Domino Problem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DominoProblem();
                DrawingActivity.Current_Pointer=DrawingActivity.Prev_Pointer;
                LoadingDialog();
            }
        });
        dialog=builder.create();
        dialog.show();

    }
    void  DismissDialog(){
        dialog.dismiss();
    }
    void  DominoProblem(){
        ArrayList<Integer> array=new ArrayList<Integer>();
        array.add(0,-1);
        array.add(0,-1);
        array.add(0,-5);
        array.add(0,-5);
        DrawingActivity.myRef.setValue(array);
    }
}