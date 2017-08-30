package com.lily.photo.selector.ui;

import android.os.Bundle;

import com.lily.photo.selector.manager.PhotoSelectorManager;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.CommonUtil;

import java.util.List;

public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements PhotoSelectorActivity.OnLocalRecentListener {

	private PhotoSelectorManager photoSelectorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		photoSelectorManager = new PhotoSelectorManager(getApplicationContext());
		init(getIntent().getExtras());
	}

	@SuppressWarnings("unchecked")
	protected void init(Bundle extras) {
		if (extras == null)
			return;

		if (extras.containsKey("photos")) { // 预览图片
			photos = (List<PhotoModel>) extras.getSerializable("photos");
			current = extras.getInt("position", 0);
			updatePercent();
			bindData();
		} else if (extras.containsKey("album")) { // 点击图片查看
			String albumName = extras.getString("album"); // 相册
			this.current = extras.getInt("position");
			if (!CommonUtil.isNull(albumName) && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
				photoSelectorManager.getRecentPhoto(this);
			} else {
				photoSelectorManager.getAlbumPhoto(albumName, this);
			}
		}
	}

	@Override
	public void onPhotoLoaded(List<PhotoModel> photos) {
		this.photos = photos;
		updatePercent();
		bindData(); // 更新界面
	}

}
