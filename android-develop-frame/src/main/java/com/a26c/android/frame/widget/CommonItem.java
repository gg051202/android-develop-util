/**
 *
 */
package com.a26c.android.frame.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a26c.android.frame.R;
import com.a26c.android.frame.util.FrameDensityUtils;

public class CommonItem extends RelativeLayout {

	private Context context;
	public TextView leftTextView, rightTextView;
	public ImageView leftImageView, rightImageView;
	public View topDividerView, bottomDividerView;
	public RelativeLayout parentLayout;

	public CommonItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.frame_layout_common_item, this, true);

		leftTextView = (TextView) findViewById(R.id.leftTextView);
		rightTextView = (TextView) findViewById(R.id.rightTextView);
		rightImageView = (ImageView) findViewById(R.id.rightImageView);
		leftImageView = (ImageView) findViewById(R.id.leftImageView);
		topDividerView = findViewById(R.id.topDividerView);
		bottomDividerView = findViewById(R.id.bottomDividerView);
		parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonItem);
		//获取默认的字体大小，尺寸是px，再转成sp
		setLeftText(array.getString(R.styleable.CommonItem_commonLeftText),
				array.getInteger(R.styleable.CommonItem_commonLeftTextSize, 15),
				array.getColor(R.styleable.CommonItem_commonLeftTextColor, 0xff2B2B2B));
		setRightText(array.getString(R.styleable.CommonItem_commonRightText),
				array.getInteger(R.styleable.CommonItem_commonRightTextSize, 15),
				array.getColor(R.styleable.CommonItem_commonRightTextColor, 0xff868686));

		int defWidth = FrameDensityUtils.dp2px(context, 28);
		setLeftImageByPx(array.getResourceId(R.styleable.CommonItem_commonLeftImage, 0),
				array.getDimension(R.styleable.CommonItem_commonLeftImageWidth, defWidth),
				array.getDimension(R.styleable.CommonItem_commonLeftImageHeight, defWidth),
				(int) array.getDimension(R.styleable.CommonItem_commonLeftMargin, FrameDensityUtils.dp2px(context, 6)));

		setRightImageByPx(array.getResourceId(R.styleable.CommonItem_commonRightImage, 0),
				array.getDimension(R.styleable.CommonItem_commonRightImageWidth, FrameDensityUtils.dp2px(context, 10)),
				array.getDimension(R.styleable.CommonItem_commonRightImageHeight, FrameDensityUtils.dp2px(context, 15)),
				(int) array.getDimension(R.styleable.CommonItem_commonRightMargin, FrameDensityUtils.dp2px(context, 6)));

		setPaddingCommonItemByPx((int) array.getDimension(R.styleable.CommonItem_commonPaddingLeft, FrameDensityUtils.dp2px(context, 12)),
				(int) array.getDimension(R.styleable.CommonItem_commonPaddingTop, 0),
				(int) array.getDimension(R.styleable.CommonItem_commonPaddingRight, FrameDensityUtils.dp2px(context, 8)),
				(int) array.getDimension(R.styleable.CommonItem_commonPaddingBottom, 0));

		setHeightByPx((int) array.getDimension(R.styleable.CommonItem_commonHeight, FrameDensityUtils.dp2px(context, 50)));
		setTopDividerVisable(array.getBoolean(R.styleable.CommonItem_commonShowTopDivider, false));
		setBottomDividerVisable(array.getBoolean(R.styleable.CommonItem_commonShowBottomDivider, false));
		setClickColorChange(array.getBoolean(R.styleable.CommonItem_commonClickColor, false));
		array.recycle();

	}

	/**
	 * 设置左边文字
	 */
	public CommonItem setLeftText(String text) {
		leftTextView.setText(text);
		return this;
	}

	/**
	 * 设置右边文字
	 */
	public CommonItem setRightText(String text) {
		rightTextView.setText(text);
		return this;
	}

	/**
	 * 设置左边文字颜色
	 */
	public CommonItem setLeftTextColor(int colorId) {
		leftTextView.setTextColor(colorId);
		return this;
	}

	/**
	 * 设置右边文字颜色
	 */
	public CommonItem setRightTextColor(int colorId) {
		rightTextView.setTextColor(colorId);
		return this;
	}

	/**
	 * 设置左边文字字体大小
	 */
	public CommonItem setLeftTextSize(float size) {
		leftTextView.setTextSize(size);
		return this;
	}

	/**
	 * 设置右边文字字体大小
	 */
	public CommonItem setRightTextSize(float size) {
		rightTextView.setTextSize(size);
		return this;
	}

	/**
	 * 设置CommonItem的高度
	 */
	public CommonItem setHeight(int height) {
		setHeightByPx(FrameDensityUtils.dp2px(context, height));
		return this;
	}

	/**
	 * 设置CommonItem的高度
	 */
	private CommonItem setHeightByPx(int height) {
		android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) parentLayout
				.getLayoutParams();
		lp.height = height;
		parentLayout.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置commonItem的内边距
	 */
	public CommonItem setPaddingCommonItem(int left, int top, int right, int botom) {
		setPaddingCommonItemByPx(FrameDensityUtils.dp2px(context, left), FrameDensityUtils.dp2px(context, top),
				FrameDensityUtils.dp2px(context, right), FrameDensityUtils.dp2px(context, botom));
		return this;
	}

	private CommonItem setPaddingCommonItemByPx(int left, int top, int right, int botom) {
		parentLayout.setPadding(left, top, right, botom);
		return this;
	}

	/**
	 * 设置左边显示的图片,单位都是dp
	 *
	 * @param imgId
	 * 		图片资源
	 * @param height
	 * 		图片高度
	 * @param width
	 * 		图片宽度
	 * @param margin
	 * 		左边的图片距离左边的文字的距离
	 */
	public CommonItem setLeftImage(int imgId, float width, float height, Integer margin) {
		setLeftImageByPx(imgId, FrameDensityUtils.dp2px(context, width), FrameDensityUtils.dp2px(context, height), FrameDensityUtils.dp2px(context,
				margin));
		return this;
	}

	private CommonItem setLeftImageByPx(int imgId, float width, float height, Integer margin) {
		leftImageView.setImageResource(imgId);
		LayoutParams lp = (LayoutParams) leftImageView.getLayoutParams();
		lp.height = (int) height;
		lp.width = (int) width;
		if (margin != null)
			lp.rightMargin = margin;
		leftImageView.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置左边显示的图片,单位都是dp
	 *
	 * @param imgId
	 * 		图片资源
	 * @param height
	 * 		图片高度
	 * @param width
	 * 		图片宽度
	 * @param margin
	 * 		左边的图片距离左边的文字的距离
	 */
	public CommonItem setRightImage(int imgId, float width, float height, Integer margin) {
		setRightImageByPx(imgId, FrameDensityUtils.dp2px(context, width), FrameDensityUtils.dp2px(context, height), FrameDensityUtils.dp2px(context,
				margin));
		return this;
	}

	private CommonItem setRightImageByPx(int imgId, float width, float height, Integer margin) {
		rightImageView.setImageResource(imgId);
		LayoutParams lp = (LayoutParams) rightImageView.getLayoutParams();
		lp.height = (int) height;
		lp.width = (int) width;
		if (margin != null)
			lp.leftMargin = margin;
		rightImageView.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置左边图片的宽高和margin
	 *
	 * @param height
	 * 		图片高度
	 * @param width
	 * 		图片宽度
	 */
	public CommonItem setLeftImage(float width, float height) {

		LayoutParams lp = (LayoutParams) leftImageView.getLayoutParams();
		lp.height = FrameDensityUtils.dp2px(context, height);
		lp.width = FrameDensityUtils.dp2px(context, width);
		leftImageView.setLayoutParams(lp);
		return this;
	}

	public CommonItem setRightImage(int imgId) {
		rightImageView.setImageResource(imgId);
		return this;
	}

	public CommonItem setLeftImage(int imgId) {
		leftImageView.setImageResource(imgId);
		return this;
	}

	/**
	 * 设置右边图片的宽高和margin
	 *
	 * @param height
	 * 		图片高度
	 * @param width
	 * 		图片宽度
	 * @param rightMargin
	 * 		右边的图片距离右边边的文字的距离
	 */
	public CommonItem setRightImage(float width, float height, Integer rightMargin) {
		LayoutParams lp = (LayoutParams) rightImageView.getLayoutParams();
		lp.height = FrameDensityUtils.dp2px(context, height);
		lp.width = FrameDensityUtils.dp2px(context, width);
		if (rightMargin != null)
			lp.leftMargin = FrameDensityUtils.dp2px(context, rightMargin);
		rightImageView.setLayoutParams(lp);
		return this;
	}

	public CommonItem setRightImage(float width, float height) {
		LayoutParams lp = (LayoutParams) rightImageView.getLayoutParams();
		lp.height = FrameDensityUtils.dp2px(context, height);
		lp.width = FrameDensityUtils.dp2px(context, width);
		rightImageView.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置顶部分割线是否可见
	 */
	public CommonItem setTopDividerVisable(boolean b) {
		topDividerView.setVisibility(b ? View.VISIBLE : View.GONE);
		return this;
	}

	/**
	 * 设置底部分割线是否可见
	 */
	public CommonItem setBottomDividerVisable(boolean b) {
		bottomDividerView.setVisibility(b ? View.VISIBLE : View.GONE);
		return this;
	}

	/**
	 * 设置左边的文字 、字体大小、 颜色
	 *
	 * @param text
	 * 		文字
	 * @param size
	 * 		字体大小
	 * @param color
	 * 		字体颜色
	 */
	public CommonItem setLeftText(String text, int size, int color) {
		setLeftText(text);
		setLeftTextColor(color);
		setLeftTextSize(size);
		return this;
	}

	public CommonItem setLeftText(Spanned text, int size, int color) {
		leftTextView.setText(text);
		setLeftTextColor(color);
		setLeftTextSize(size);
		return this;
	}


	/**
	 * 设置右边的文字 、字体大小、 颜色
	 *
	 * @param text
	 * 		文字
	 * @param size
	 * 		字体大小
	 * @param color
	 * 		字体颜色
	 */
	public CommonItem setRightText(String text, int size, int color) {
		setRightText(text);
		setRightTextColor(color);
		setRightTextSize(size);
		return this;
	}

	/**
	 * 设置左边文字的最大宽度
	 *
	 * @param width
	 */
	public CommonItem setLeftTextMaxWidth(float width) {
		leftTextView.setMaxWidth(FrameDensityUtils.dp2px(context, width));
		return this;
	}

	/**
	 * 设置右边文字的字体
	 *
	 * @param tf
	 */
	public CommonItem setRightTextTypeFace(Typeface tf) {
		rightTextView.setTypeface(tf);
		return this;
	}

	/**
	 * 设置点击时变色
	 *
	 * @param b
	 */
	public CommonItem setClickColorChange(boolean b) {
		if (b) {
			if (Build.VERSION.SDK_INT >= 16) {
				parentLayout.setBackground(context.getResources().getDrawable(R.drawable.frame_bg_common_item_pressed));
			} else {
				parentLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.frame_bg_common_item_pressed));
			}
		}
		return this;
	}

	public CommonItem setRightImageVisable(boolean b) {
		rightImageView.setVisibility(b ? View.VISIBLE : View.GONE);
		return this;
	}

	public CommonItem setLeftImageVisable(boolean visable) {
		leftImageView.setVisibility(visable ? VISIBLE : GONE);
		return this;
	}

	public String getRightTextString() {
		return rightTextView.getText().toString();
	}

	public String getLeftTextString() {
		return leftTextView.getText().toString();
	}

	public TextView getLeftTextView() {
		return leftTextView;
	}

	public TextView getRightTextView() {
		return rightTextView;
	}

	public ImageView getLeftImageView() {
		return leftImageView;
	}

	public ImageView getRightImageView() {
		return rightImageView;
	}

	public RelativeLayout getParentLayout() {
		return parentLayout;
	}

	public View getBottomDividerView() {
		return bottomDividerView;
	}

	public View getTopDividerView() {
		return topDividerView;
	}
}
