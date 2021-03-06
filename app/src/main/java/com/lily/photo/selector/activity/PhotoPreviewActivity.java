package com.lily.photo.selector.activity;

import android.os.Bundle;

import com.lily.photo.selector.manager.PhotoSelectorManager;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.CommonUtil;

import java.util.List;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 14:45
 * @Describe 图片预览
 *
 */
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
		if (extras == null) {
			return;
		}
		// 预览图片
		if (extras.containsKey(KEY_PHOTOS)) {
			photos = (List<PhotoModel>) extras.getSerializable(KEY_PHOTOS);
			current = extras.getInt(KEY_POSITION, 0);
			updatePercent();
			bindData();
		} else if (extras.containsKey(KEY_ALBUM)) {
			// 点击图片查看
			// 相册
			String albumName = extras.getString(KEY_ALBUM);
			this.current = extras.getInt(KEY_POSITION);
			if (!CommonUtil.isNull(albumName) && albumName.equals(PhotoSelectorActivity.RECENT_PHOTO)) {
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
		// 更新界面
		bindData();
	}

}
