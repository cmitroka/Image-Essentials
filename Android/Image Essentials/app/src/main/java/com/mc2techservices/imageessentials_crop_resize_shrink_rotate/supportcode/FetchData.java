package com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchData extends AsyncTask<String, String, String> {
    HttpURLConnection con;
    String pFDcaller;
    String pFDurl;
    String pFDparams;
    String pFDxmlns;
    int pFDtimeout;
    FetchDataCallbackInterface callbackInterface;
    /**
     * Constructor
     * @param pFDurl
     * @param pFDparams
     * @param pFDxmlns
     * @param pFDtimeout
     * @param callbackInterface class which defines the callback method
     */
    public FetchData(String pFDcaller, String pFDurl, String pFDparams, String pFDxmlns, int pFDtimeout, FetchDataCallbackInterface callbackInterface) {
        this.pFDcaller=pFDcaller;
        this.pFDurl = pFDurl;
        this.pFDparams=pFDparams;
        this.pFDxmlns=pFDxmlns;
        this.pFDtimeout=pFDtimeout;
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected String doInBackground(String... args) {
        String resp=null;
        publishProgress("Sleeping..."); // Calls onProgressUpdate()
        try {
            String url = this.pFDurl;
            String urlParameters = this.pFDparams;
            URL obj = new URL(url);
            con = (HttpURLConnection) obj
                    .openConnection();
            con.setConnectTimeout(pFDtimeout*1000);
            con.setReadTimeout(pFDtimeout*1000);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Chrome");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);
            Log.d("FetchData", "Ready to send...: " + url + urlParameters);

            try {
                Log.d("FetchData", "NOTE: If you changed WiFi, this will time out!  Reconnect.");
                DataOutputStream wr = new DataOutputStream(
                        con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            } catch (Exception e) {
                Log.d("FetchData", "Error 001");
                Log.d("FetchData", e.getMessage());
            }

            int responseCode = con.getResponseCode();

            Log.d("FetchData",
                    "\nSending 'POST' request to URL : " + url);
            Log.d("FetchData", "Post parameters : "
                    + urlParameters);
            Log.d("FetchData", "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            Log.d("FetchData", response.toString());
            resp = response.toString();
        } catch (Exception e) {
            Log.d("FetchData", "Error 002");
            Log.d("FetchData", e.getMessage());
        }
        return resp;
    }
    @Override
    protected void onPostExecute(String result) {
        String pCleanResult=GetResonseData(result);
        super.onPostExecute(pCleanResult);
        // pass the result to the callback function
        this.callbackInterface.fetchDataCallback(this.pFDcaller, pCleanResult);
    }
    private String GetResonseData(String DataIn)
    {
        if (DataIn==null) return null;
        String retVal="";
        String startText=this.pFDxmlns;
        String endText="</string>";
        try
        {
            int startPos=DataIn.indexOf(startText);
            if (startPos>-1)
            {
                startPos=startPos+startText.length();
            }
            int endPos=DataIn.indexOf(endText,startPos);
            retVal=DataIn.substring(startPos,endPos);
            retVal=EncodedHTMLToText(retVal);
        }
        catch (Exception e)
        {
            Log.d("FetchData", e.getMessage());
        }
        return retVal;
    }
    private static String EncodedHTMLToText(String str)
    {
        String newstr=str;
        newstr=newstr.replaceAll("&lt;", "<");
        newstr=newstr.replaceAll("&gt;", ">");
        newstr=newstr.replaceAll("&amp;", "&");
        newstr=newstr.replaceAll("&quot;", "\"");
        newstr=newstr.replaceAll("&#39;", "'");
        return newstr;
    }

}