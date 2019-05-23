package com.lily.photo.selector.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lily.photo.selector.R;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.AnimationUtil;
import com.lily.photo.selector.view.PhotoPreview;

import java.util.List;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 14:44
 * @Describe 图片预览基类
 *
 */
public class BasePhotoPreviewActivity extends Activity implements OnPageChangeListener, OnClickListener {

	public static final String KEY_PHOTOS = "photos";
	public static final String KEY_POSITION = "position";
	public static final String KEY_ALBUM = "album";
	private ViewPager mViewPager;
	private RelativeLayout rlTopTitle;
	private ImageButton btnBack;
	private TextView tvPercent;
	protected List<PhotoModel> photos;
	protected int current;
	protected boolean isUp = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_preview);
		rlTopTitle = findViewById(R.id.rl_top_title);
		btnBack = findViewById(R.id.btn_back_app);
		tvPercent = findViewById(R.id.tv_percent_app);
		mViewPager = findViewById(R.id.vp_base_app);

		btnBack.setOnClickListener(this);
		mViewPager.addOnPageChangeListener(this);

		// 渐入效果
		overridePendingTransition(R.anim.activity_alpha_action_in, 0);
	}

	/**
	 * 绑定数据，更新界面
	 */
	protected void bindData() {
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(current);
	}

	private PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public int getCount() {
			if (photos == null) {
				return 0;
			} else {
				return photos.size();
			}
		}

		@Override
		public View instantiateItem(final ViewGroup container, final int position) {
			PhotoPreview photoPreview = new PhotoPreview(BasePhotoPreviewActivity.this);
			photoPreview.loadImage(photos.get(position));
			photoPreview.setOnClickListener(photoItemClickListener);
            container.addView(photoPreview);
			return photoPreview;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_back_app && !isUp) {
			finish();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		current = arg0;
		updatePercent();
	}

	protected void updatePercent() {
		tvPercent.setText((current + 1) + "/" + photos.size());
	}

	/**
	 * 图片点击事件回调
	 */
	private OnClickListener photoItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isUp) {
				new AnimationUtil(getApplicationContext(), R.anim.translate_up)
                        .setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(rlTopTitle);
				isUp = true;
			} else {
				new AnimationUtil(getApplicationContext(), R.anim.translate_down_current)
						.setInterpolator(new LinearInterpolator()).setFillAfter(true)
                        .startAnimation(rlTopTitle);
				isUp = false;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mViewPager != null) {
			mViewPager.removeOnPageChangeListener(this);
		}
	}
}
