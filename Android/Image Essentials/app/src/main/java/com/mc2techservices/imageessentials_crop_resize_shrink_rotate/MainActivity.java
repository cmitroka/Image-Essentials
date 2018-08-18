package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.GeneralFunctions01;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class MainActivity extends AppCompatActivity {
    // region: Fields and Consts
    ProgressBar progressBar;
    ImageView testImage;
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private MainFragment mCurrentFragment;

    private Uri mCropImageUri;
    private String baseImageURL;

    private String saveLoc;
    private String saveFile;
    private int saveQuality;

    private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();
    // endregion

    public void setCurrentFragment(MainFragment fragment) {
        mCurrentFragment = fragment;
    }

    public void setCurrentOptions(CropImageViewOptions options) {
        mCropImageViewOptions = options;
        //updateDrawerTogglesByOptions(options);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FeatureInfo[] list = this.getPackageManager()
                .getSystemAvailableFeatures();

        Toast.makeText(this,
                "OpenGL ES Version: " + list[list.length - 1].getGlEsVersion(),
                Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        testImage = findViewById(R.id.testImage);
        progressBar.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle =
                new ActionBarDrawerToggle(
                        this, mDrawerLayout, R.string.main_drawer_open, R.string.main_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            setMainFragmentByPreset(CropDemoPreset.RECT);
        }

    }

    private void setCropViewOptionsFromAppSpecific()
    {
        if (AppSpecific.gloSettingShape.equals("RECT"))
        {
            mCropImageViewOptions.cropShape = CropImageView.CropShape.RECTANGLE;
        }
        else if (AppSpecific.gloSettingShape.equals("OVAL"))
        {
            mCropImageViewOptions.cropShape = CropImageView.CropShape.OVAL;
        }

        if (AppSpecific.gloSettingARLocked.equals("False"))
        {
            mCropImageViewOptions.fixAspectRatio=false;
        }
        else
        {
            mCropImageViewOptions.fixAspectRatio=true;
        }


        SetAspectMode(AppSpecific.gloSettingAR);
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        mCurrentFragment.mCropImageView.setImageBitmap(AppSpecific.DoGetCurrentImageFromBA(this));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        mCurrentFragment.updateCurrentCropViewOptions();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("APP", "Act onResume");
        setCropViewOptionsFromAppSpecific();
        if (AppSpecific.gloInstruction > 0) {
            //mCurrentFragment.mCropImageView.setImageBitmap(AppSpecific.gloBitmaps[AppSpecific.gloInstruction]);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            if (AppSpecific.gloInstruction==-1)
            {
                finish();
                return false;
            }
            String pWhatToDo = AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction];
            if (AppSpecific.gloInstruction > 1) {
                Log.d("APP", "Restoring profile" + String.valueOf(AppSpecific.gloInstruction));
                if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]=="Set Image")
                {
                    //Nothing we can really do here...
                }
                else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("Rotate Clockwise"))
                {
                    mCurrentFragment.mCropImageView.rotateImage(270);
                }
                else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("Rotate Counterclockwise"))
                {
                    mCurrentFragment.mCropImageView.rotateImage(90);
                }
                else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("FlipVer"))
                {
                    mCurrentFragment.mCropImageView.flipImageVertically();
                }
                else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("FlipHor"))
                {
                    mCurrentFragment.mCropImageView.flipImageHorizontally();
                }
                else
                {
                }
                Bitmap b = AppSpecific.DoUndo(this);
                mCurrentFragment.mCropImageView.setImageBitmap(b);
                //then would need to replay the steps from when the last image was loaded to right before this

                mCurrentFragment.setHasOptionsMenu(true);
                AppSpecific.gloInstruction--;
            } else if (AppSpecific.gloInstruction==1) {
                //Alert we're exiting...
                Toast toast = Toast.makeText(this, "Tap back again to exit.", Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (mCurrentFragment != null && mCurrentFragment.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external
            // storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and
            // see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;

                baseImageURL=imageUri.toString();
                AppSpecific.gloBaseImageURL=baseImageURL;
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                //mCurrentFragment.setImageUri(imageUri);
                //IMPORTANT:  This is when a picture is first selected
                //Start
                baseImageURL=imageUri.toString();
                AppSpecific.gloBaseImageURL=baseImageURL;
                Log.d("APP", "Beginning... ");
                AppSpecific.gloInstruction = 1;
                AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Set Image";
                Bitmap mImage = null;
                try {
                    mImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
                //AsyncTaskRunner task = new AsyncTaskRunner(this, "", mImage);
                //task.execute();
                int mImageH=mImage.getHeight();
                int mImageW=mImage.getWidth();
                double dScale=1.0;
                if (mImageW>mImageH)
                {
                    if (mImageW>AppSpecific.gloMaxBitmapSize)
                    {
                        dScale=(double)AppSpecific.gloMaxBitmapSize/(double)mImageW;
                    }
                }
                else
                {
                    if (mImageH>AppSpecific.gloMaxBitmapSize)
                    {
                        dScale=(double)AppSpecific.gloMaxBitmapSize/(double)mImageH;
                    }
                }
                double dImageW=(double)mImageW*dScale;
                double dImageH=(double)mImageH*dScale;
                Bitmap scaled = Bitmap.createScaledBitmap(mImage, (int)dImageW, (int)dImageH, true);
                AppSpecific.DoSaveImageToBA(this, scaled);
                mCurrentFragment.mCropImageView.setImageBitmap(scaled);

                //Uri tempImg=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(AppSpecific.gloInstruction+".jpg")));
                //mCurrentFragment.setImageUri(tempImg);
                Log.d("APP", "Ending... ");
                //End
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                        .show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null
                    && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCurrentFragment.setImageUri(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @SuppressLint("NewApi")
    public void onDrawerOptionClicked(View view) {
        switch (view.getId()) {
            case R.id.drawer_option_load:
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(this);
                }
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_save_as:
                if (AppSpecific.gloBaseImageURL==null)
                {
                    ShowToast("You need to load an image first");
                }
                else
                {
                    ShowCustomDialogChooseLocation();
                }
                mDrawerLayout.closeDrawers();
                break;
                //ShowCustomDialogChooseMode();
            case R.id.drawer_option_oval:
                //setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                AppSpecific.gloSettingShape = "OVAL";
                mCropImageViewOptions.cropShape = CropImageView.CropShape.OVAL;
                mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
                mCropImageViewOptions.fixAspectRatio = false;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                mCurrentFragment.mCropImageView.setImageBitmap(AppSpecific.DoGetCurrentImageFromBA(this));
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_rect:
                //setMainFragmentByPreset(CropDemoPreset.RECT);
                AppSpecific.gloSettingShape = "RECT";
                mCropImageViewOptions.cropShape = CropImageView.CropShape.RECTANGLE;
                mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
                mCropImageViewOptions.fixAspectRatio = false;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                mCurrentFragment.mCropImageView.setImageBitmap(AppSpecific.DoGetCurrentImageFromBA(this));
                //Followed by
                mDrawerLayout.closeDrawers();
                break;

            case R.id.drawer_option_ar:
                ShowCustomDialogAspectRatio();
            default:
                Toast.makeText(this, "Unknown drawer option clicked", Toast.LENGTH_LONG).show();
        }
    }


    private void ShowCustomDialogAspectRatio() {
        final Dialog nsp = new Dialog(this);
        nsp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nsp.setContentView(R.layout.custom_dialog_aspect_ratio);
        //nsp.setTitle("Welcome to Pocket Cube Solver!");
        final TextView tv = nsp.findViewById(R.id.textViewAspectRatio);
        final String temp;
        RadioGroup rg = (RadioGroup) nsp.findViewById(R.id.radioGroupAspectRatio);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = group.findViewById(checkedId);
                String sID = getResources().getResourceEntryName(checkedId);
                if (sID.equals("rbFree")) {
                    tv.setText("Free");
                    Log.e("APP", "RadioGroup RB Hit");
                } else if (sID.equals("rbOneOne")) {
                    tv.setText("1:1");
                    Log.e("APP", "RadioGroup RB Hit");
                } else if (sID.equals("rbFourThree")) {
                    tv.setText("4:3");
                    Log.e("APP", "RadioGroup RB Hit");
                } else if (sID.equals("rbNineSixteen")) {
                    tv.setText("9:16");
                    Log.e("APP", "RadioGroup RB Hit");
                } else if (sID.equals("rbSixteenNine")) {
                    tv.setText("16:9");
                    Log.e("APP", "RadioGroup RB Hit");
                }
            }
        });
        Button buttonSave = nsp.findViewById(R.id.buttonSave);
        Button buttonCancel = nsp.findViewById(R.id.buttonCancel);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAspectMode(tv.getText().toString());
                nsp.dismiss();
                //finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                nsp.dismiss();
            }
        });
        nsp.show();
    }

    private void SetAspectMode(String pAspectRatioIn) {
        mCropImageViewOptions.fixAspectRatio = true;
        if (pAspectRatioIn.equals("Free")) {
            mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
            mCropImageViewOptions.fixAspectRatio = false;
        } else if (pAspectRatioIn.equals("1:1")) {
            mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
        } else if (pAspectRatioIn.equals("4:3")) {
            mCropImageViewOptions.aspectRatio = new Pair<>(4, 3);
        } else if (pAspectRatioIn.equals("9:16")) {
            mCropImageViewOptions.aspectRatio = new Pair<>(9, 16);
        } else if (pAspectRatioIn.equals("16:9")) {
            mCropImageViewOptions.aspectRatio = new Pair<>(16, 9);
        } else {
            Toast toast = Toast.makeText(this, "Unsure of AR...", Toast.LENGTH_LONG);
            mCropImageViewOptions.fixAspectRatio = false;
        }
        GeneralFunctions01.Cfg.WriteSharedPreference(this, "SettingAR", pAspectRatioIn);
        //Where all the specifics are setup 2
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        mDrawerLayout.closeDrawers();
        //mCurrentFragment.mCropImageView.setImageBitmap(AppSpecific.DoGetCurrentImageFromBA(this));

    }

    private void ShowCustomDialogChooseLocation() {
        final Dialog nsp = new Dialog(this);
        nsp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nsp.setContentView(R.layout.custom_dialog_choose_location);
        saveLoc="";
        saveFile="";
        saveQuality=0;
        final String temp;
        RadioGroup rg = (RadioGroup) nsp.findViewById(R.id.radioGroupChooseLocation);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String sID = getResources().getResourceEntryName(checkedId);
                //xxx
                if (sID.equals("rbDocuments")) {
                    saveLoc="Documents";
                } else if (sID.equals("rbPictures")) {
                    saveLoc="Pictures";
                } else if (sID.equals("rbDownload")) {
                    saveLoc="Download";
                } else if (sID.equals("rbDCIM")) {
                    saveLoc="DCIM";
                }
                //nsp.dismiss();
            }
        });

        final EditText editTextNameOfFile = nsp.findViewById(R.id.editTextNameOfFile);

        Button buttonSave = nsp.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveLoc.length()==0)
                {
                    ShowToast("Save Error: You have to specify a location.");
                    return;
                }

                saveFile=editTextNameOfFile.getText().toString();
                boolean pTestRes=TestValidity(saveLoc, saveFile);
                if (pTestRes)
                {
                    if (saveQuality==0)
                    {
                        ShowToast("Save Error: You have to specify a quality.");
                    }
                    else
                    {
                        SaveItAs(saveLoc, saveFile);
                        nsp.dismiss();
                    }
                }
                else
                {
                    ShowToast("Save Error: Invalid File Name.  Please change it and try again.");
                }
            }
        });


        RadioGroup rgc = nsp.findViewById(R.id.radioGroupCompression);
        rgc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String sID = getResources().getResourceEntryName(checkedId);
                //xxx
                if (sID.equals("rbLossless")) {
                    saveQuality=100;
                } else if (sID.equals("rbCompressed")) {
                    ShowCustomDialogChooseQuality();
                }
                //nsp.dismiss();
            }
        });

        nsp.show();
    }

    private void ShowToast(String pMsg)
    {
        Toast toast = Toast.makeText(this, pMsg, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean TestValidity(String pSaveToLocation, String pName)
    {
        boolean retVal=false;
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/"+pSaveToLocation);
        boolean isPresent = true;
        if (!docsFolder.exists()) {
            isPresent = docsFolder.mkdir();
        }
        if (isPresent) {
            try {
                File file = new File(docsFolder.getAbsolutePath(),pName);
                file.delete();
                if(file.createNewFile())
                {
                    System.out.println("Success!");
                    file.delete();
                    retVal=true;
                }
                else
                {
                    System.out.println ("Error, file already exists.");
                }
                //file.delete();
            }
            catch (Exception ex)
            {

            }
        }
        return retVal;
    }

    public void ShowCustomDialogChooseQuality() {
        final Dialog nsp = new Dialog(this);
        nsp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nsp.setContentView(R.layout.custom_dialog_enter_quality);
        Button buttonCancelQuality= nsp.findViewById(R.id.buttonCancelQuality);
        Button buttonContinuelQuality = nsp.findViewById(R.id.buttonContinuelQuality);
        final EditText editTextQuality = nsp.findViewById(R.id.editTextQuality);
        buttonCancelQuality.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   nsp.dismiss();
               }
           });
        buttonContinuelQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String validNum=editTextQuality.getText().toString();
                int iValidNum=GeneralFunctions01.Conv.StringToInt(validNum);
                if (iValidNum>0 && iValidNum<100)
                {
                    saveQuality=iValidNum;
                    nsp.dismiss();
                }
                else
                {
                    ShowToast("Enter a number between 1 and 99, or cancel.");
                }
                //task.execute();
                //finish();
            }
        });
        nsp.show();
    }
    public void ShowCustomDialogChooseMode() {
        //if (!EntryPoint.equals("DLArea")) finish();
        final Dialog nsp = new Dialog(this);
        nsp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nsp.setContentView(R.layout.custom_dialog_choose_mode);
        //nsp.setTitle("Welcome to Pocket Cube Solver!");
        RadioGroup rg = (RadioGroup) nsp.findViewById(R.id.radioGroupMode);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = (RadioButton) group.findViewById(checkedId);
                String sID = getResources().getResourceEntryName(checkedId);
                if (sID.equals("Lossy")) {
                    Log.e("APP", "Selected RadioButton->Lossy");
                } else if (sID.equals("rbLosslessWOUndo")) {
                    Log.e("APP", "Selected RadioButton->Lossless WO Undo");
                } else if (sID.equals("rbLosslessWUndo")) {
                    Log.e("APP", "Selected RadioButton->Lossless W Undo");
                }
            }
        });
        Button buttonSave = nsp.findViewById(R.id.buttonSave);
        Button buttonCancel = nsp.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nsp.dismiss();

                //task.execute();
                //finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                nsp.dismiss();
            }
        });
        nsp.show();
    }

    private void SaveIt()
    {
        Rect rect = new Rect(0, 0, 10000, 10000);
        mCurrentFragment.mCropImageView.setCropRect(rect);
        Bitmap a=mCurrentFragment.mCropImageView.getCroppedImage();
        AsyncTaskRunner task = new AsyncTaskRunner(this, "", baseImageURL, a);
        task.execute();
    }
    private void SaveItAs(String pLocation, String pFullPathAndName)
    {
        Rect rect = new Rect(0, 0, 10000, 10000);
        mCurrentFragment.mCropImageView.setCropRect(rect);
        Bitmap a=mCurrentFragment.mCropImageView.getCroppedImage();
        AsyncTaskRunner task = new AsyncTaskRunner(this, pLocation, pFullPathAndName, a);
        task.execute();
    }
    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(demoPreset))
                .commit();

    }
    public class AsyncTaskRunner extends AsyncTask <Void, Void, Bitmap> {

        private String pName;
        private String pLocation;
        private Bitmap pBitmap;
        private Context pContext;
        ProgressDialog progressDialog;

        AsyncTaskRunner(Context pContextIn, String pSaveToLocation, String pFullPathAndName, Bitmap pBitmapIn) {
            pName = Uri.parse(pFullPathAndName).getLastPathSegment();
            pLocation = pSaveToLocation;
            pBitmap = pBitmapIn;
            MainActivity.AsyncTaskRunner.this.pContext = pContextIn;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(pContext,
                    "Saving...",
                    "Waiting...");
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            AppSpecific.DoSaveImage(pContext,pBitmap,pLocation,pName, saveQuality);
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
            Toast toast = Toast.makeText(pContext, "Save Complete!", Toast.LENGTH_LONG);
            toast.show();
        }

    }
}