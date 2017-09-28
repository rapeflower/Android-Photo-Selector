package com.lily.photo.selector.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by lilei on 2017/9/28.
 */
public class ViewHolderHelper {

    /**
     * 获取缓存View
     *
     * @param convertView 所有缓存View的根View
     * @param id 缓存View的唯一标识
     * @param <T>
     * @return
     */
    public static <T extends View> T get(View convertView, int id) {
        SparseArray<View> viewSparseArray = (SparseArray<View>) convertView.getTag();
        //如果convertView没有用来缓存View的集合
        if (viewSparseArray == null) {
            viewSparseArray = new SparseArray<View>();
            convertView.setTag(viewSparseArray);
        }

        //获取convertView储存在集合中的子view
        View childView = viewSparseArray.get(id);
        //如果没有该id对应的子view缓存在viewSparseArray中
        if (childView == null) {
            //findViewById()找到该view
            childView = convertView.findViewById(id);
            //保存到集合中
            viewSparseArray.put(id, childView);
        }

        return (T) childView;
    }
}
