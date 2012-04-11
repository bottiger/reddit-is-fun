package com.bottiger.android.reddit.threads;

import java.util.ArrayList;

import com.bottiger.android.reddit.common.util.Util;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public abstract class SwipeDetectorActivity extends ListActivity implements OnGestureListener {
	
    // List of GesturePoints for calculating gestures
    private GestureDetector mGestureDetector = new GestureDetector(this);
    
    protected abstract ArrayList<String> getSubreddits();
    protected abstract String getCurrentSubreddit();
    
    /*
     * Handle touch events.
     * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
    	int q = event.getAction();
    	Log.i("Pointerlog X", "MyClass.getView() — get position " + event.getX());
    	Log.i("Pointerlog Y", "MyClass.getView() — get position " + event.getY());
    	
        switch (q)
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Record the location of the ACTION_DOWN
                //Either in a GestureDetector or in a variable
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                //Potentially start consuming events here as you may
                //have moved to far for a click or scroll
                //Also scroll the screen as necessary
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                //Consume if necessary and perform the fling / swipe action
                //if it has been determined to be a fling / swipe
            	if (true) {
            		int i = 1+1;
            		i++;
            	}
                break;
            }
        }
        
        mGestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
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
           	Intent intent = new Intent();
           	String reddit = getSubreddits().get(subredditIndex);
           	reddit = "wtf";
           	Uri redditUrl = Util.createSubredditUri(reddit);
           	intent.setData(redditUrl);
           	setResult(RESULT_OK, intent);
           	finish();	
        } catch (Exception e) {
            // nothing
        	int i = 0;
        	i++;
        }
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) { return false; }

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}


}
