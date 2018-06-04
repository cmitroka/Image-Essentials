package com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 5/12/2018.
 */

public class AppSpecific {
    public static String gloUUID;
    public static String gloxmlns;
    public static String gloWebServiceURL;
    public static String gloLD;
    public static String gloPD;
    public static int gloInstruction;   //This will be needed for the 'Undo' feature, should it ever be coded
    public static String[] gloInstructionDetail=new String[99];   //This will be needed for the 'Undo' feature, should it ever be coded

    public static String gloInternalStorageImgDir;

    public static int gloDelay;

    public static void SetIntStorageDir(Context c)
    {
        String retVal="";
        ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        gloInternalStorageImgDir="file://"+String.valueOf(directory.getAbsolutePath());
    }

    public static String GetLastKnownBalance(Context c)
    {
        String pBalanceMsg="";
        //String pL4ETValue=editTextLast4.getText().toString();
        //String pL4LastKnowBalance= GeneralFunctions01.Cfg.ReadSharedPreference(this, pL4ETValue);
        String pL4LastKnowBalance= GeneralFunctions01.Cfg.ReadSharedPreference(c, "CardBal");
        if (pL4LastKnowBalance.length()==0)
        {
            pBalanceMsg="Looks like you've never retrieved your balance yet.  Once you do this, you'll be able to see the value here."  ;
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Balance Info");
            builder.setMessage(pBalanceMsg);
            builder.setNeutralButton("Got It", dialogClickListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            return pL4LastKnowBalance;
        }
        return "0";
    }

    public static void UpdateBalance(Context c, String pNewBal)
    {
        GeneralFunctions01.Cfg.WriteSharedPreference(c, "CardBal",pNewBal);
    }

}