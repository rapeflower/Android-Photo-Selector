package com.lily.photo.selector.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/***********
 *
 * @Author rape flower
 * @Date 2018-02-01 15:30
 * @Describe 动画工具类
 *
 */
public class AnimationUtil implements AnimationListener {

	private Animation animation;
	/**
	 * 动画完成监听器
	 */
	private OnAnimationEndListener animationEndListener;
	/**
	 * 动画开始监听器
	 */
	private OnAnimationStartListener animationStartListener;
	/**
	 * 动画重复时的监听器
	 */
	private OnAnimationRepeatListener animationRepeatListener;

	public AnimationUtil(Context context, int resId) {
		this.animation = AnimationUtils.loadAnimation(context, resId);
		this.animation.setAnimationListener(this);
	}

	/**
	 * 自定义一个Translate类型的Animation
	 *
	 * @param fromXDelta
	 * @param toXDelta
	 * @param fromYDelta
	 * @param toYDelta
	 */
	public AnimationUtil(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta,
				toYDelta);
	}

	/**
	 * 两个动画之间的时间间隔
	 *
	 * @param startOffset
	 * @return
	 */
	public AnimationUtil setStartOffset(long startOffset) {
		animation.setStartOffset(startOffset);
		return this;
	}

	/**
	 * 设置一个动画的插入器
	 *
	 * @param i
	 * @return
	 */
	public AnimationUtil setInterpolator(Interpolator i) {
		animation.setInterpolator(i);
		return this;
	}
	
	public AnimationUtil setLinearInterpolator() {
		animation.setInterpolator(new LinearInterpolator());
		return this;
	}

	/**
	 * 开始动画
	 *
	 * @param view
	 */
	public void startAnimation(View view) {
		view.startAnimation(animation);
	}

	/**
	 * 开启一个帧动画
	 *
	 * @param resId
	 * @param view
	 */
	public static void startAnimation(int resId, View view) {
		view.setBackgroundResource(resId);
		((AnimationDrawable) view.getBackground()).start();
	}

	public AnimationUtil setDuration(long durationMillis) {
		animation.setDuration(durationMillis);
		return this;
	}

	public AnimationUtil setFillAfter(boolean fillAfter) {
		animation.setFillAfter(fillAfter);
		return this;
	}

	public interface OnAnimationEndListener {
		/**
		 * 动画结束
		 * @param animation
		 */
		void onAnimationEnd(Animation animation);
	}

	public interface OnAnimationStartListener {
		/**
		 * 动画开始
		 * @param animation
		 */
		void onAnimationStart(Animation animation);
	}

	public interface OnAnimationRepeatListener {
		/**
		 * 动画重复
		 * @param animation
		 */
		void onAnimationRepeat(Animation animation);
	}

	public AnimationUtil setOnAnimationEndLinstener(
			OnAnimationEndListener listener) {
		this.animationEndListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationStartLinstener(
			OnAnimationStartListener listener) {
		this.animationStartListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationRepeatLinstener(
			OnAnimationRepeatListener listener) {
		this.animationRepeatListener = listener;
		return this;
	}

	public void setAnimationListener(AnimationListener animationListener) {
		animation.setAnimationListener(animationListener);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (this.animationStartListener != null) {
			this.animationStartListener.onAnimationStart(animation);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (this.animationEndListener != null) {
			this.animationEndListener.onAnimationEnd(animation);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		if (this.animationRepeatListener != null) {
			this.animationRepeatListener.onAnimationRepeat(animation);
		}
	}

}
