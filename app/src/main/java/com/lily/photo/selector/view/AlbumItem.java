package com.lily.photo.selector.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lily.photo.selector.R;
import com.lily.photo.selector.model.AlbumModel;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:54
 * @Describe 相册控件
 *
 */
public class AlbumItem extends LinearLayout {

	private ImageView ivAlbum, ivIndex;
	private TextView tvName, tvCount;
	private Context mContext;

	public AlbumItem(Context context) {
		this(context, null);
	}

	public AlbumItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.item_album, this, true);

		ivAlbum = findViewById(R.id.iv_album);
		ivIndex = findViewById(R.id.iv_index);
		tvName = findViewById(R.id.tv_album_name);
		tvCount = findViewById(R.id.tv_photo_count);
	}

	public AlbumItem(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	/**
	 * 设置相册封面
	 * @param path
	 */
	public void setAlbumImage(String path) {
		 Glide.with(mContext).load("file://" + path)
				 .placeholder(R.drawable.ic_picture_loading)
				 .error(R.drawable.ic_picture_loadfailed)
				 .crossFade().into(ivAlbum);
	}

	/**
	 * 更新"相册"信息
	 *
	 * @param album
	 */
	public void update(AlbumModel album) {
		setAlbumImage(album.getRecent());
		setName(album.getName());
		setCount(album.getCount());
		isCheck(album.isCheck());
	}

	public void setName(CharSequence title) {
		tvName.setText(title);
	}

	public void setCount(int count) {
		tvCount.setText(count + "张");
	}

	public void isCheck(boolean isCheck) {
		if (isCheck) {
			ivIndex.setVisibility(View.VISIBLE);
		} else {
			ivIndex.setVisibility(View.GONE);
		}
	}

}
