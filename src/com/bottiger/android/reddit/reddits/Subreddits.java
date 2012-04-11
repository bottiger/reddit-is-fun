package com.bottiger.android.reddit.reddits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.bottiger.android.reddit.common.CacheInfo;
import com.bottiger.android.reddit.common.Constants;
import com.bottiger.android.reddit.common.RedditIsFunHttpClientFactory;
import com.bottiger.android.reddit.reddits.PickSubredditActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * This currently contains a lot of code from PickSubredditActivity.
 * This class should be refactored into PickSubredditActivity
 */
public class Subreddits {
	
	private static final String TAG = "Subreddits";
	
	// Group 1: inner
    private final Pattern MY_SUBREDDITS_OUTER = Pattern.compile("your front page reddits.*?<ul>(.*?)</ul>", Pattern.CASE_INSENSITIVE);
    // Group 3: subreddit name. Repeat the matcher.find() until it fails.
    private final Pattern MY_SUBREDDITS_INNER = Pattern.compile("<a(.*?)/r/(.*?)>(.+?)</a>");
	
    private Context mContext = null;
    
	private HttpClient mClient = RedditIsFunHttpClientFactory.getGzipHttpClient();
	private static ArrayList<String> mSubredditsList;
	
    public static final String[] DEFAULT_SUBREDDITS = {
    	"pics",
    	"funny",
    	"politics",
    	"gaming",
    	"askreddit",
    	"worldnews",
    	"videos",
    	"iama",
    	"todayilearned",
    	"wtf",
    	"aww",
    	"technology",
    	"science",
    	"music",
    	"askscience",
    	"movies",
    	"bestof",
    	"fffffffuuuuuuuuuuuu",
    	"programming",
    	"comics",
    	"offbeat",
    	"environment",
    	"business",
    	"entertainment",
    	"economics",
    	"trees",
    	"linux",
    	"android"
    };
    
    public Subreddits(Context c) {
    	mContext = c;
    }
    
    public ArrayList<String> getSubreddits() {
    	return mSubredditsList == null ? downloadSubreddits(true) : mSubredditsList;
    }
	
    private ArrayList<String> downloadSubreddits(boolean userSubreddits) {
		ArrayList<String> reddits = null;
		HttpEntity entity = null;
        try {
        	
        	reddits = cacheSubredditsList(reddits);
        	
        	if (reddits == null) {
        		reddits = new ArrayList<String>();
        		
        		String subredditUrl = Constants.REDDIT_BASE_URL + "/reddits";
        		if (userSubreddits) subredditUrl = subredditUrl + "/mine";
        		
            	HttpGet request = new HttpGet(subredditUrl);
            	// Set timeout to 15 seconds
                HttpParams params = request.getParams();
    	        HttpConnectionParams.setConnectionTimeout(params, 15000);
    	        HttpConnectionParams.setSoTimeout(params, 15000);
    	        
    	        HttpResponse response = mClient.execute(request);
            	entity = response.getEntity();
            	BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                
                String line = in.readLine();
                in.close();
                entity.consumeContent();
                
                Matcher outer = MY_SUBREDDITS_OUTER.matcher(line);
                if (outer.find()) {
                	Matcher inner = MY_SUBREDDITS_INNER.matcher(outer.group(1));
                	while (inner.find()) {
                		reddits.add(inner.group(3));
                	}
                } else {
                	return null;
                }
                
                if (Constants.LOGGING) Log.d(TAG, "new subreddit list size: " + reddits.size());
                
                if (Constants.USE_SUBREDDITS_CACHE) {
                	try {
                		CacheInfo.setCachedSubredditList(mContext, reddits);
                		if (Constants.LOGGING) Log.d(TAG, "wrote subreddit list to cache:" + reddits);
                	} catch (IOException e) {
                		if (Constants.LOGGING) Log.e(TAG, "error on setCachedSubredditList", e);
                	}
                }
        	}
            
    		if (reddits == null || reddits.size() == 0) {
    			// Need to make a copy because Arrays.asList returns List backed by original array
    	        mSubredditsList = new ArrayList<String>();
    	        mSubredditsList.addAll(Arrays.asList(DEFAULT_SUBREDDITS));
    		} else {
    			mSubredditsList = reddits;
    		}
    		
    		return mSubredditsList;
            
        } catch (Exception e) {
        	if (Constants.LOGGING) Log.e(TAG, "failed", e);
            if (entity != null) {
                try {
                	entity.consumeContent();
                } catch (Exception e2) {
                	// Ignore.
                }
            }
        }
        return null;
    }
    
    private ArrayList<String> cacheSubredditsList(ArrayList<String> reddits){
    	if (Constants.USE_SUBREDDITS_CACHE) {
    		if (CacheInfo.checkFreshSubredditListCache(mContext)) {
    			reddits = CacheInfo.getCachedSubredditList(mContext);
    			if (Constants.LOGGING) Log.d(TAG, "cached subreddit list:" + reddits);
    		}
    	}
		return reddits;
    }

}
