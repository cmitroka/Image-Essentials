package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.GeneralFunctions01;

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


    }
    private void SwitchScreens()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
