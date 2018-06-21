package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.GeneralFunctions01;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    // region: Fields and Consts
    ProgressBar progressBar;
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private MainFragment mCurrentFragment;

    private Uri mCropImageUri;

    private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();
    // endregion

    public void setCurrentFragment(MainFragment fragment) {
        mCurrentFragment = fragment;
    }

    public void setCurrentOptions(CropImageViewOptions options) {
        mCropImageViewOptions = options;
        updateDrawerTogglesByOptions(options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progressBar);
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
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        mCurrentFragment.updateCurrentCropViewOptions();

    }
    @Override
    public void onResume () {
        super.onResume();
        //IMPORTANT:  This always hit
        /*
        Uri pLoadMe=AppSpecific.DoGetCurrentImageUri();
        if (pLoadMe==null)
        {
            Toast.makeText(this, "No image to work with", Toast.LENGTH_LONG);
            return;
        }
        //Uri temp2=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(AppSpecific.gloInstruction+".jpg")));
        mCurrentFragment.setImageUri(pLoadMe);
        */
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            String pWhatToDo=AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction];
            if (AppSpecific.gloInstruction>0)
            {
                Log.d("APP", "Restoring profile"+String.valueOf(AppSpecific.gloInstruction));
                Uri temp2=(Uri.parse(AppSpecific.gloInternalStorageImgDir+"/profile"+String.valueOf(AppSpecific.gloInstruction+".jpg")));
                //mCurrentFragment.setImageUri(temp2);
                Bitmap b = AppSpecific.DoUndo(this);
                mCurrentFragment.mCropImageView.setImageBitmap(b);
            }
            else
            {
                //Alert we're exiting...
                Toast toast = Toast.makeText(this, "At the beginning...", Toast.LENGTH_LONG);
                View view = toast.getView();
                //view.setBackgroundResource(R.drawable.ic_launcher_background);
                TextView text = (TextView) view.findViewById(android.R.id.message);
                /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                text.setTextColor(Color.parseColor("#000000"));
                toast.show();
                //finish();
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
                requestPermissions(
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                //mCurrentFragment.setImageUri(imageUri);
                //IMPORTANT:  This is when a picture is first selected
                //Start
                Log.d("APP", "Beginning... ");
                AppSpecific.gloInstruction=0;
                String sInst="0";
                Bitmap mImage=null;
                try{
                    mImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch(IOException ioEx) {
                    ioEx.printStackTrace();
                }
                //AppSpecific.DoSaveImage(this,mImage);
                AsyncTaskRunner task = new AsyncTaskRunner(this, "", mImage);
                task.execute();
                mCurrentFragment.mCropImageView.setImageBitmap(mImage);

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
                            new String[] {Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(this);
                }
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_select_mode:
                ChooseMode();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_savepic:
                setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_loadpic:
                setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                mDrawerLayout.closeDrawers();
                break;

            case R.id.drawer_option_oval:
                setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_rect:
                setMainFragmentByPreset(CropDemoPreset.RECT);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_customized_overlay:
                setMainFragmentByPreset(CropDemoPreset.CUSTOMIZED_OVERLAY);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_min_max_override:
                setMainFragmentByPreset(CropDemoPreset.MIN_MAX_OVERRIDE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_scale_center:
                setMainFragmentByPreset(CropDemoPreset.SCALE_CENTER_INSIDE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_toggle_scale:
                mCropImageViewOptions.scaleType =
                        mCropImageViewOptions.scaleType == CropImageView.ScaleType.FIT_CENTER
                                ? CropImageView.ScaleType.CENTER_INSIDE
                                : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER_INSIDE
                                ? CropImageView.ScaleType.CENTER
                                : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER
                                ? CropImageView.ScaleType.CENTER_CROP
                                : CropImageView.ScaleType.FIT_CENTER;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_shape:
                mCropImageViewOptions.cropShape =
                        mCropImageViewOptions.cropShape == CropImageView.CropShape.RECTANGLE
                                ? CropImageView.CropShape.OVAL
                                : CropImageView.CropShape.RECTANGLE;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_guidelines:
                mCropImageViewOptions.guidelines =
                        mCropImageViewOptions.guidelines == CropImageView.Guidelines.OFF
                                ? CropImageView.Guidelines.ON
                                : mCropImageViewOptions.guidelines == CropImageView.Guidelines.ON
                                ? CropImageView.Guidelines.ON_TOUCH
                                : CropImageView.Guidelines.OFF;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_aspect_ratio:
                if (!mCropImageViewOptions.fixAspectRatio) {
                    mCropImageViewOptions.fixAspectRatio = true;
                    mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
                } else {
                    if (mCropImageViewOptions.aspectRatio.first == 1
                            && mCropImageViewOptions.aspectRatio.second == 1) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(4, 3);
                    } else if (mCropImageViewOptions.aspectRatio.first == 4
                            && mCropImageViewOptions.aspectRatio.second == 3) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(16, 9);
                    } else if (mCropImageViewOptions.aspectRatio.first == 16
                            && mCropImageViewOptions.aspectRatio.second == 9) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(9, 16);
                    } else {
                        mCropImageViewOptions.fixAspectRatio = false;
                    }
                }
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_auto_zoom:
                mCropImageViewOptions.autoZoomEnabled = !mCropImageViewOptions.autoZoomEnabled;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_max_zoom:
                mCropImageViewOptions.maxZoomLevel =
                        mCropImageViewOptions.maxZoomLevel == 4
                                ? 8
                                : mCropImageViewOptions.maxZoomLevel == 8 ? 2 : 4;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_set_initial_crop_rect:
                mCurrentFragment.setInitialCropRect();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_reset_crop_rect:
                mCurrentFragment.resetCropRect();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.drawer_option_toggle_multitouch:
                mCropImageViewOptions.multitouch = !mCropImageViewOptions.multitouch;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_show_overlay:
                mCropImageViewOptions.showCropOverlay = !mCropImageViewOptions.showCropOverlay;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            case R.id.drawer_option_toggle_show_progress_bar:
                mCropImageViewOptions.showProgressBar = !mCropImageViewOptions.showProgressBar;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                updateDrawerTogglesByOptions(mCropImageViewOptions);
                break;
            default:
                Toast.makeText(this, "Unknown drawer option clicked", Toast.LENGTH_LONG).show();
        }
    }
    public void ChooseMode()
    {
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
                String sID=getResources().getResourceEntryName(checkedId);
                if (sID.equals("Lossy")) {
                    Log.e("APP","Selected RadioButton->Lossy");
                }
                else if (sID.equals("rbLosslessWOUndo")) {
                    Log.e("APP","Selected RadioButton->Lossless WO Undo");
                }
                else if (sID.equals("rbLosslessWUndo")) {
                    Log.e("APP","Selected RadioButton->Lossless W Undo");
                }
            }
        });
        Button buttonSave = nsp.findViewById(R.id.buttonSave);
        Button buttonCancel = nsp.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(demoPreset))
                .commit();
    }

    private void updateDrawerTogglesByOptions(CropImageViewOptions options) {
        ((TextView) findViewById(R.id.drawer_option_toggle_scale))
                .setText(
                        getResources()
                                .getString(R.string.drawer_option_toggle_scale, options.scaleType.name()));
        ((TextView) findViewById(R.id.drawer_option_toggle_shape))
                .setText(
                        getResources()
                                .getString(R.string.drawer_option_toggle_shape, options.cropShape.name()));
        ((TextView) findViewById(R.id.drawer_option_toggle_guidelines))
                .setText(
                        getResources()
                                .getString(R.string.drawer_option_toggle_guidelines, options.guidelines.name()));
        ((TextView) findViewById(R.id.drawer_option_toggle_multitouch))
                .setText(
                        getResources()
                                .getString(
                                        R.string.drawer_option_toggle_multitouch,
                                        Boolean.toString(options.multitouch)));
        ((TextView) findViewById(R.id.drawer_option_toggle_show_overlay))
                .setText(
                        getResources()
                                .getString(
                                        R.string.drawer_option_toggle_show_overlay,
                                        Boolean.toString(options.showCropOverlay)));
        ((TextView) findViewById(R.id.drawer_option_toggle_show_progress_bar))
                .setText(
                        getResources()
                                .getString(
                                        R.string.drawer_option_toggle_show_progress_bar,
                                        Boolean.toString(options.showProgressBar)));

        String aspectRatio = "FREE";
        if (options.fixAspectRatio) {
            aspectRatio = options.aspectRatio.first + ":" + options.aspectRatio.second;
        }
        ((TextView) findViewById(R.id.drawer_option_toggle_aspect_ratio))
                .setText(getResources().getString(R.string.drawer_option_toggle_aspect_ratio, aspectRatio));

        ((TextView) findViewById(R.id.drawer_option_toggle_auto_zoom))
                .setText(
                        getResources()
                                .getString(
                                        R.string.drawer_option_toggle_auto_zoom,
                                        options.autoZoomEnabled ? "Enabled" : "Disabled"));
        ((TextView) findViewById(R.id.drawer_option_toggle_max_zoom))
                .setText(
                        getResources().getString(R.string.drawer_option_toggle_max_zoom, options.maxZoomLevel));
    }

    public class AsyncTaskRunner extends AsyncTask <Void, Void, Bitmap>{

        private String pName;
        private Bitmap pBitmap;
        private Context pContext;
        ProgressDialog progressDialog;

        AsyncTaskRunner(Context pContextIn, String pNameIn, Bitmap pBitmapIn)
        {
            pName = pNameIn;
            pBitmap = pBitmapIn;
            AsyncTaskRunner.this.pContext = pContextIn;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(pContext,
                    "Saving...",
                    "Waiting...");
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            AppSpecific.DoSaveImage(pContext,pBitmap);
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
        }
    }
}
