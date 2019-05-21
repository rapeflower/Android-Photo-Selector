package com.lily.photo.selector.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lily.photo.selector.R;
import com.lily.photo.selector.model.PhotoModel;

import java.util.Random;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:55
 * @Describe 照片控件
 *
 */
public class PhotoItem extends LinearLayout implements OnCheckedChangeListener, OnClickListener {

	private ImageView ivPhoto;
	private CheckBox cbPhoto;
	private Context mContext;
	private onPhotoItemCheckedListener listener;
	private PhotoModel photo;
	private boolean isCheckChange;
	private onItemClickListener l;
	private int position;

	private PhotoItem(Context context) {
		super(context);
	}

	public PhotoItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PhotoItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PhotoItem(Context context, onPhotoItemCheckedListener listener) {
		this(context);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.item_photo, this, true);
		this.listener = listener;

		ivPhoto = findViewById(R.id.iv_photo_selector);
		cbPhoto = findViewById(R.id.cb_photo_selector);

		// CheckBox选中状态改变监听器
		cbPhoto.setOnCheckedChangeListener(this);
		setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (listener != null && !isCheckChange) {
            // 调用主界面回调函数
			listener.onCheckedChanged(photo, buttonView, isChecked);
		}
		android.util.Log.w("log", " isChecked = " + isChecked);
		// 让图片变暗或者变亮
		if (isChecked) {
			setDrawingEnable();
			ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		} else {
			ivPhoto.clearColorFilter();
		}
		photo.setChecked(isChecked);
	}

	/**
	 * 设置路径下的图片对应的缩略图
	 * @param photo
	 */
	public void setImageDrawable(final PhotoModel photo) {
		this.photo = photo;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				    Glide.with(mContext).load("file://" + photo.getOriginalPath())
							.placeholder(R.drawable.ic_picture_loading)
							.error(R.drawable.ic_picture_loadfailed)
							.diskCacheStrategy(DiskCacheStrategy.SOURCE)
							.crossFade()
							.into(ivPhoto);
			}
		}, new Random().nextInt(10));
	}

	private void setDrawingEnable() {
		ivPhoto.setDrawingCacheEnabled(true);
		ivPhoto.buildDrawingCache();
	}

	@Override
	public void setSelected(boolean selected) {
		if (photo == null) {
			return;
		}
		isCheckChange = true;
		cbPhoto.setChecked(selected);
		isCheckChange = false;
	}

	public void setCanContinueChecked(boolean canChecked) {
		//cbPhoto.setEnabled(canChecked);
	}

	public void setOnClickListener(onItemClickListener l, int position) {
		this.l = l;
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		if (l != null) {
			l.onItemClick(position);
		}
	}

	/**
	 * 图片Item选中事件监听器
	 */
	public interface onPhotoItemCheckedListener {
		void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked);
	}

	/**
	 * 图片点击事件
	 */
	public interface onItemClickListener {
		void onItemClick(int position);
	}

}
