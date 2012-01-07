package de.dsp.android.hsc.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import de.dsp.android.hsc.R;

public class HttpStatusCheckTask extends AsyncTask<String, Integer, Integer>{
	
	private ProgressBar progressBar;
	private ImageView imageViewStatus;
	
	public HttpStatusCheckTask(ProgressBar progressBar, ImageView imageViewStatus){
		this.progressBar = progressBar;
		this.imageViewStatus = imageViewStatus;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (progressBar!=null){
    		progressBar.setVisibility(View.VISIBLE);
    	}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

    	if (progressBar!=null){
    		progressBar.setVisibility(View.GONE);
    	}

		if (imageViewStatus!=null){
    		imageViewStatus.setVisibility(View.VISIBLE);
    		imageViewStatus.setClickable(true);
    		final AlertDialog alertDialog = new AlertDialog.Builder(imageViewStatus.getContext()).create();  
    	    alertDialog.setTitle("Http Status Code");  
    	    alertDialog.setMessage("" + result);  
    	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
    	      public void onClick(DialogInterface dialog, int which) {  
    	        return;  
    	    } });  
    	    imageViewStatus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.show();
				}
			} );
    		if (result==200){
        		imageViewStatus.setImageResource(R.drawable.drawable_image_status_ok);
    		} else {
        		imageViewStatus.setImageResource(R.drawable.drawable_image_status_error);
    		}
    	}

	}

	@Override
	protected Integer doInBackground(String... params) {
    	return checkHttpStatus(params[0]);
	}

    private int checkHttpStatus(String urlString){
		try {
			URL url = new URL(urlString);
			URI uri = url.toURI();
	    	HttpGet httpRequest = new HttpGet(uri);
	    	HttpClient httpclient = getHttpClient();
	    	HttpResponse response = httpclient.execute(httpRequest);
	    	if (response!=null){
	    		return response.getStatusLine().getStatusCode();
//	    		int statusCode = response.getStatusLine().getStatusCode();
//	    		return statusCode == 200;
	    	}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return -1;
    }
    
    private DefaultHttpClient getHttpClient() {
        DefaultHttpClient ret = null;

        //sets up parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);

        //registers schemes for both http and https
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", sslSocketFactory, 443));

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);
        return ret;
    }    
	
}
