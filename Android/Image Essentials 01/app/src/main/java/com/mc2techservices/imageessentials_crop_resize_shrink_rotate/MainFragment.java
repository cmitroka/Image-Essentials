package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific.gloBaseImageURL;
import static java.lang.String.valueOf;

public final class MainFragment extends Fragment
        implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {

    // region: Fields and Consts

    private CropDemoPreset mDemoPreset;

    public CropImageView mCropImageView;
    int mQuality;
    // endregion

    /** Returns a new instance of this fragment for the given section number. */
    public static MainFragment newInstance(CropDemoPreset demoPreset) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putString("DEMO_PRESET", "XXX");
        args.putString("DEMO_PRESET", demoPreset.name());
        fragment.setArguments(args);
        return fragment;
    }

    /** Set the image to show for cropping. */
    public void setImageUri(Uri imageUri) {
        //IMPORTANT: This area handles a lot of the undo features
        if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]==null)
        {
            mCropImageView.setImageUriAsync(imageUri);
            return;
        }
        else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("Rotate Clockwise"))
        {
            mCropImageView.rotateImage(270);
        }
        else if (AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction].equals("Rotate Counterclockwise"))
        {
            mCropImageView.rotateImage(270);
        }

    }

    /** Set the options of the crop image view to the given values. */
    public void setCropImageViewOptions(CropImageViewOptions options) {
        //Where all the specifics are setup 2
        mCropImageView.setScaleType(options.scaleType);
        mCropImageView.setCropShape(options.cropShape);
        mCropImageView.setGuidelines(options.guidelines);
        mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
        mCropImageView.setMultiTouchEnabled(options.multitouch);
        mCropImageView.setShowCropOverlay(options.showCropOverlay);
        mCropImageView.setShowProgressBar(options.showProgressBar);
        mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
        mCropImageView.setMaxZoom(options.maxZoomLevel);
        mCropImageView.setFlippedHorizontally(options.flipHorizontally);
        mCropImageView.setFlippedVertically(options.flipVertically);
    }

    /** Set the initial rectangle to use. */
    public void setInitialCropRect() {
        mCropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    /** Reset crop window to initial rectangle. */
    public void resetCropRect() {
        mCropImageView.resetCropRect();
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        //Where all the specifics are setup 1
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        options.flipHorizontally = mCropImageView.isFlippedHorizontally();
        options.flipVertically = mCropImageView.isFlippedVertically();
        ((MainActivity) getActivity()).setCurrentOptions(options);
    }
    @Override
    public void onResume () {
        super.onResume();
        if (AppSpecific.gloInstruction>0)
        {
            Log.e("APP","Frag onResume");
        }
    }

    @Override
    public View onCreateView(

            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        switch (mDemoPreset) {
            case RECT:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            case CIRCULAR:
                rootView = inflater.inflate(R.layout.fragment_main_oval, container, false);
                break;
            case CUSTOMIZED_OVERLAY:
                rootView = inflater.inflate(R.layout.fragment_main_customized, container, false);
                break;
            case MIN_MAX_OVERRIDE:
                rootView = inflater.inflate(R.layout.fragment_main_min_max, container, false);
                break;
            case SCALE_CENTER_INSIDE:
                rootView = inflater.inflate(R.layout.fragment_main_scale_center, container, false);
                break;
            case CUSTOM:
                rootView = inflater.inflate(R.layout.fragment_main_rect, container, false);
                break;
            default:
                throw new IllegalStateException("Unknown preset: " + mDemoPreset);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCropImageView = view.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        updateCurrentCropViewOptions();
        if (savedInstanceState == null) {
            if (mDemoPreset == CropDemoPreset.SCALE_CENTER_INSIDE) {
                mCropImageView.setImageResource(R.drawable.cat_small);
            } else {
                mCropImageView.setImageResource(R.drawable.cat);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //IMPORTANT: These are the actions
        if (item.getItemId() == R.id.main_action_crop) {
            if (AppSpecific.gloBaseImageURL==null)
            {
                Toast.makeText(getActivity(), "You need to load an image first", Toast.LENGTH_SHORT).show();
                return false;
            }
            mCropImageView.getCroppedImage();
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Crop";

            /*
            Rect rect = new Rect(0, 0, 10000, 10000);
            mCropImageView.setCropRect(rect);
            Bitmap a=mCropImageView.getCroppedImage();
            AppSpecific.DoSaveImageToBA(this.getContext(), a);
            */
            //Rect prevRect=mCropImageView.getCropRect();
            //mCropImageView.setCropRect(prevRect);
            Bitmap b=mCropImageView.getCroppedImage();


            if (AppSpecific.gloSettingShape==null) AppSpecific.gloSettingShape="";
            if (AppSpecific.gloSettingShape.equals("OVAL"))
            {
                Bitmap c=CropImage.toOvalBitmap(b);
                AppSpecific.DoSaveImageToBA(this.getContext(), c);
                mCropImageView.setImageBitmap(c);
            }
            else
            {
                AppSpecific.DoSaveImageToBA(this.getContext(), b);
                mCropImageView.setImageBitmap(b);
            }
            return true;
        } else if (item.getItemId() == R.id.main_action_rotate) {
            if (AppSpecific.gloBaseImageURL==null)
            {
                Toast.makeText(getActivity(), "You need to load an image first", Toast.LENGTH_SHORT).show();
                return false;
            }
            //AppSpecific.gloInstruction++;
            //AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Rotate90";
            //mCropImageView.rotateImage(90);
            return true;  //
        }  else if (item.getItemId() == R.id.main_action_resize) {
            if (AppSpecific.gloBaseImageURL==null)
            {
                Toast.makeText(getActivity(), "You need to load an image first", Toast.LENGTH_SHORT).show();
                return false;
            }
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Resize";
            Bitmap b2=AppSpecific.DoGetImageFromBA(this.getContext(), AppSpecific.gloInstruction-1);
            int pHeight=b2.getHeight();
            int pWidth=b2.getWidth();
            CustomDialogResize(valueOf(pWidth), valueOf(pHeight));
            //then does ResizeImage to complete...
        }
        else if (item.getItemId() == R.id.main_action_rotate_clockwise) {
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Rotate Clockwise";
            mCropImageView.rotateImage(90);
            Rect prevRect=mCropImageView.getCropRect();
            Rect rect = new Rect(0, 0, 10000, 10000);
            mCropImageView.setCropRect(rect);
            Bitmap a=mCropImageView.getCroppedImage();
            AppSpecific.DoSaveImageToBA(this.getContext(),a);
            mCropImageView.setCropRect(prevRect);
            return true;
        }  else if (item.getItemId() == R.id.main_action_rotate_cclockwise) {
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="Rotate Counterclockwise";
            mCropImageView.rotateImage(270);
            Rect prevRect=mCropImageView.getCropRect();
            Rect rect = new Rect(0, 0, 10000, 10000);
            mCropImageView.setCropRect(rect);
            Bitmap a=mCropImageView.getCroppedImage();
            AppSpecific.DoSaveImageToBA(this.getContext(),a);
            mCropImageView.setCropRect(prevRect);
            return true;
        }
        else if (item.getItemId() == R.id.main_action_flip_horizontally) {
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="FlipHor";
            mCropImageView.flipImageHorizontally();
            Rect rect = new Rect(0, 0, 10000, 10000);
            mCropImageView.setCropRect(rect);
            Bitmap a=mCropImageView.getCroppedImage();
            AppSpecific.DoSaveImageToBA(this.getContext(),a);
            return true;
        } else if (item.getItemId() == R.id.main_action_flip_vertically) {
            AppSpecific.gloInstruction++;
            AppSpecific.gloInstructionDetail[AppSpecific.gloInstruction]="FlipVer";
            mCropImageView.flipImageVertically();
            Rect rect = new Rect(0, 0, 10000, 10000);
            mCropImageView.setCropRect(rect);
            Bitmap a=mCropImageView.getCroppedImage();
            AppSpecific.DoSaveImageToBA(this.getContext(),a);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void CustomDialogResize(final String pCurrWidth, final String pCurrHeight)
    {
        final Dialog nsp = new Dialog(this.getContext());
        nsp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nsp.setContentView(R.layout.custom_dialog_resize);
        //nsp.setContentView(R.layout.custom_dialog_resize);
        //nsp.setTitle("Welcome to Pocket Cube Solver!");
        final EditText editTextWidth = nsp.findViewById(R.id.editTextWidth);
        final EditText editTextHeight = nsp.findViewById(R.id.editTextHeight);
        EditText editTextDims = nsp.findViewById(R.id.editTextDims);
        final EditText editTextPercentage = nsp.findViewById(R.id.editTextPercentage);
        String pWxH=pCurrWidth + " x " + pCurrHeight;
        final double pW=Double.parseDouble(pCurrWidth);
        final double pH=Double.parseDouble(pCurrHeight);
        editTextDims.setText(pWxH);
        editTextDims.setEnabled(false);
        RadioGroup rg = nsp.findViewById(R.id.radioGroupResize);

        /*
        editTextWidth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextWidth.setText("");
                editTextHeight.setText("");
            }
        });
        editTextHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextWidth.setText("");
                editTextHeight.setText("");
            }
        });
        */

        editTextHeight.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pValIn=s.toString();
            if (pValIn.length()==0)
            {
                String Wtest0=editTextWidth.getText().toString();
                if (!Wtest0.equals(valueOf(pValIn))) editTextWidth.setText("");
                return;
            }
            double pNewH= Double.parseDouble(pValIn);
            double pPctChange=pNewH/pH;
            double pNewW=pW*pPctChange;
            int iNewW = (int) Math.round(pNewW);
            String Wtest=editTextWidth.getText().toString();
            if (!Wtest.equals(valueOf(iNewW)))
            {
                editTextWidth.setText(valueOf(iNewW));
            }
            String Htest=editTextHeight.getText().toString();
            if (!Htest.equals(valueOf(pValIn)))
            {
                editTextHeight.setText(pValIn);
            }

            }
        });

        editTextWidth.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pValIn=s.toString();
                if (pValIn.length()==0)
                {
                    editTextHeight.setText("");
                    return;
                }
                double pNewW= Double.parseDouble(pValIn);
                double pPctChange=pNewW/pW;
                double pNewH=pH*pPctChange;
                int iNewH = (int) Math.round(pNewH);
                String Htest=editTextHeight.getText().toString();
                if (!Htest.equals(valueOf(iNewH)))
                {
                    editTextHeight.setText(valueOf(iNewH));
                }
                String Wtest=editTextWidth.getText().toString();
                if (!Wtest.equals(valueOf(pValIn)))
                {
                    editTextWidth.setText(pValIn);
                }
            }
        });

        editTextPercentage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pValIn=s.toString();
                if (pValIn.length()==0)
                {
                    editTextHeight.setText("");
                    editTextWidth.setText("");
                    return;
                }
                double pActualPct= Double.parseDouble(pValIn)*.01;
                double dCurrHeight=Double.parseDouble(pCurrHeight);
                double pNewH=dCurrHeight*pActualPct;
                double dCurrWidth=Double.parseDouble(pCurrWidth);
                double pNewW=dCurrWidth*pActualPct;
                editTextWidth.setText(valueOf(pNewW));
                editTextHeight.setText(valueOf(pNewH));
            }
        });


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = group.findViewById(checkedId);
                String sID=getResources().getResourceEntryName(checkedId);
                if (sID.equals("rbPercentage")) {
                    Log.e("APP","Selected RadioButton->Lossy");
                    editTextHeight.setEnabled(false);
                    editTextWidth.setEnabled(false);
                    editTextPercentage.setEnabled(true);
                }
                else if (sID.equals("rbPixels")) {
                    Log.e("APP","Selected RadioButton->Lossless WO Undo");
                    editTextHeight.setEnabled(true);
                    editTextWidth.setEnabled(true);
                    editTextPercentage.setEnabled(false);
                }
            }
        });
        Button buttonSave = nsp.findViewById(R.id.buttonSave);
        Button buttonCancel = nsp.findViewById(R.id.buttonCancel);
        RadioButton rbPixels=nsp.findViewById(R.id.rbPixels);
        rbPixels.setChecked(true);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ResizeImage(editTextWidth.getText().toString(),editTextHeight.getText().toString());
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

    private void ResizeImage(String pWidth, String pHeight)
    {
        double dW=Double.parseDouble(pWidth);
        double dH=Double.parseDouble(pHeight);
        int iW = (int) dW;
        int iH = (int) dH;
        Bitmap b3=AppSpecific.DoGetCurrentImageFromBA(this.getContext());
        Bitmap br=ResizeBitmap01(b3,iW,iH);
        AppSpecific.DoSaveImageToBA(this.getContext(),br);
        mCropImageView.setImageBitmap(br);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDemoPreset = CropDemoPreset.valueOf(getArguments().getString("DEMO_PRESET"));
        ((MainActivity) activity).setCurrentFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnCropImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getActivity(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            Intent intent = new Intent(getActivity(), CropResultActivity.class);
            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
            if (result.getUri() != null) {
                intent.putExtra("URI", result.getUri());
            } else {
                CropResultActivity.mImage =
                        mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                                ? CropImage.toOvalBitmap(result.getBitmap())
                                : result.getBitmap();
            }
            startActivity(intent);
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    getActivity(),
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    private  Bitmap ResizeBitmap01(Bitmap originalBitmap, int newWidth, int newHeight)
    {
        Bitmap resizedBitmap=null;
        try {
            resizedBitmap = Bitmap.createScaledBitmap(
                    originalBitmap, newWidth, newHeight, false);
        }
        catch (Exception ex)
        {
            Log.e("APP","ResizeBitmap01 Exception " + ex.getMessage());
        }
        return resizedBitmap;
    }
    public class AsyncTaskRunner extends AsyncTask <Void, Void, Bitmap> {

        private String pName;
        private Bitmap pBitmap;
        private Context pContext;
        ProgressDialog progressDialog;

        AsyncTaskRunner(Context pContextIn, String pNameIn, Bitmap pBitmapIn) {
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
            AppSpecific.DoSaveImage(pContext,pBitmap,"","", mQuality);
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
        }

    }
}
