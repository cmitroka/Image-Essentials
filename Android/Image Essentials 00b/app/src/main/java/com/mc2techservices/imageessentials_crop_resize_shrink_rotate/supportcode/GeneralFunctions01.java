package com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class GeneralFunctions01 {
	public static String isOnline;
	public static String chkRequestType;

	//AsyncWebCall Examples
	//
	//new GeneralFunctions01.AsyncWebCall().execute(AppSpecific.gloWebServiceURL + "/LogLookupRequest", "pUUID="+AppSpecific.gloUUID);
	//
	//String pParams = "pUUID=" + pUUID;
	//String pURL=AppSpecific.gloWebServiceURL + "/LogAppLaunched";
	//new GeneralFunctions01.AsyncWebCall().execute(pURL,pParams);

	public static class AsyncWebCall extends AsyncTask<String, String, String> {

		private String resp;
		private String USER_AGENT = "Mozilla/5.0";

		@Override
		protected String doInBackground(String... params) {
			publishProgress("Sleeping..."); // Calls onProgressUpdate()
			try {
				String url = params[0];
				String urlParameters = params[1];
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj
						.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				con.setDoOutput(true);
				Log.d("GF AsyncWebCall", "Ready to send...: " + url);

				try {
					DataOutputStream wr = new DataOutputStream(
							con.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();
				} catch (Exception e) {
					Log.d("GF AsyncWebCall", "Error 001");
					Log.d("GF AsyncWebCall", e.getMessage());
					resp = "CallWebService Exception Occured 1";
				}

				int responseCode = con.getResponseCode();

				Log.d("GF AsyncWebCall",
						"\nSending 'POST' request to URL : " + url);
				Log.d("GF AsyncWebCall", "Post parameters : "
						+ urlParameters);
				Log.d("GF AsyncWebCall", "Response Code : " + responseCode);

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
				Log.d("GF AsyncWebCall", response.toString());
				resp = response.toString();
			} catch (Exception e) {
				Log.d("GF AsyncWebCall", "Error 002");
				Log.d("GF AsyncWebCall", e.getMessage());
				resp = "CallWebService Exception Occured 2";
			}
			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
			//This does come back; what to do with it/how to handle it... not sure.
			Log.d("GF AsyncWebCall", "onPostExecute");
			Log.d("GF AsyncWebCall", "onPostExecute Raw Result:" + result);
			if (result.equals("CallWebService Exception Occured 2")) //Means the server or user is offline
			{
				isOnline="0";
			}
			else
			{
				isOnline="1";
			}
			String Response = Comm.GetResonseData(result,AppSpecific.gloxmlns); //THIS NEEDS NAMESPACE INFO!!!!  FIX LATER!!!
			Log.d("GF AsyncWebCall", "onPostExecute Clean Response" + Response);
		}

		@Override
		protected void onPreExecute() {
			Log.d("GF AsyncWebCall", "onPreExecute");
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			Log.d("GF AsyncWebCall", "onProgressUpdate");
		}
	}


	public class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
		@Override
		protected Long doInBackground(URL... urls) {
			int count = urls.length;
			long totalSize = 0;
			Log.d("GF DownloadFilesTask", "Start");
			for (int i = 0; i < count; i++) {
				//totalSize += Downloader.downloadFile(urls[i]);
				publishProgress((int) ((i / (float) count) * 100));
				// Escape early if cancel() is called
				if (isCancelled()) break;
			}
			return totalSize;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			Log.d("GF DownloadFilesTask", "onProgressUpdate");
			//setProgressPercent(progress[0]);
		}

		@Override
		protected void onPostExecute(Long result) {
			Log.d("GF DownloadFilesTask", "onPostExecute");
			//showDialog("Downloaded " + result + " bytes");
		}
	}
	public static class Oth{

		public static String getApplicationName(Context context) {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			int stringId = applicationInfo.labelRes;
			return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
		}
		public static boolean isValidEmail(String pEmailIn)
		{
			boolean retVal=false;
			int val=0;
			if (pEmailIn.contains("'")) return false;
			if (pEmailIn.contains("@")) val++;
			if (pEmailIn.length()>6) val++;;
			if (pEmailIn.contains(".")) val++;
			if (val==3) retVal=true;
			return retVal;
		}
		public static boolean IsNothing(Object a)
		{
			boolean retVal=false;
			if (a==null)
			{
				retVal=true;
			}
			else if (a.equals("null"))
			{
				retVal=true;
			}
			else if (a.equals(null))
			{
				retVal=true;
			}
			else if (a=="null")
			{
				retVal=true;
			}
			return retVal;
		}
		public static void Alert(Context pContext, String pMessage)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(pContext);
			builder.setTitle("Message")
					.setMessage(pMessage)
					.setNeutralButton("OK", null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		public static Boolean IsInArray(String[] valIn, int pos)
		{
			Boolean retVal=true;
			try {
				if (valIn[pos]!="djkslfdjslfjsdfhurfscm,");
			} catch (Exception e) {
				retVal=false;
			}
			return retVal;
		}
		public static boolean IsOddNumer(int num) {
			return (num & 1) != 0;
		}

	}
	public static class Img{
		public static String saveToInternalStorage(Context c, Bitmap bitmapImage, String pNameOfFile){
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
				bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
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
		public static Bitmap loadImageFromStorage(String path)
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
	}
	public static class Cfg{
		public static void WriteSharedPreference(Context pContext, String PrefName, String ValToWrite)
		{
			SharedPreferences prefs = pContext.getSharedPreferences(
					pContext.getPackageName(), Context.MODE_PRIVATE);
			prefs.edit().putString(PrefName, ValToWrite).commit();
		}
		public static String ReadSharedPreference(Context pContext, String PrefName)
		{
			String retVal="";
			SharedPreferences prefs = pContext.getSharedPreferences(
					pContext.getPackageName(), Context.MODE_PRIVATE);

			retVal = prefs.getString(PrefName, retVal);
			return retVal;
		}
		public static void DeleteSharedPreference(Context pContext, String PrefName)
		{
			SharedPreferences preferences = pContext.getSharedPreferences(pContext.getPackageName(), 0);
			preferences.edit().remove(PrefName).commit();
		}
	}
	public static class Comm{

		public static boolean isInternetConnectionThere(Context pContext, Activity activity) {
			boolean booThereConnection = false;
			ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(pContext.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting())
				booThereConnection = true;

			return booThereConnection;
		}

		public static boolean isNetworkConnected(Context context) {
			ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conManager.getActiveNetworkInfo();
			return ( netInfo != null && netInfo.isConnected() );
		}
		public static String GetResonseData(String DataIn, String gloxmlns)
		{
			String retVal="";
			String startText= gloxmlns;
			String endText="</string>";
			try 		{
				int startPos=DataIn.indexOf(startText);
				if (startPos>-1)
				{
					startPos=startPos+startText.length();
				}
				int endPos=DataIn.indexOf(endText,startPos);
				retVal=DataIn.substring(startPos,endPos);
				retVal= Text.EncodedHTMLToText(retVal);
			}
			catch (Exception e)
			{
				Log.d("GF GetResonseData", e.getMessage());
			}
			return retVal;
		}

		public static String NonAsyncWebCall(String pURL, String pParams)
		{
			String resp="";
			String USER_AGENT = "Mozilla/5.0";
			//if (GeneralFunctions01.isOnline.equals("0")) return "";
			if (Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy =
						new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			try {
				String url = pURL;
				String urlParameters = pParams;

				URL obj = new URL(url);
				Log.d("GF NonAsyncWebCall", "1");
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				con.setDoOutput(true);
				Log.d("GF NonAsyncWebCall", "2");
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();

				Log.d("GF NonAsyncWebCall", "3");
				int responseCode = con.getResponseCode();

				Log.d("GF NonAsyncWebCall", "\nSending 'POST' request to URL : " + url);
				Log.d("GF NonAsyncWebCall","Post parameters : " + urlParameters);
				Log.d("GF NonAsyncWebCall","Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				System.out.println(response.toString());
				Log.d("GF NonAsync Resp:", response.toString());
				resp=response.toString();
				resp= Comm.GetResonseData(resp, AppSpecific.gloxmlns);  //THIS NEEDS NAMESPACE INFO!!!!  FIX LATER!!!
				Log.d("GF NonAsync Clean Resp:", resp);
			}
			catch (Exception e)
			{
				isOnline="0";
				Log.d("GF NonAsyncWebCall", e.getMessage());
			}
			return resp;
		}
		public static void DownloadImageFromURL(Context pContext, String pURL, String pWhereToSaveImage)
		{
			try {
				String imageName= Subfunctions.GetImageName(pURL, "/");
				String fileLoc= Subfunctions.GetWorkingDirectory(pContext)+"/"+ imageName;
				InputStream input;
				URL url = new URL(pURL);
				input = url.openStream();
				byte[] buffer = new byte[1500];

				Log.d("GF DownloadImageFromURL", "Writing: "+fileLoc);
				OutputStream output = new FileOutputStream(fileLoc);
				try
				{
					int bytesRead = 0;
					while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
					{
						output.write(buffer, 0, bytesRead);
					}
				}
				catch (Exception e1) {
					// TODO Auto-generated catch block
					Log.d("GF DownloadImageFromURL", e1.getMessage());
				}
				finally
				{
					output.close();
					buffer=null;
				}
			}
			catch(Exception e)
			{
				Log.d("GF DownloadImageFromURL", e.getMessage());
				Log.d("GF DownloadImageFromURL", e.toString());
			}
		}
	}
	public static class Sys{
		public static List<File> GetFileList(File parentDir) {
			ArrayList<File> inFiles = new ArrayList<File>();
			File[] files = parentDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					inFiles.addAll(GetFileList(file));
				} else {
					if(file.getName().length()>0){
						inFiles.add(file);
					}
				}
			}
			return inFiles;
		}

		public static boolean IsAndroidEmulator() {
			String product = Build.PRODUCT;
			boolean isEmulator = false;
			if (product != null) {
				isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
			}
			Log.d("GF IsAndroidEmulator", "isEmulator=" + isEmulator);
			return isEmulator;
		}
		public static String CreateUUID()
		{
			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString();
			return randomUUIDString;
		}

		public static String GetVersion(Context pContext) {
			String vn = "";
			int vc = 0;
			try {
				vn = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0).versionName;
				vc = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0).versionCode;
			} catch (Exception e) {
				// Huh? Really?  Check to make sure something like this is in the Manifest... android:name=".fpfriendsfor500mb"
				Log.d("GF GetVersion", "GetVersion Error: "+ e.getMessage());

			}
			//String temp=vn + "." + String.valueOf(vc);
			String temp=vn;
			return temp;
		}
		public static String GetVersionCode(Context pContext) {
			String vn = "";
			int vc = 0;
			try {
				vc = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0).versionCode;
			} catch (Exception e) {
			}
			return String.valueOf(vn);
		}
		public static void DirectoryHelper(String pProposedDirectory)
		{
			try {
				File folder = new File(pProposedDirectory);
				boolean success = true;
				if (!folder.exists())
				{
					success = folder.mkdirs();
				}
				else
				{
					Log.d("GF DirectoryHelper", "DirectoryHelper Existed");
				}
				if (success) {
					Log.d("GF DirectoryHelper", "DirectoryHelper OK");
				} else {
					Log.d("GF DirectoryHelper", "DirectoryHelper Fail");
				}
			}
			catch (Exception e)
			{
				Log.d("GF DirectoryHelper", e.getMessage());
			}

		}
		public static Boolean DirectoryExists(String pProposedDirectory)
		{
			File f = new File(pProposedDirectory);
			if(f.isDirectory()) {
				Log.d("GF DirectoryExists", "DirectoryExists: True");
				return true;
			}
			else
			{
				Log.d("GF DirectoryExists", "DirectoryExists: False");
				return false;
			}
		}
		public static Boolean FileExists(String pProposedFile)
		{
			File file = new File(pProposedFile);
			if(file.exists())
			{
				return true;
			}
			else
			{
				return false;
			}
		}

	}
	public static class Text{
		public static String GetValInArray(String[] pObjIn, int pIndex)
		{
			String retVal="";
			if (Oth.IsInArray(pObjIn, pIndex))
			{
				retVal=pObjIn[pIndex];
			}
			return retVal;
		}
		public static String GetVal(Object pObjIn)
		{
			String retVal="";
			try {
				Spinner s1= (Spinner)pObjIn;
				retVal=s1.getSelectedItem().toString();
			} catch (Exception e) {}
			try {
				EditText t1= (EditText)pObjIn;
				retVal=t1.getText().toString();
			} catch (Exception e) {}
			try {
				TextView tv1= (TextView)pObjIn;
				retVal=tv1.getText().toString();
			} catch (Exception e) {}
			Object a=pObjIn;
			try {
				if (pObjIn==null)
				{
					retVal="";
				}
			} catch (Exception e) {}

			try {
				if (pObjIn.equals("null"))
				{
					retVal="";
				}
			} catch (Exception e) {}

			try {
				if (pObjIn.equals(null))
				{
					retVal="";
				}
			} catch (Exception e) {}
			return retVal;
		}
		public static String[] Split(String DataIn, String SplitDel) {
			String[] pVal=DataIn.split(SplitDel);
			for (int i = 0; i < pVal.length; i++) {
				if (pVal[i].equals(null))
				{
					pVal[i]="";
				}
				else if (pVal[i].equals("null"))
				{
					pVal[i]="";
				}
				else if (pVal[i]==null)
				{
					pVal[i]="";
				}
				else if (pVal[i]=="null")
				{
					pVal[i]="";
				}
			}
			return pVal;
		}
		private static final String ALLOWED_CHARACTERS_ALPHANUMERICFULL ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		private static final String ALLOWED_CHARACTERS_ALPHANUMERIC ="0123456789qwertyuiopasdfghjklzxcvbnm";
		private static final String ALLOWED_CHARACTERS_NUMERIC ="0123456789";
		private static final String ALLOWED_CHARACTERS_ALPHA ="0123456789";
		private static final String ALLOWED_CHARACTERS_HEX ="0123456789ABCDEF";
		public static String GetRandomInt(int MinVal, int MaxVal) {
			Random r1 = new Random();
			int i1 = r1.nextInt((MaxVal+1) - MinVal) + MinVal;
			String retVal= Conv.IntToString(i1);
			return retVal;
		}
		public static String GetRandomString(String Type, final int sizeOfRandomString)
		{
			final Random random=new Random();
			final StringBuilder sb=new StringBuilder(sizeOfRandomString);
			for(int i=0;i<sizeOfRandomString;++i)
				if (Type.equals("AN"))
				{
					sb.append(ALLOWED_CHARACTERS_ALPHANUMERIC.charAt(random.nextInt(ALLOWED_CHARACTERS_ALPHANUMERIC.length())));
				}
				else if (Type.equals("ANF"))
				{
					sb.append(ALLOWED_CHARACTERS_ALPHANUMERICFULL.charAt(random.nextInt(ALLOWED_CHARACTERS_ALPHANUMERICFULL.length())));
				}
				else if (Type.equals("A"))
				{
					sb.append(ALLOWED_CHARACTERS_ALPHA.charAt(random.nextInt(ALLOWED_CHARACTERS_ALPHA.length())));
				}
				else if (Type.equals("N"))
				{
					sb.append(ALLOWED_CHARACTERS_NUMERIC.charAt(random.nextInt(ALLOWED_CHARACTERS_NUMERIC.length())));

				}
				else if (Type.equals("H"))
				{
					sb.append(ALLOWED_CHARACTERS_HEX.charAt(random.nextInt(ALLOWED_CHARACTERS_HEX.length())));
				}
			return sb.toString();
		}

		public static String PutValsInSingQuotes(String pCommaSeperatedVals)
		{
			String line=pCommaSeperatedVals;
			int count = line.length() - line.replace(",", "").length();
			if (count>0)count++;
			String WriteVal;
			String retVal="";
			String[] separated = pCommaSeperatedVals.split(",");
			for (int i = 0; i < count; i++) {
				if (Oth.IsInArray(separated, i))
				{
					WriteVal=separated[i];
				}
				else
				{
					WriteVal="";
				}
				retVal=retVal+"'" + WriteVal + "',";
			}
			retVal=retVal.substring(0, retVal.length()-1);
			return retVal;
		}
		public static String EncodedHTMLToText(String str)
		{
			String newstr=str;
			newstr=newstr.replaceAll("&lt;", "<");
			newstr=newstr.replaceAll("&gt;", ">");
			newstr=newstr.replaceAll("&amp;", "&");
			newstr=newstr.replaceAll("&quot;", "\"");
			newstr=newstr.replaceAll("&#39;", "'");
			return newstr;
		}
		public static String GetValueAtPosition(String FullDataString, String Delimiter, int location)
		{
			String retVal="";
			try {
				String[] separated = FullDataString.split(Delimiter);

				retVal=separated[location]; // this will contain "Fruit"

			} catch (Exception e) {
				// TODO: handle exception
			}
			return retVal;
		}


	}
	public static class Dte{
		public static long DaysBetweenDate(String StartDate, String EndDate)
		{
			if (StartDate==null)
			{
				String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				Calendar c = Calendar.getInstance();
				System.out.println("Current time => " + c.getTime());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				StartDate = df.format(c.getTime());
			}
			Date d1= Conv.StringToDate(StartDate,false);
			Date d2= Conv.StringToDate(EndDate,false);
			long diff = d2.getTime() - d1.getTime();

			long diffDays = diff / (24 * 60 * 60 * 1000);
			return diffDays;
		}
		public static String ModifyDays(String DateIn, int DaysToChange)
		{
			String dt = DateIn; //"2012-01-04";  // Start date
			SimpleDateFormat sdf;
			if (DateIn.length()==10)
			{
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			}
			else
			{
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			Calendar c = Calendar.getInstance();
			try {
				c.setTime(sdf.parse(dt));
			} catch (Exception e) {
				e.printStackTrace();
			}
			c.add(Calendar.DATE, DaysToChange);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
			String output = sdf.format(c.getTime());
			return output;
		}
		public static String GetCurrentDateTime()
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			return sdf.format(date);
		}
		public static long SecondsBetweenDateTimes(String dateStart, String dateStop)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long diffSeconds=0;
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = format.parse(dateStart);
				d2 = format.parse(dateStop);

				// Get msec from each, and subtract.
				long diff = d2.getTime() - d1.getTime();
				diffSeconds = diff / 1000;
				System.out.println("Time in seconds: " + diffSeconds + " seconds.");
			} catch (Exception e) {
				e.getMessage();
			}
			return diffSeconds;
		}
		public static String TimeBetweenDateTimes(String dateStart, String dateStop)
		{
			String retVal="";
			Date d1 = null;
			Date d2 = null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				d1 = format.parse(dateStart);
				d2 = format.parse(dateStop);

				//in milliseconds
				long diff = d2.getTime() - d1.getTime();

				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);

				retVal = diffDays + " days, " + diffHours + " hours, " + diffMinutes + " minutes, " + diffSeconds + " seconds";
				System.out.print(diffDays + " days, ");
				System.out.print(diffHours + " hours, ");
				System.out.print(diffMinutes + " minutes, ");
				System.out.print(diffSeconds + " seconds.");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return retVal;
		}
		public static String GetCurrentDate()
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			return dateFormat.format(date);
		}

	}
	public static class Conv{

		public static String DateToString(Date DateIn)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.format(DateIn);
		}
		public static Date StringToDate(String StringDateIn, Boolean UseDateTime)
		{
			Date date=null;
			String dtStart = "2010-10-15T09:27:37Z";
			dtStart = StringDateIn;
			//dtStart = "2010-10-15T09:27:37Z";
			SimpleDateFormat format;
			if (UseDateTime==true)
			{
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

			}
			else
			{
				format = new SimpleDateFormat("yyyy-MM-dd");  //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			}
			try {
				date = format.parse(dtStart);
				System.out.println(date);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return date;
		}

		public static String DBDateToStandardDate(String DBDateIn, Boolean UseDateTime)
		{
			String retVal="";
			try {
				SimpleDateFormat simpledateformat;
				Date DateToConv;
				if (UseDateTime==true)
				{
					//myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());
					simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					DateToConv = simpledateformat.parse(DBDateIn);
					SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
					retVal = sdformat.format(DateToConv);
				}
				else
				{
					//myDate = new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
					simpledateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					DateToConv = simpledateformat.parse(DBDateIn);
					SimpleDateFormat sdformat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
					retVal = sdformat.format(DateToConv);
				}
				System.out.println(retVal); // Sat Jan 02 00:00:00 GMT 2010
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return retVal;
		}
		public static int StringToInt(String valIn)
		{
			int myNum = 0;

			try {
				myNum = Integer.parseInt(valIn);
			} catch(Exception ex) {
				System.out.println("Could not ConvStringToInt " + ex.getMessage());
			}
			return myNum;
		}
		public static String IntToString(int valIn)
		{
			String retVal = "";

			try {
				retVal = Integer.toString(valIn);
			} catch(Exception ex) {
				System.out.println("Could not ConvIntToString " + ex.getMessage());
			}
			return retVal;
		}
		public static String StandardDateToDBDate(String StandardDateIn, Boolean UseDateTime)
		{
			String retVal="";
			try {
				String myDate="";
				SimpleDateFormat simpledateformat;
				Date DateToConv;
				if (UseDateTime==true)
				{
					//myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());
					simpledateformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
					DateToConv = simpledateformat.parse(StandardDateIn);
					SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					retVal = sdformat.format(DateToConv);
				}
				else
				{
					//myDate = new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
					simpledateformat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
					DateToConv = simpledateformat.parse(StandardDateIn);
					SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					retVal = sdformat.format(DateToConv);
				}
				System.out.println(retVal); // Sat Jan 02 00:00:00 GMT 2010
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return retVal;
		}

	}
	private static class Subfunctions
	{
		private static String GetImageName(String DataIn, String Delimiter)
		{
			String RetVal="";
			String[] result = DataIn.split(Delimiter);
			if (result.length > 0)
			{
				int test=result.length-1;
				RetVal=result[test];
			}
			return RetVal;
		}
		private static String GetWorkingDirectory(Context pContext)
		{
			String retVal="";
			String pPackageName=pContext.getPackageName();
			String temp = Environment.getExternalStorageDirectory().toString()
					+ "//Android//Data//" + pPackageName; // "/android/data/"+GlobalClass.gloPackageName;
			temp = temp.replace("//", "/");
			return retVal;
		}
	}
}