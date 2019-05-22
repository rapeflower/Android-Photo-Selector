package com.lily.photo.selector.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lily.photo.selector.R;
import com.lily.photo.selector.model.PhotoModel;
import com.lily.photo.selector.utils.CommonUtil;

import java.util.List;
import java.util.Random;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:06
 * @Describe 图片适配器
 *
 */
public class PhotoAdapter extends BaseAdapter{

    private List<PhotoModel> models;
    private Context mContext;
    private LayoutInflater mInflater;
    private int imageWidth;
    private int imageHeight;
    OnItemCheckedChangedListener mOnItemCheckedChangedListener;

    public interface OnItemCheckedChangedListener {
        /**
         * 条目选择改变事件
         *
         * @param chBox
         * @param isChecked
         * @param photoModel
         */
        void onItemCheckedChanged(CompoundButton chBox, boolean isChecked, PhotoModel photoModel);

        /**
         * 显示图片
         *
         * @param path
         * @param position
         */
        void onShowPicture(String path, int position);
    }

    public PhotoAdapter(Context context, List<PhotoModel> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.models = list;
        int space = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_6px);
        imageHeight = imageWidth = (CommonUtil.getWidthPixels((Activity) context) - space * 2) / 3;
    }

    /**
     *
     * @param onItemCheckedChangedListener
     */
    public void setOnItemCheckedChangedListener(OnItemCheckedChangedListener onItemCheckedChangedListener) {
        this.mOnItemCheckedChangedListener = onItemCheckedChangedListener;
    }

    @Override
    public int getCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public Object getItem(int position) {
        return models == null ? null : models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_photo, null);
            holder = new ViewHolder();
            holder.photoView = convertView.findViewById(R.id.iv_photo_selector);
            holder.cbSelect = convertView.findViewById(R.id.cb_photo_selector);
            setImageLayoutParams(holder.photoView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PhotoModel photoModel = models.get(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext).load("file://" + photoModel.getOriginalPath())
                        .placeholder(R.drawable.ic_picture_loading)
                        .error(R.drawable.ic_picture_loadfailed)
                        .crossFade()
                        .into(holder.photoView);
            }
        }, new Random().nextInt(10));

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setTag(position);
        holder.cbSelect.setChecked(photoModel.isChecked());
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int index = (int) buttonView.getTag();
                models.get(index).setChecked(isChecked);
                if (mOnItemCheckedChangedListener != null) {
                    mOnItemCheckedChangedListener.onItemCheckedChanged(buttonView, isChecked, photoModel);
                }
            }
        });

        holder.photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemCheckedChangedListener != null) {
                    String path = photoModel.getOriginalPath();
                    mOnItemCheckedChangedListener.onShowPicture(path, position);
                }
            }
        });

        return convertView;
    }

    /**
     * 设置图片的宽、高
     * @param imageView
     */
    private void setImageLayoutParams(ImageView imageView) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        lp.width = imageWidth;
        lp.height = imageHeight;
    }

    private class ViewHolder {
        ImageView photoView;
        CheckBox cbSelect;
    }
}
