package com.mansimransingh.percepts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class EnterComment extends Activity {
	private Button saveButton = null;
	private ImageView img = null;
	private FrameLayout parentLayout = null;
	private EditText commentBox = null;
	private byte[] result = null;
	//private Bitmap imgData = null;
	private byte[] imgData;
	private ImageView imageView = null;
	private String commentURL = "http://152.78.200.147:8080/images/ID/comment";
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		  setContentView(R.layout.enter_comment);
		  
		  Bundle params = getIntent().getExtras();
		 imgData = null;
		  img = (ImageView)findViewById(R.id.image_view_comment);
			  
		  byte[] bytes = getIntent().getByteArrayExtra("IMAGEE");
	      
		  if(bytes != null) {
			  Log.e("MAINN","IMAGEE RESULT WASNT NULL");
			  Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			  bmp = RotateBitmap(bmp, 90);
			  		if(bmp == null){
			  			Log.e("BITMAP","BITMAP RETURNED NULL");
			  		} else {
			  			img.setImageBitmap(bmp);
			  		}
			 
		  } else {
			  
			  Log.e("MAIN","IMAGE DATA WAS NULL");
		  }
		  
	  }	  	 
	  public static Bitmap RotateBitmap(Bitmap source, float angle)	  {
	        Matrix matrix = new Matrix();
	        matrix.postRotate(angle);
	        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	  }
	  @Override
	  public void onResume() {
	    super.onResume();

	  }
	    
	  @Override
	  public void onPause() {
          
	    super.onPause();
	  }
	  
	}