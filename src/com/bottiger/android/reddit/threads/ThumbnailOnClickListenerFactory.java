package com.bottiger.android.reddit.threads;

import com.bottiger.android.reddit.things.ThingInfo;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

public interface ThumbnailOnClickListenerFactory {
	OnClickListener getThumbnailOnClickListener(ThingInfo threadThingInfo, Fragment fragment);
}