package com.lily.photo.selector.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lily.photo.selector.activity.PhotoSelectorActivity;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:29
 * @Describe 通用工具类
 *
 */
public class PhotoSelector {

	public static final String MAX_SELECT_ABLE = "maxSelectable";
	/**
	 * 图片选择的最多数量，默认是9张
	 */
	private int maxSelectableNumber = 9;
	public static PhotoSelector mInstance = null;

	private PhotoSelector() {

    }

    public static PhotoSelector getInstance() {
	    if (mInstance == null) {
	        synchronized (PhotoSelector.class) {
	            if (mInstance == null) {
	                mInstance = new PhotoSelector();
	                return mInstance;
                }
            }
        }
	    return mInstance;
    }

    public void setMaxSelectable(int maxSelectable) {
        this.maxSelectableNumber = maxSelectable;
    }

    public int maxSelectable() {
	    return this.maxSelectableNumber;
    }

    /**
     * 选择照片
     *
     * @param context
     * @param requestCode
     */
	public static void choosePhoto(Context context, int requestCode) {
		Intent intent = new Intent(context, PhotoSelectorActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
     * 拍照
	 *
	 * @param context
     * @param intent
     * @param requestCode
	 */
	public static void takePhoto(Context context, Intent intent, int requestCode) {
        ((Activity) context).startActivityForResult(intent, requestCode);
	}
}
