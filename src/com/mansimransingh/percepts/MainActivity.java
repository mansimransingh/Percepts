package com.mansimransingh.percepts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements CallBackListener {
	  private SurfaceView preview=null;
	  private SurfaceHolder previewHolder=null;
	  private Camera camera=null;
	  private boolean inPreview=false;
	  private boolean cameraConfigured=false;
	  private Button takePhotoButton = null;
	  private byte[] imgArray = null;
	  private String fileName = "";
	  private Bitmap imageBitmap = null;
	  private byte[] bytes;
	  AsyncPost asyncHttpPost;
	  //http://152.78.200.147:8080/
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.main_layout);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    
	    preview=(SurfaceView)findViewById(R.id.cpPreview);
	    previewHolder=preview.getHolder();
	    previewHolder.addCallback(surfaceCallback);
	 	preview.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(camera != null && inPreview){
					camera.autoFocus(null);
				}	
			}	 		
	 	});
	    takePhotoButton = (Button) findViewById(R.id.take_photo);
	    
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	Log.d("DEBUG","Button clicked");  
            	if(inPreview){
					camera.takePicture(null, displayPicture, jpegCallback);
            		Log.d("DEBUG","Was in preview so should have taken picture");
            		takePhotoButton.setEnabled(false);            		
            	}
            }
        });
	  }	  
	  
	  Camera.PictureCallback displayPicture = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] Src, Camera camera) {
			//imgArray = Src;
			//imageBitmap = BitmapFactory.decodeByteArray(Src,0,Src.length);
			
		}
	};
	  
	  Camera.PictureCallback jpegPicture = new Camera.PictureCallback(){

		@Override
		public void onPictureTaken(byte[] imgData, Camera camera) {
			onPause();
			imageBitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
			String s = encodeTobase64(imgData);
			
			//sendData(s);
			AsyncPost asyncHttpPost = new AsyncPost(getApplicationContext());
			
			asyncHttpPost.execute("http://msingh.co/percept/post.php", s);

			
			Toast
	          .makeText(MainActivity.this, "URL is msingh.co", Toast.LENGTH_LONG)
	          .show();
			Log.d("DEBUG","Started async post");
		}
		  
	  };


	  PictureCallback jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				 ByteArrayOutputStream stream = new ByteArrayOutputStream();
			        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			         bytes = stream.toByteArray(); 
			       
			        
				FileOutputStream outStream = null;
				fileName = String.format(
						"/sdcard/tmpCache/%d.jpg", System.currentTimeMillis());
				
				
				try {
					File myDir = new File("sdcard/tmpCache");
					myDir.mkdir();
					
					outStream = new FileOutputStream(fileName);
					outStream.write(data);
					outStream.close();
					Log.d("DEBUG", "onPictureTaken - wrote bytes: " + data.length);
					 Toast
			          .makeText(MainActivity.this, "Image saved", Toast.LENGTH_LONG)
			          .show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				Log.d("DEBUG", "onPictureTaken - jpeg");
				
				
				
				//pass to server
				String s = encodeTobase64(data);
				
				//sendData(s);
				asyncHttpPost = new AsyncPost(getApplicationContext());
				asyncHttpPost.setListener(MainActivity.this);
				//asyncHttpPost.execute("http://msingh.co/percept/post.php", s);
				asyncHttpPost.execute("http://152.78.200.147:8080/test", s);
				 Toast
		          .makeText(MainActivity.this, "Uploading and analysing image", Toast.LENGTH_LONG)
		          .show();
				
				
		}
		};

	  public static String encodeTobase64(byte[] b){
	      String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
	      Log.d("LOOK", "Image encoded to base 64");
	      return imageEncoded;
	  }
	  
	  @Override
	  public void onResume() {
		  super.onResume();
	    if(!inPreview || camera == null){
	    	
	    	
	    	camera=Camera.open();
	    	camera.setDisplayOrientation(90);
	    	startPreview();
	    	 inPreview = true;
	    	 takePhotoButton.setEnabled(true);   
	    }
	  }
	    
	  @Override
	  public void onPause() {
	    if (inPreview || camera != null) {
	      camera.stopPreview();
	      camera.release();
		  camera=null;
		  inPreview=false;
	    }   
	          
	    super.onPause();
	  }
	  
	  private Camera.Size getBestPreviewSize(int width, int height,
	                                         Camera.Parameters parameters) {
	    Camera.Size result=null;
	    
	    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
	      if (size.width<=width && size.height<=height) {
	        if (result==null) {
	          result=size;
	        }
	        else {
	          int resultArea=result.width*result.height;
	          int newArea=size.width*size.height;
	          
	          if (newArea>resultArea) {
	            result=size;
	          }
	        }
	      }
	    }
	    
	    return(result);
	  }
	  
	  
	  
	  private void initPreview(int width, int height) {
	    if (camera!=null && previewHolder.getSurface()!=null) {
	      try {
	        camera.setPreviewDisplay(previewHolder);
	      }
	      catch (Throwable t) {
	        Log.e("PreviewDemo-surfaceCallback",
	              "Exception in setPreviewDisplay()", t);
	        Toast
	          .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
	          .show();
	      }

	      if (!cameraConfigured) {
	    	  
	        Camera.Parameters parameters=camera.getParameters();
	        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();	        
	        camera.setDisplayOrientation(90);
	        /*if(display.getRotation() == Surface.ROTATION_0)
	        {
	            parameters.setPreviewSize(height, width);                           
	            camera.setDisplayOrientation(90);
	        }

	        if(display.getRotation() == Surface.ROTATION_90)
	        {
	            parameters.setPreviewSize(width, height);                           
	        }

	        if(display.getRotation() == Surface.ROTATION_180)
	        {
	            parameters.setPreviewSize(height, width);               
	        }

	        if(display.getRotation() == Surface.ROTATION_270)
	        {
	            parameters.setPreviewSize(width, height);
	            camera.setDisplayOrientation(180);
	        }*/

	        //camera.autoFocus(null);
	        Camera.Size size=getBestPreviewSize(width, height,
	                                            parameters);
	        
	        if (size!=null) {
	          parameters.setPreviewSize(size.width, size.height);
	          camera.setParameters(parameters);
	          cameraConfigured=true;
	        }
	      }
	    }
	  }
	  
	  private void startPreview() {
	    if (cameraConfigured && camera!=null) {
	      camera.startPreview();
	      inPreview=true;
	    }
	  }
	  
	  SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
	    public void surfaceCreated(SurfaceHolder holder) {
	      // no-op -- wait until surfaceChanged()
	    }
	    
	    public void surfaceChanged(SurfaceHolder holder,
	                               int format, int width,
	                               int height) {
	      initPreview(width, height);
	      startPreview();
	    }
	    
	    public void surfaceDestroyed(SurfaceHolder holder) {
	      // no-op
	    }
	  };
		@Override
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
			    	Intent intent = new Intent(MainActivity.this, EnterComment.class);
			    	intent.putExtra("IMAGEE",bytes);
			    	
			        intent.putExtra("IMAGEID",targetID);
			        Log.e("TARGETID",targetID);
			        startActivity(intent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("ERROR","FAILED TO PARSE JSON");
				Toast
		          .makeText(MainActivity.this, "Encountered an error. Please restart Percepts", Toast.LENGTH_LONG)
		          .show();
			}

			// get LL json object11-10 12:16:04.609: E/RESPONSE(9058): {"success": "True", "author": null, "title": null, "targetId": "188d7966a4cd42e4b713a054faf6e41d", "comments": [], "likes": null}

			

			
	          
			
		}

	}