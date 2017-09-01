package com.lily.photo.selector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.lily.photo.selector.R;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.CommonUtil;
import com.lily.photo.selector.view.PhotoItem;
import com.lily.photo.selector.view.PhotoItem.onItemClickListener;
import com.lily.photo.selector.view.PhotoItem.onPhotoItemCheckedListener;

import java.util.ArrayList;

public class PhotoSelectorAdapter extends MBaseAdapter<PhotoModel> {

	private int itemWidth;
	private int horizontalNum = 3;
	private int maxSelectable = 9;//图片选择的最多数量，默认是9张
	private int currentSelectNum = 0;//当前选中的图片数量
	private onPhotoItemCheckedListener listener;
	private LayoutParams itemLayoutParams;
	private onItemClickListener mCallback;
	private OnClickListener cameraListener;

	private PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models) {
		super(context, models);
	}

	public PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth,
			onPhotoItemCheckedListener listener, onItemClickListener mCallback, OnClickListener cameraListener) {
		this(context, models);
		setItemWidth(screenWidth);
		this.listener = listener;
		this.mCallback = mCallback;
		this.cameraListener = cameraListener;
	}

	/**
	 * 设置每一个Item的宽高
	 *
	 * @param screenWidth 屏幕宽度
	 */
	public void setItemWidth(int screenWidth) {
		int horizontalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
		this.itemWidth = (screenWidth - (horizontalSpace * (horizontalNum - 1))) / horizontalNum;
		this.itemLayoutParams = new LayoutParams(itemWidth, itemWidth);
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
	 * 当前选中的图片数量
	 *
	 * @param currentSelectNum
	 */
	public void setCurrentSelectNum(int currentSelectNum) {
		this.currentSelectNum = currentSelectNum;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoItem item = null;
		TextView tvCamera = null;
		if (position == 0 && CommonUtil.isNull(models.get(position).getOriginalPath())) { // 当时第一个时，显示按钮
			if (convertView == null || !(convertView instanceof TextView)) {
				tvCamera = (TextView) LayoutInflater.from(context).inflate(R.layout.view_camera, null);
				tvCamera.setHeight(itemWidth);
				tvCamera.setWidth(itemWidth);
				convertView = tvCamera;
			}
			convertView.setOnClickListener(cameraListener);
		} else {  // 显示图片
			if (convertView == null || !(convertView instanceof PhotoItem)) {
				item = new PhotoItem(context, listener);
				item.setLayoutParams(itemLayoutParams);
				convertView = item;
			} else {
				item = (PhotoItem) convertView;
			}
            item.setTag(position);

			item.setImageDrawable(models.get(position));
			item.setSelected(models.get(position).isChecked());
            int index = (int) item.getTag();
            if (index == position) {
                //android.util.Log.w("log", " position = " + position + ", isEnabled = " + models.get(position).isEnabled());
                item.setCanContinueChecked(models.get(position).isEnabled());
            }
			item.setOnClickListener(mCallback, position);
		}
		return convertView;
	}
}
