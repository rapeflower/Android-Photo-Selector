package com.lily.photo.selector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lily.photo.selector.R;
import com.lily.photo.selector.adapter.AlbumAdapter;
import com.lily.photo.selector.adapter.PhotoSelectorAdapter;
import com.lily.photo.selector.manager.PhotoSelectorManager;
import com.lily.photo.selector.model.AlbumModel;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.AnimationUtil;
import com.lily.photo.selector.utils.CommonUtil;
import com.lily.photo.selector.view.PhotoItem.onItemClickListener;
import com.lily.photo.selector.view.PhotoItem.onPhotoItemCheckedListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PhotoSelectorActivity extends Activity implements onItemClickListener, onPhotoItemCheckedListener,
		OnItemClickListener, OnClickListener {

	public static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_CAMERA = 1;

	public static final String RECENT_PHOTO = "最近照片";

	private GridView gvPhotos;
	private ListView lvAlbum;
	private Button btnOk;
	private TextView tvAlbum, tvPreview, tvTitle;
	private PhotoSelectorManager photoSelectorDomain;
	private PhotoSelectorAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private RelativeLayout layoutAlbum;
	private ArrayList<PhotoModel> photoData;
	private ArrayList<PhotoModel> selected;
	private int maxSelectable = 9;//图片选择的最多数量，默认是9张
	private boolean isReset = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_photoselector);

		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder() //
				.considerExifParams(true) // 调整图片方向
				.resetViewBeforeLoading(true) // 载入之前重置ImageView
				.showImageOnLoading(R.drawable.ic_picture_loading) // 载入时图片设置为黑色
				.showImageOnFail(R.drawable.ic_picture_loadfailed) // 加载失败时显示的图片
				.delayBeforeLoading(0) // 载入之前的延迟时间
				.build(); //
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultDisplayImageOptions).memoryCacheExtraOptions(480, 800)
				.threadPoolSize(5).build();
		ImageLoader.getInstance().init(config);

		photoSelectorDomain = new PhotoSelectorManager(getApplicationContext());

		photoData = new ArrayList<PhotoModel>();
		selected = new ArrayList<PhotoModel>();

		tvTitle = (TextView) findViewById(R.id.tv_title_lh);
		gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
		lvAlbum = (ListView) findViewById(R.id.lv_album_ar);
		btnOk = (Button) findViewById(R.id.btn_right_lh);
		tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
		tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
		layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);

		btnOk.setOnClickListener(this);
		tvAlbum.setOnClickListener(this);
		tvPreview.setOnClickListener(this);

		photoAdapter = new PhotoSelectorAdapter(getApplicationContext(), new ArrayList<PhotoModel>(),
				CommonUtil.getWidthPixels(this), this, this, this);
		gvPhotos.setAdapter(photoAdapter);

		albumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
		lvAlbum.setAdapter(albumAdapter);
		lvAlbum.setOnItemClickListener(this);

		findViewById(R.id.bv_back_lh).setOnClickListener(this); //  返回

		photoSelectorDomain.getRecentPhoto(recentListener); //  更新最近照片
		photoSelectorDomain.getAlbums(albumListener); // 更新相册信息
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_right_lh)
			ok();  // 选完照片
		else if (v.getId() == R.id.tv_album_ar)
			album();
		else if (v.getId() == R.id.tv_preview_ar)
			preview();
		else if (v.getId() == R.id.tv_camera_vc)
			catchPicture();
		else if (v.getId() == R.id.bv_back_lh)
			finish();
	}

	/**
	 * 拍照
	 */
	private void catchPicture() {
		CommonUtil.launchActivityForResult(this, new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
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
			if (selected.size() > maxSelectable)  {
				Toast.makeText(this, "你最多只能选择9张图片", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("photos", selected);
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
		bundle.putSerializable("photos", selected);
		CommonUtil.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	private void album() {
		if (layoutAlbum.getVisibility() == View.GONE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}

	/**
	 * 弹出相册列表
	 */
	private void popAlbum() {
		layoutAlbum.setVisibility(View.VISIBLE);
		new AnimationUtil(getApplicationContext(), R.anim.translate_up_current).setLinearInterpolator().startAnimation(
				layoutAlbum);
	}

	/**
	 * 隐藏相册列表
	 */
	private void hideAlbum() {
		new AnimationUtil(getApplicationContext(), R.anim.translate_down).setLinearInterpolator().startAnimation(
				layoutAlbum);
		layoutAlbum.setVisibility(View.GONE);
	}

	/**
	 * 清空选中的图片
	 */
	private void reset() {
		selected.clear();
		tvPreview.setText("预览");
		tvPreview.setEnabled(false);
	}

	/**
	 * 点击查看照片
	 *
	 * @param position
	 */
	@Override
	public void onItemClick(int position) {
//		if (selected.size() >= maxSelectable)  {
//			if (photoData == null || photoData.size() == 0) return;
//			PhotoModel pm = photoData.get(position);
//			if (pm != null && !pm.isEnabled()) {
//				Toast.makeText(this, "你最多只能选择9张图片", Toast.LENGTH_SHORT).show();
//				return;
//			}
//		}
		Bundle bundle = new Bundle();
		if (tvAlbum.getText().toString().equals(RECENT_PHOTO)) {
			bundle.putInt("position", position - 1);
		} else {
			bundle.putInt("position", position);
		}
		bundle.putString("album", tvAlbum.getText().toString());
		CommonUtil.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	/**
	 * 照片选中状态改变之后
	 *
	 * @param photoModel
	 * @param buttonView
	 * @param isChecked
	 */
	@Override
	public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			selected.add(photoModel);
			tvPreview.setEnabled(true);
		} else {
			selected.remove(photoModel);
		}
		tvPreview.setText("预览(" + selected.size() + ")");   //修改预览数量

		if (selected.isEmpty()) {
			tvPreview.setEnabled(false);
			tvPreview.setText("预览");
		}

//		if (selected.size() >= maxSelectable) {
//            if (photoData == null || photoData.size() == 0) return;
//			isReset = true;
//
//			for (PhotoModel pd : photoData) {
//				if (pd == null) continue;
//				String path = pd.getOriginalPath();
//				if (TextUtils.isEmpty(path)) continue;
//				for (PhotoModel selectPd : selected) {
//					if (selectPd == null) continue;
//					if (path.equals(selectPd.getOriginalPath())) {
//						pd.setEnabled(true);
//						break;
//					} else {
//						pd.setEnabled(false);
//					}
//				}
//			}
//			photoAdapter.update(photoData);
//		} else {
//			if (isReset) {
//				for (PhotoModel pd : photoData) {
//					if (pd == null) continue;
//					pd.setEnabled(true);
//				}
//				photoAdapter.update(photoData);
//
//				isReset = false;
//			}
//		}
	}

	@Override
	public void onBackPressed() {
		if (layoutAlbum.getVisibility() == View.VISIBLE) {
			hideAlbum();
		} else
			super.onBackPressed();
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
			if (i == position)
				album.setCheck(true);
			else
				album.setCheck(false);
		}
		albumAdapter.notifyDataSetChanged();
		hideAlbum();
		tvAlbum.setText(current.getName());
		tvTitle.setText(current.getName());

		// 更新照片列表
		if (current.getName().equals(RECENT_PHOTO))
			photoSelectorDomain.getRecentPhoto(recentListener);
		else
			photoSelectorDomain.getAlbumPhoto(current.getName(), recentListener); // 获取选中相册的照片
	}

	/**
	 * 设置最多可选择图片的数量
	 *
	 * @param maxSelectable
	 */
	public void setMaxSelectable(int maxSelectable) {
		this.maxSelectable = maxSelectable;
	}

	/**
	 * 获取本地图库照片回调
	 */
	public interface OnLocalRecentListener {
		public void onPhotoLoaded(List<PhotoModel> photos);
	}

	/**
	 * 获取本地相册信息回调
	 */
	public interface OnLocalAlbumListener {
		public void onAlbumLoaded(List<AlbumModel> albums);
	}

	private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
		@Override
		public void onAlbumLoaded(List<AlbumModel> albums) {
			albumAdapter.update(albums);
		}
	};

	private OnLocalRecentListener recentListener = new OnLocalRecentListener() {
		@Override
		public void onPhotoLoaded(List<PhotoModel> photos) {
			if (tvAlbum.getText().equals(RECENT_PHOTO)) {
				photos.add(0, new PhotoModel());
			}
			photoData.clear();
			photoData.addAll(photos);
			photoAdapter.update(photoData);
			gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
			reset();
		}
	};
}
