package com.bottiger.android.reddit.threads;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import com.bottiger.android.reddit.common.util.Util;
import com.bottiger.android.reddit.reddits.Subreddits;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public abstract class SwipeDetectorActivity extends ListActivity implements OnGestureListener {
	
    // List of GesturePoints for calculating gestures
    private GestureDetector mGestureDetector = new GestureDetector(this);
    private ArrayList<String> mSubredditsList = null;
    
    protected abstract String getCurrentSubreddit();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	CookieSyncManager.createInstance(this);
    	new DownloadSubredditListTask().execute();
    }
    
    /*
     * Handle touch events.
     * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
    	Log.i("Pointerlog X", "MyClass.getView() — get position " + event.getX());
    	Log.i("Pointerlog Y", "MyClass.getView() — get position " + event.getY());
    	
        if (mGestureDetector.onTouchEvent(event))
        	return true;
        
        return super.dispatchTouchEvent(event);
    }

	protected ArrayList<String> getSubreddits() {
		return mSubredditsList;
	}
    
	@Override
	public boolean onDown(MotionEvent e) {return false;}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
	    final int SWIPE_MIN_DISTANCE = 120;
	    final int SWIPE_MAX_OFF_PATH = 250;
	    final int SWIPE_THRESHOLD_VELOCITY = 200;
		
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            
            int subredditIndex;
            try {
            	subredditIndex = getSubreddits().indexOf(getCurrentSubreddit());
            } catch (Exception e) {
            	subredditIndex = 1;
            }
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Left Swipe
            	subredditIndex++;
            	Toast.makeText(SwipeDetectorActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right Swipe
            	subredditIndex--;
            	Toast.makeText(SwipeDetectorActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
            }
            
            //FIXME
            subredditIndex = subredditIndex < 0 ? 0 : subredditIndex;
           	Intent intent = new Intent();
           	String reddit = getSubreddits().get(subredditIndex);
           	Toast.makeText(SwipeDetectorActivity.this, reddit, Toast.LENGTH_SHORT).show();
           	gotoSubreddit(reddit);
        } catch (Exception e) {
            // nothing
        	int i = 0;
        	i++;
        }
		return false;
	}
	
	abstract void gotoSubreddit(String subreddit);

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) { return false; }

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}
	
	
	 private class DownloadSubredditListTask extends AsyncTask<Void, Void, ArrayList<String>> {
	     protected ArrayList<String> doInBackground(Void... voidz) {
	         ArrayList<String> subreddits = new Subreddits(getApplicationContext()).getSubreddits();
	         return subreddits;
	     }
	     
	    	@Override
	    	public void onPreExecute() {
	    		String[] reddits = Subreddits.DEFAULT_SUBREDDITS;
	    		mSubredditsList = new ArrayList<String>();
	    		Collections.addAll(mSubredditsList, reddits);
	    	}

	     protected void onPostExecute(ArrayList<String> result) {
	         mSubredditsList = result;
	     }
	 }
	 
	 
	 


}
