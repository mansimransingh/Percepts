package com.mansimransingh.percepts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;


public class EnterComment extends Activity implements CallBackListener {
	private Button saveButton = null;
	private ImageView img = null;
	private FrameLayout parentLayout = null;
	private EditText commentBox = null;
	//private Bitmap imgData = null;
	private byte[] imgData;
	private ImageView imageView = null;
	private String commentURL = "http://152.78.200.147:8080/images/";
	private AsyncComment asyncHttpPost;
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		  setContentView(R.layout.enter_comment);
		  
		  Bundle params = getIntent().getExtras();
		 imgData = null;
		  img = (ImageView)findViewById(R.id.image_view_comment);
		  
		  byte[] bytes = getIntent().getByteArrayExtra("IMAGEE");
		  String imgID = getIntent().getStringExtra("IMAGEID");
	      commentURL = commentURL + imgID + "/comment";
	      
		  if(bytes != null) {
			  Log.e("MAINN","IMAGEE RESULT WASNT NULL");
			  Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			  bmp = RotateBitmap(bmp, 90);
			  		if(bmp == null){
			  			Log.e("BITMAP","BITMAP RETURNED NULL");
			  			 Toast
				          .makeText(EnterComment.this, "Failed to load image. Please restart Percepts", Toast.LENGTH_LONG)
				          .show();
			  		} else {
			  			img.setImageBitmap(bmp);
			  		}
			 Toast.makeText(EnterComment.this, commentURL, Toast.LENGTH_SHORT);
		  } else {
			  
			  Log.e("MAIN","IMAGE DATA WAS NULL");
			  Toast
	          .makeText(EnterComment.this, "Failed to load image. Please restart Percepts", Toast.LENGTH_LONG)
	          .show();
		  }
		  
		  Log.e("COMMENTURL",commentURL);
		  
		  saveButton = (Button) findViewById(R.id.save_button);
		  commentBox = (EditText)findViewById(R.id.comment_text_field);
		  
		  saveButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	            	String str = commentBox.getText().toString();
	            	if(str.replaceAll("\\s+","").length() != 0){
	            		//lets post the string here
	            		asyncHttpPost = new AsyncComment(getApplicationContext());
	    				asyncHttpPost.setListener(EnterComment.this);
	    				//asyncHttpPost.execute("http://msingh.co/percept/post.php", s);
	    				asyncHttpPost.execute(commentURL, str);
	    				 Toast
	    		          .makeText(EnterComment.this, "Saving comment", Toast.LENGTH_SHORT)
	    		          .show();
	            		
	            	}
	            	
	            }
	        });
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
	  
	  public void callback() {
			// TODO Auto-generated method stub
			String response = asyncHttpPost.getResponse();
			Log.e("RESPONSE",response);
			// Convert String to json object
			JSONObject json;
			try {
				json = new JSONObject(response);
				//JSONObject  = json.getJSONObject("success");
				String success = json.getString("success");
				if(success.equals("true")){
					Log.e("SUCCESS","SUCCESS ON JSON ");
					String targetID = json.getString("targetId");
					// get value from LL Json Object
					//String str_value=json.getString("value"); //<< get value here
				
					
					//TODO go to next activity
					
					
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("ERROR","FAILED TO PARSE JSON");
				Toast
		          .makeText(EnterComment.this, "Encountered an error. Please restart Percepts", Toast.LENGTH_LONG)
		          .show();
			}
	  
	}
}