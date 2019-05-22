package com.lily.photo.selector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lily.photo.selector.R;
import com.lily.photo.selector.adapter.AlbumAdapter;
import com.lily.photo.selector.adapter.PhotoAdapter;
import com.lily.photo.selector.manager.PhotoSelectorManager;
import com.lily.photo.selector.model.AlbumModel;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.AnimationUtil;
import com.lily.photo.selector.utils.CommonUtil;
import com.lily.photo.selector.utils.PhotoSelector;

import java.util.ArrayList;
import java.util.List;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:00
 * @Describe 选择图片
 *
 */
public class PhotoSelectorActivity extends Activity implements OnItemClickListener, OnClickListener {

	public static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_CAMERA = 1;
	public static final String RECENT_PHOTO = "最近照片";

	private GridView gvPhotos;
	private ListView lvAlbum;
	private Button btnOk;
	private TextView tvAlbumAr, tvPreview, tvTitle;
	private ImageView ivAlbumAr;
	private PhotoSelectorManager photoSelectorDomain;
	private PhotoAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private ArrayList<PhotoModel> photoData;
	private ArrayList<PhotoModel> selected;
	/**
	 * 图片选择的最多数量
	 */
	private int maxSelectable = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_selector);
		maxSelectable = PhotoSelector.getInstance().maxSelectable();

		photoSelectorDomain = new PhotoSelectorManager(getApplicationContext());
		photoData = new ArrayList<>();
		selected = new ArrayList<>();

		tvTitle = findViewById(R.id.tv_title_lh);
		gvPhotos = findViewById(R.id.gv_photos_ar);
		lvAlbum = findViewById(R.id.lv_album_ar);
		btnOk = findViewById(R.id.btn_right_lh);
        tvAlbumAr = findViewById(R.id.tv_album_ar);
        ivAlbumAr = findViewById(R.id.iv_album_ar);
		tvPreview = findViewById(R.id.tv_preview_ar);

		btnOk.setOnClickListener(this);
        tvAlbumAr.setOnClickListener(this);
        ivAlbumAr.setOnClickListener(this);
		tvPreview.setOnClickListener(this);

		photoAdapter = new PhotoAdapter(PhotoSelectorActivity.this, photoData);
		gvPhotos.setAdapter(photoAdapter);
		photoAdapter.setOnItemCheckedChangedListener(new PhotoAdapter.OnItemCheckedChangedListener() {
			@Override
			public void onItemCheckedChanged(CompoundButton chBox, boolean isChecked, PhotoModel photoModel) {
				if (isChecked) {
					if (getSelectedPhotoCount() > maxSelectable - 1) {
						Toast.makeText(PhotoSelectorActivity.this, "你最多只能选择" + maxSelectable + "张图片", Toast.LENGTH_SHORT).show();
						chBox.setChecked(false);
						photoModel.setChecked(false);
						selected.remove(photoModel);
					} else {
						selected.add(photoModel);
					}
				} else {
					selected.remove(photoModel);
				}

				updatePreview();
			}

			@Override
			public void onShowPicture(String path, int position) {
				Bundle bundle = new Bundle();
				bundle.putInt(BasePhotoPreviewActivity.KEY_POSITION, position);
				bundle.putString(BasePhotoPreviewActivity.KEY_ALBUM, tvAlbumAr.getText().toString());
				startActivity(PhotoPreviewActivity.class, bundle);
			}
		});

		albumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
		lvAlbum.setAdapter(albumAdapter);
		lvAlbum.setOnItemClickListener(this);

		// 返回
		findViewById(R.id.iv_back_vb).setOnClickListener(this);

		// 更新最近照片
		photoSelectorDomain.getRecentPhoto(recentListener);
		// 更新相册信息
		photoSelectorDomain.getAlbums(albumListener);
	}

	private int getSelectedPhotoCount() {
		return selected == null ? 0 : selected.size();
	}

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	        case R.id.btn_right_lh:
                // 选完照片
                ok();
	            break;
            case R.id.tv_album_ar:
            case R.id.iv_album_ar:
                album();
                break;
            case R.id.tv_preview_ar:
                preview();
                break;
            case R.id.iv_back_vb:
                finish();
                break;
        }
	}

	/**
	 * 拍照
	 */
	private void takePhoto() {
		PhotoSelector.takePhoto(this, new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			PhotoModel photoModel = new PhotoModel(CommonUtil.query(getApplicationContext(), data.getData()));
			selected.clear();
			selected.add(photoModel);
			ok();
		}
	}

	/**
	 * 完成
	 */
	private void ok() {
		if (selected.isEmpty()) {
			setResult(RESULT_CANCELED);
		} else {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable(BasePhotoPreviewActivity.KEY_PHOTOS, selected);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
		}
		finish();
	}

	/**
	 * 预览照片
	 */
	private void preview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(BasePhotoPreviewActivity.KEY_PHOTOS, selected);
		startActivity(PhotoPreviewActivity.class, bundle);
	}

    /**
     * 相册
     */
	private void album() {
		if (lvAlbum.getVisibility() == View.GONE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}

	/**
	 * 弹出相册列表
	 */
	private void popAlbum() {
        lvAlbum.setVisibility(View.VISIBLE);
		new AnimationUtil(getApplicationContext(), R.anim.translate_up_current)
                .setLinearInterpolator()
                .startAnimation(lvAlbum);
	}

	/**
	 * 隐藏相册列表
	 */
	private void hideAlbum() {
		new AnimationUtil(getApplicationContext(), R.anim.translate_down)
                .setLinearInterpolator()
                .startAnimation(lvAlbum);
        lvAlbum.setVisibility(View.GONE);
	}

	/**
	 * 清空选中的图片
	 */
	private void reset() {
		selected.clear();
		tvPreview.setText(getResources().getString(R.string.psl_preview));
		tvPreview.setEnabled(false);
	}

	/**
	 * 跳转
	 * @param activity
	 * @param bundle
	 */
	public void startActivity(Class<?> activity, Bundle bundle) {
		Intent intent = new Intent(this, activity);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}

    /**
     * 修改预览数量
     */
	private void updatePreview() {
        if (selected.isEmpty()) {
            tvPreview.setEnabled(false);
            tvPreview.setText(getResources().getString(R.string.psl_preview));
        } else {
            tvPreview.setEnabled(true);
            tvPreview.setText(getResources().getString(R.string.psl_preview) + "(" + selected.size() + ")");
        }
    }

	@Override
	public void onBackPressed() {
		if (lvAlbum.getVisibility() == View.VISIBLE) {
			hideAlbum();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 相册列表点击事件
	 *
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
		for (int i = 0; i < parent.getCount(); i++) {
			AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
			if (i == position) {
				album.setCheck(true);
			} else {
				album.setCheck(false);
			}
		}
		albumAdapter.notifyDataSetChanged();
		hideAlbum();
        tvAlbumAr.setText(current.getName());
		tvTitle.setText(current.getName());

		// 更新照片列表
		if (current.getName().equals(RECENT_PHOTO)) {
			photoSelectorDomain.getRecentPhoto(recentListener);
		} else {
			// 获取选中相册的照片
			photoSelectorDomain.getAlbumPhoto(current.getName(), recentListener);
		}
	}

	/**
	 * 获取本地图库照片回调
	 */
	public interface OnLocalRecentListener {
		/**
		 * 获取本地图片
		 *
		 * @param photos
		 */
		void onPhotoLoaded(List<PhotoModel> photos);
	}

	/**
	 * 获取本地相册信息回调
	 */
	public interface OnLocalAlbumListener {
		/**
		 * 获取本地相册
		 *
		 * @param albums
		 */
		void onAlbumLoaded(List<AlbumModel> albums);
	}

    /**
     * 本地相册 监听
     */
	private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
		@Override
		public void onAlbumLoaded(List<AlbumModel> albums) {
			albumAdapter.notifyDataSetChanged(albums);
		}
	};

    /**
     * "最近照片" 监听
     */
	private OnLocalRecentListener recentListener = new OnLocalRecentListener() {
		@Override
		public void onPhotoLoaded(List<PhotoModel> photos) {
			photoData.clear();
			photoData.addAll(photos);
			photoAdapter.notifyDataSetChanged();
			// 滚动到顶端
			gvPhotos.smoothScrollToPosition(0);
			reset();
		}
	};
}
