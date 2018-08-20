package com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 5/12/2018.
 */

public class AppSpecific {
    public static String gloUUID;
    public static String gloxmlns;
    public static String gloWebServiceURL;
    public static String gloBaseImageURL;
    public static String gloSettingAR;
    public static String gloSettingShape;
    public static String gloSettingARLocked;
    public static int gloMaxBitmapSize;


    public static String gloLD;
    public static String gloPD;
    public static int gloInstruction;   //This will be needed for the 'Undo' feature, should it ever be coded
    public static String[] gloInstructionDetail=new String[99];   //This will be needed for the 'Undo' feature, should it ever be coded
    public static Bitmap[] gloBitmaps=new Bitmap[99];   //This will be needed for the 'Undo' feature, should it ever be coded

    public static String gloInternalStorageImgDir;

    public static int gloDelay;
    public static void DoSaveImageToBA(Context c, Bitmap b)
    {
        gloBitmaps[gloInstruction]=b;
    }

    public static Bitmap DoGetCurrentImageFromBA(Context c)
    {
        if (gloInstruction<0) return null;
        Bitmap a=gloBitmaps[gloInstruction];
        if (a==null)
        {
            Log.e("APP","There's a problem here...");
        }
        Bitmap bmpcopy = a.copy(a.getConfig(), true);
        gloBitmaps[gloInstruction]=bmpcopy.copy(bmpcopy.getConfig(), true);
        return a;
    }
    public static Bitmap DoGetImageFromBA(Context c, int pIndex)
    {
        Bitmap a=gloBitmaps[pIndex];
        if (a==null)
        {
            Log.e("APP","There's a problem here...");
            return null;
        }
        Bitmap bmpcopy = a.copy(a.getConfig(), true);
        gloBitmaps[gloInstruction]=bmpcopy.copy(bmpcopy.getConfig(), true);
        return a;
    }

    public static Bitmap DoGetPriorImageFromBA(Context c)
    {
        if (gloInstruction<0) return null;
        int tempIns=gloInstruction-1;
        Bitmap a=gloBitmaps[tempIns];
        Bitmap bmpcopy = a.copy(a.getConfig(), true);
        gloBitmaps[tempIns]=bmpcopy.copy(bmpcopy.getConfig(), true);
        return a;
    }
    public static Bitmap DoUndo(Context c)
    {
        if (gloInstruction<0) return null;

        /*
        for (int x = gloInstruction-1; x > 0; x--)
        {
            if (gloBitmaps[x]!=null)
            {
                //This is our prior image; load this.
                pWhatToUse=x;
                System.out.println("Value of x:" + x);
                break;
            }
        }
        */
        int WhatToUse=gloInstruction-1;
        Bitmap a=gloBitmaps[WhatToUse];
        //then would need to replay the steps from when the last image was loaded to right before this
        return a;

    }
    public static String DoSaveImage(Context c, Bitmap bitmapImage, String pSaveToLocation, String pNameOfFile, Integer pQuality){
        String retVal="";
        File docsFolder;
        FileOutputStream fos = null;
        boolean isPresent = true;
        if (pNameOfFile.equals("")) pNameOfFile="tempimg";
        //String gptest=getPath(c, Uri.parse(pPath));
        if (pSaveToLocation.equals(""))
        {
            ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
            docsFolder = cw.getDir("tempImageDir", MODE_PRIVATE);
        }
        else {
            docsFolder = new File(Environment.getExternalStorageDirectory() + "/" + pSaveToLocation + "/ImageEssentialSaves");
            if (!docsFolder.exists()) {
                isPresent = docsFolder.mkdir();
            }
        }
        try {
            if (pQuality==100)
            {
                fos = new FileOutputStream(docsFolder+"/" + pNameOfFile+".png");
                bitmapImage.compress(Bitmap.CompressFormat.PNG, pQuality, fos);
            }
            else
            {
                fos = new FileOutputStream(docsFolder+"/" + pNameOfFile+".jpg");
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, pQuality, fos);
            }
            retVal="file://"+docsFolder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            retVal="";
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
        //return directory.getAbsolutePath();
    }


    // **************************************OLD CODE***********************************************
    /*
    public static void SetIntStorageDir(Context c)
    {
        String retVal="";
        ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        gloInternalStorageImgDir="file://"+String.valueOf(directory.getAbsolutePath());
    }

    public static Bitmap DoGetCurrentImage()
    {
        if (gloInstruction<0) return null;
        Uri tempUri=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(gloInstruction+".jpg")));
        Bitmap b=DoLoadImageFromStorage(tempUri.toString());
        return b;
    }
    public static Uri DoGetNextImageUri()
    {

        gloInstruction++;
        Uri tempUri=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(gloInstruction+".jpg")));
        return tempUri;
    }
    public static Uri DoGetCurrentImageUri()
    {
        if (gloInstruction<0) return null;
        Uri tempUri=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(gloInstruction+".jpg")));
        return tempUri;
    }
    public static Bitmap DoGetCurrentImage01(Context c)
    {
        if (gloInstruction<0) return null;
        Uri tempUri=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(gloInstruction+".jpg")));
        Bitmap a=DoUriToBitmap00(c,tempUri);
        return a;
    }

    public static void DoSaveImage(Context c, Bitmap b)
    {
        gloInstruction++;
        //String sFilename="/profile"+String.valueOf(gloInstruction+".jpg");
        String sFilename="/profile"+String.valueOf(gloInstruction);
        gloBitmaps[gloInstruction]=b;
        //DoSaveToInternalStorage(c, b,sFilename);
    }
    public static Bitmap DoUndo(Context c)
    {
        gloInstruction--;
        if (AppSpecific.gloInstruction<0)
        {
            return null;
        }
        //Uri temp2=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(AppSpecific.gloInstruction+".jpg")));
        //Bitmap b = DoUriToBitmap00(c, temp2);
        Bitmap b = gloBitmaps[gloInstruction];
        return b;
    }
    public static Bitmap DoUriToBitmap(Context c, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {

                image_stream = c.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap DoUriToBitmap00(Context c, Uri sendUri) {
        Bitmap getBitmap = null;
            try {
                //Uri imageUri = data.getData();
                getBitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), sendUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return getBitmap;
    }

    public static String DoSaveToInternalStorage(Context c, Bitmap bitmapImage, String pNameOfFile){
        String retVal="";
        ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,pNameOfFile+".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            retVal="file://"+mypath.toString();
        } catch (Exception e) {
            e.printStackTrace();
            retVal="";
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
        //return directory.getAbsolutePath();
    }
    public static Bitmap DoLoadImageFromStorage(String path)
    {
        //UNTESTED
        Bitmap b=null;
        try {
            File f=new File(path, "");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
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
    */
}