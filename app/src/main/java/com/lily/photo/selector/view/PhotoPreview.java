package com.lily.photo.selector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.lily.photo.selector.R;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.polites.GestureImageView;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:54
 * @Describe 预览照片的控件
 *
 */
public class PhotoPreview extends LinearLayout {

	private ProgressBar pbLoading;
	private GestureImageView ivContent;
	private Context mContext;
	private OnClickListener mOnClickListener;

	public PhotoPreview(Context context) {
		super(context);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_photo_preview, this, true);

		pbLoading = findViewById(R.id.pb_loading_vpp);
		ivContent = findViewById(R.id.iv_content_vpp);

        ivContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.iv_content_vpp && mOnClickListener != null) {
					mOnClickListener.onClick(ivContent);
				}
			}
		});
	}

	public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
		this(context);
	}

	public PhotoPreview(Context context, AttributeSet attrs) {
		this(context);
	}

	public void loadImage(PhotoModel photoModel) {
		loadImage("file://" + photoModel.getOriginalPath());
	}

    /**
     * 加载图片
     * @param path
     */
	private void loadImage(String path) {
		DrawableTypeRequest<String> drawableTypeRequest = Glide.with(mContext).load(path);
		drawableTypeRequest.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.placeholder(R.drawable.ic_picture_loading)
				.error(R.drawable.ic_picture_loadfailed)
				.crossFade();

		drawableTypeRequest.asBitmap().into(new ImageViewTarget<Bitmap>(ivContent) {
			@Override
			protected void setResource(Bitmap resource) {
				ivContent.setImageBitmap(resource);
				pbLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadFailed(Exception e, Drawable errorDrawable) {
				ivContent.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_loadfailed));
				pbLoading.setVisibility(View.GONE);
				super.onLoadFailed(e, errorDrawable);
			}
		});
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		this.mOnClickListener = listener;
	}

}
