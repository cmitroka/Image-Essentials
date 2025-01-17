// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.mc2techservices.imageessentials_crop_resize_shrink_rotate;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.AppSpecific;
import com.mc2techservices.imageessentials_crop_resize_shrink_rotate.supportcode.GeneralFunctions01;

import java.io.File;

public final class CropResultActivity extends AppCompatActivity {

  /** The image to show in the activity. */
  static Bitmap mImage;

  private ImageView imageView;


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu1, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_crop_result);
    imageView = ((ImageView) findViewById(R.id.resultImageView));
    imageView.setBackgroundResource(R.drawable.backdrop);

    //Intent intent = getIntent();
    if (mImage != null) {
      AppSpecific.gloInstruction++;
      String sInst=String.valueOf(AppSpecific.gloInstruction);
      GeneralFunctions01.Img.saveToInternalStorage(this, mImage,"profile"+sInst);
      Log.d("APP", "Saving profile"+String.valueOf(AppSpecific.gloInstruction));

      //Intent intentx = new Intent(this, MainActivity.class);
      //startActivity(intentx);
      finish();
      /*
      imageView.setImageBitmap(mImage);
      int sampleSize = intent.getIntExtra("SAMPLE_SIZE", 1);
      double ratio = ((int) (10 * mImage.getWidth() / (double) mImage.getHeight())) / 10d;
      int byteCount = 0;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
        byteCount = mImage.getByteCount() / 1024;
      }
      String desc =
          "("
              + mImage.getWidth()
              + ", "
              + mImage.getHeight()
              + "), Sample: "
              + sampleSize
              + ", Ratio: "
              + ratio
              + ", Bytes: "
              + byteCount
              + "K";
      ((TextView) findViewById(R.id.resultImageText)).setText(desc);
    } else {
      Uri imageUri = intent.getParcelableExtra("URI");
      if (imageUri != null) {
        imageView.setImageURI(imageUri);
      } else {
        Toast.makeText(this, "No image is set to show", Toast.LENGTH_LONG).show();
      }
    */
    }
  }

  @Override
  public void onBackPressed() {
    releaseBitmap();
    super.onBackPressed();
  }

  public void onImageViewClicked(View view) {
    releaseBitmap();
    finish();
  }

  private void releaseBitmap() {
    if (mImage != null) {
      mImage.recycle();
      mImage = null;
    }
  }
}
