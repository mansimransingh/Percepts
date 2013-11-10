package com.mansimransingh.percepts;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncComment extends AsyncTask<String, String, String> {
	private Context c;
	private StatusLine statusLine = null;
	private HttpResponse response = null;
    private String[] mData = null;// post data
    private byte[] result = null;
    private String str = "";
   // private static String response;
    /**
     * constructor
     */
    CallBackListener mListener;

    public void setListener(CallBackListener listener){
      mListener = listener;
    }
    
    public AsyncComment(Context con) {
    	c = con.getApplicationContext();
    }

    /**
     * background
     */
    @Override
    protected String doInBackground(String... params) {
    	 mData = params;
        
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(mData[0]);// in this case, params[0] is URL
        try {
            // set up post data
        	Log.d("DEBUG","Trying to send image data via post");
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            
              //$_POST['name'],  $_POST['imgData'] , $width = $_POST['width'], $metaData = $_POST['metaData']
            	//String randomNameString = randomString();
            	//Log.e("IMAGE NAME", randomNameString);
                nameValuePair.add(new BasicNameValuePair("comment", mData[1]));
                         
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "ASCII"));
            response = client.execute(post);
            Log.d("DEBUG","Got a response");
            statusLine = response.getStatusLine();
            Log.d("DEBUG","INT Status code "+statusLine.getStatusCode());
            if(statusLine.getStatusCode() == 200){
            	Log.d("DEBUG", "Happy days");
                result = EntityUtils.toByteArray(response.getEntity());
                str = new String(result, "UTF-8");
                Log.d("RESPONSE FROM COMMENT",str);
                //setResponse(str);
               
                onPostExecute();
             }
            
        }
        catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        	Log.e("Exception","Unsupported encoding exception in async post");
        }
        catch (Exception e) {
        	Log.e("Exception","Exception in async post"+e.toString());
        	
        }
        
        return str;
    }

   
    protected void onPostExecute() {
    	Log.d("EXECUTED","POST EXECUTION METHOD");
    	if(statusLine != null){
    		Log.d("DEBUG","Status line is not null");
    		if(statusLine.getStatusCode() == 200){
    			mListener.callback();
    		}
    	} else {
    		Log.e("ERROR","Status line is NULL");
    	}
    	 
    }


	protected void onProgressUpdate(Integer... values) {
    	Log.d("PROGRESS","VAL: "+values);
    }
	
	protected void onPreExecute(){
		Log.e("PREEXECUTE","PREEXECUTION METHOD");
	}
	public String getResponse() {
		// TODO Auto-generated method stub
		
		return str;
	}

}