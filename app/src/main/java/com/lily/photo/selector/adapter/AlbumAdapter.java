package com.lily.photo.selector.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lily.photo.selector.model.AlbumModel;
import com.lily.photo.selector.view.AlbumItem;

import java.util.ArrayList;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:03
 * @Describe 相册适配器
 *
 */
public class AlbumAdapter extends XBaseAdapter<AlbumModel> {

	public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
		super(context, models);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlbumItem albumItem = null;
		if (convertView == null) {
			albumItem = new AlbumItem(context);
			convertView = albumItem;
		} else {
			albumItem = (AlbumItem) convertView;
		}
		albumItem.update(models.get(position));
		return convertView;
	}

}
