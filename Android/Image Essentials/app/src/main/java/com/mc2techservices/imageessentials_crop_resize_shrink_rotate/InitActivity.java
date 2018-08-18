package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.GeneralFunctions01;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        Init();
        SwitchScreens();
    }

    private void Init()
    {
        //AppSpecific.gloxmlns= "xmlns=\"proto.mc2techservices.com\">";
        AppSpecific.gloxmlns= "proto.mc2techservices.com";
        AppSpecific.gloLD="XZQX";
        AppSpecific.gloPD="~_~";
        AppSpecific.gloxmlns= "xmlns=\"proto.mc2techservices.com\">";
        String pURL="http://proto.mc2techservices.com/";
        AppSpecific.gloWebServiceURL=pURL + "GCCA.asmx";
        AppSpecific.gloInstruction=-1;
        //AppSpecific.SetIntStorageDir(this);
        if (GeneralFunctions01.Cfg.ReadSharedPreference(this, "UUID").equals(""))
        {
            String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (android_id==null) android_id="";
            if (android_id.length()<7) android_id=GeneralFunctions01.Text.GetRandomString("ANF", 8);
            GeneralFunctions01.Cfg.WriteSharedPreference(this, "UUID",android_id);
            GeneralFunctions01.Cfg.WriteSharedPreference(this,"Delay","5");
        }
        AppSpecific.gloUUID= GeneralFunctions01.Cfg.ReadSharedPreference(this,"UUID");
        AppSpecific.gloSettingShape= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingShape");
        if (AppSpecific.gloSettingShape.equals(""))
        {
            //Initial Value
            GeneralFunctions01.Cfg.WriteSharedPreference(this,"SettingShape","RECT");
            AppSpecific.gloSettingShape= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingShape");
        }
        AppSpecific.gloSettingAR= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingAR");
        if (AppSpecific.gloSettingAR.equals(""))
        {
            //Initial Value
            GeneralFunctions01.Cfg.WriteSharedPreference(this,"SettingAR","1:1");
            AppSpecific.gloSettingAR= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingAR");
        }

        AppSpecific.gloSettingARLocked= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingARLocked");
        if (AppSpecific.gloSettingARLocked.equals(""))
        {
            //Initial Value
            GeneralFunctions01.Cfg.WriteSharedPreference(this,"SettingARLocked","False");
            AppSpecific.gloSettingARLocked= GeneralFunctions01.Cfg.ReadSharedPreference(this,"SettingARLocked");
        }
        AppSpecific.gloMaxBitmapSize=getMaxTextureSize();



    }
    private void SwitchScreens()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }

}
