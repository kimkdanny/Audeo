package com.orchestr8.android.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class GridItemAdapter extends BaseAdapter {
	Context con;
	LayoutInflater inflater;
	List<String>grid_pictures;
	String image_url = "";
	SmartImageView grid_image;
	ArrayList<String>image_urls = new ArrayList<String>();
	boolean finished = false;
	int counter = 0;
	String emotion;
	String answer;
	public GridItemAdapter(Context c, ArrayList<String> concepts, String emotion) {
		inflater = LayoutInflater.from(c);
		con = c;
		grid_pictures = concepts;
		System.out.println("FUCK GRIDPICS" + grid_pictures);
		this.emotion = emotion;
		System.out.println("FUCK SEARCHING" + grid_pictures.get(0));
		System.out.println("FUCK SEARCH URL " + "https://www.google.com/search?start=1&q=" + grid_pictures.get(0).replace(" ", "%20") + "&tbs=isz:lt,is&gbv=2&biw=1279&bih=679&sei=8ujrUfjiGOmTiQeqmIGQCg&tbm=isch#q=" + grid_pictures.get(0) + "&safe=off&gbv=2&tbs=isz:i&tbm=isch&source=lnt&sa=X&ei=8ujrUcb6Ja-aiAeB8YGABQ&ved=0CBwQpwUoAzgB&bav=on.2,or.r_cp.r_qf.&bvm=bv.49478099%2Cd.aGc%2Cpv.xjs.s.en_US.c75bKy5EQ0A.O&fp=f895aedf61b43810&biw=1279&bih=679");
	    new RequestTask().execute("http://images.google.com/images?gbv=1&start=" + 20 + "&q=" + grid_pictures.get(0).replace(" ", "%20") + "&tbs=isz:ex,iszw:150,iszh:150");
		
	    
	}

	@Override	
	public int getCount() {
		if(grid_pictures!=null){
			return grid_pictures.size() + 1;
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}
	
	public String get_emotion_url(){
		if (emotion.equals("affection_friendliness")) {
			answer = "Affection";
			return "http://i.imgur.com/dakunR7.png";
		} else if (emotion.equals("enjoyment_elation")) {
			answer = "Enjoyment";
			return "http://i.imgur.com/D1RwYIJ.png";
		} else if (emotion.equals("amusement_excitement")) {
			answer = "Excitement";
			return "http://i.imgur.com/iHOJF52.png";
		} else if (emotion.equals("contentment_gratitude")) {
			answer = "Contentment";
			return "http://i.imgur.com/eoF5vd7.png";
		} else if (emotion.equals("sadness_grief")) {
			answer = "Sadness";
			return "http://i.imgur.com/ZOTvcgz.png";
		} else if (emotion.equals("anger_loathing")) {
			answer = "Anger";
			return "http://i.imgur.com/88kCzZz.png";
		} else if (emotion.equals("fear_uneasiness")) {
			answer = "Fear";
			return "http://i.imgur.com/kQ8lC73.png";
		} else if (emotion.equals("humiliation_shame")) {
			answer = "Shame";
			return "http://i.imgur.com/ynKIkJJ.png";
		}else{
			answer = "Neutral";
			return "http://i.imgur.com/l87haug.png";
		}
	}
	

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = inflater.inflate(R.layout.grid_item, null);
		}
		//inflater.inflate(R.layout.grid_item, parent, false);

		grid_image = (SmartImageView) convertView
				.findViewById(R.id.imageView1);
		TextView text = (TextView)convertView.findViewById(R.id.textView1);
		//grid_image.setAlpha(127);
		grid_image.setTag(position);
		try{
			grid_image.setImageUrl(image_urls.get(position));
			
			text.setText(grid_pictures.get(position));

		}catch(Exception e){
			
		}
		
		if(position == getCount()-1){
			System.out.println("FUCK URL " + get_emotion_url());
			grid_image.setImageUrl(get_emotion_url());
			text.setText(answer);
		}
		
	
		return convertView;
	}
	
	public void refresh(){
		notifyDataSetChanged();
	}
	
	
	class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        //responseString = responseString.substring(responseString.indexOf("imgurl=http"), responseString.indexOf("imgurl=http") + responseString.substring(responseString.indexOf("imgurl=http")).indexOf(".jpg"));
	        //System.out.println("FUCK URRRR: " + responseString);
	        Pattern pattern = Pattern.compile("imgurl=(.*?\\.(?i)(jpg|jpeg|png|gif|bmp|tif|tiff))");
	    
	        Matcher matcher = pattern.matcher(responseString);
	        while (matcher.find()) {
	            System.out.print("FUCK GOOGLE Start index: " + matcher.start());
	            System.out.print("FUCK GOOGLE End index: " + matcher.end() + " ");
	            String match = matcher.group();
	            match = match.substring(match.indexOf("http"));
	            System.out.println("FUCK URL: " + match);
	            return match;
	            
	        }
	       return "";
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        
	        
//	        System.out.println("FUCK " + result);
//	        String farm_id = result.substring(result.indexOf("farm") + 6, result.indexOf(", \"title"));
//	        String server_id = result.substring(result.indexOf("server") + 9, result.indexOf(", \"farm")-1);
//	        String id = result.substring(result.indexOf("id") + 5, result.indexOf(", \"owner")-1);
//	        String secret = result.substring(result.indexOf("secret") + 9, result.indexOf(", \"server")-1);
	        image_url = result;
	      //  System.out.println("FUCK SECRET: " + secret);
	        image_urls.add(image_url);
	        counter++;
	        if(counter == grid_pictures.size()){
	        	refresh();
	        }else{
	    	    new RequestTask().execute("http://images.google.com/images?gbv=1&start=" + 20 + "&q=" + grid_pictures.get(counter).replace(" ", "%20")  + "&tbs=isz:ex,iszw:150,iszh:150");

	        }
	        //urls.add(url);
	    }
	}
}