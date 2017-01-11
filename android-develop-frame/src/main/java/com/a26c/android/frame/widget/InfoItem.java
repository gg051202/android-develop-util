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

/**
 * 左边两个TextView,右边一个ImageView的布局
 */
public class InfoItem extends RelativeLayout {

	private Context context;
	public TextView titleTextView, descTextView;
	public ImageView rightImageView;
	public View topDividerView, bottomDividerView;
	public RelativeLayout parentLayout;

	public InfoItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.frame_layout_info_item, this, true);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		descTextView = (TextView) findViewById(R.id.descTextView);
		rightImageView = (ImageView) findViewById(R.id.rightImageView);
		topDividerView = findViewById(R.id.topDividerView);
		bottomDividerView = findViewById(R.id.bottomDividerView);
		parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.InfoItem);
		//获取默认的字体大小，尺寸是px，再转成sp
		setTitleText(array.getString(R.styleable.InfoItem_InfoItemTitleText),
				array.getInteger(R.styleable.InfoItem_InfoItemTitleTextSize, 15),
				array.getColor(R.styleable.InfoItem_InfoItemTitleTextColor, 0xff2B2B2B));
		setDescText(array.getString(R.styleable.InfoItem_InfoItemDescText),
				array.getInteger(R.styleable.InfoItem_InfoItemDescTextSize, 15),
				array.getColor(R.styleable.InfoItem_InfoItemDescTextColor, 0xff868686));

		setRightImageByPx(array.getResourceId(R.styleable.InfoItem_InfoItemImage, 0),
				array.getDimension(R.styleable.InfoItem_InfoItemImageWidth, FrameDensityUtils.dp2px(context, 10)),
				array.getDimension(R.styleable.InfoItem_InfoItemImageHeight, FrameDensityUtils.dp2px(context, 15)));

		setPaddingInfoItemByPx((int) array.getDimension(R.styleable.InfoItem_InfoItemPaddingLeft, FrameDensityUtils.dp2px(context, 12)),
				(int) array.getDimension(R.styleable.InfoItem_InfoItemPaddingTop, 0),
				(int) array.getDimension(R.styleable.InfoItem_InfoItemPaddingRight, FrameDensityUtils.dp2px(context, 8)),
				(int) array.getDimension(R.styleable.InfoItem_InfoItemPaddingBottom, 0));

		setHeightByPx((int) array.getDimension(R.styleable.InfoItem_InfoItemHeight, FrameDensityUtils.dp2px(context, 50)));
		setTopDividerVisable(array.getBoolean(R.styleable.InfoItem_InfoItemShowTopDivider, false));
		setBottomDividerVisable(array.getBoolean(R.styleable.InfoItem_InfoItemShowBottomDivider, false));
		array.recycle();

	}

	public InfoItem setTitleText(String text) {
		titleTextView.setText(text);
		return this;
	}

	public InfoItem setDescText(String text) {
		descTextView.setText(text);
		return this;
	}

	public InfoItem setTitleTextColor(int colorId) {
		titleTextView.setTextColor(colorId);
		return this;
	}

	public InfoItem setDescTextColor(int colorId) {
		descTextView.setTextColor(colorId);
		return this;
	}

	public InfoItem setTitleTextSize(float size) {
		titleTextView.setTextSize(size);
		return this;
	}

	public InfoItem setDescTextSize(float size) {
		descTextView.setTextSize(size);
		return this;
	}

	/**
	 * 设置CommonItem的高度
	 */
	public InfoItem setHeight(int height) {
		setHeightByPx(FrameDensityUtils.dp2px(context, height));
		return this;
	}

	/**
	 * 设置CommonItem的高度
	 */
	private InfoItem setHeightByPx(int height) {
		android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) parentLayout
				.getLayoutParams();
		lp.height = height;
		parentLayout.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置commonItem的内边距
	 */
	public InfoItem setPaddingInfoItem(int left, int top, int right, int botom) {
		setPaddingInfoItemByPx(FrameDensityUtils.dp2px(context, left), FrameDensityUtils.dp2px(context, top),
				FrameDensityUtils.dp2px(context, right), FrameDensityUtils.dp2px(context, botom));
		return this;
	}

	private InfoItem setPaddingInfoItemByPx(int left, int top, int right, int botom) {
		parentLayout.setPadding(left, top, right, botom);
		return this;
	}

	private InfoItem setRightImageByPx(int imgId, float width, float height) {
		setRightImage(imgId);
		setRightImageByPx(width, height);
		return this;
	}


	public InfoItem setRightImage(int imgId) {
		rightImageView.setImageResource(imgId);
		return this;
	}

	public InfoItem setRightImageByPx(float width, float height) {
		LayoutParams lp = (LayoutParams) rightImageView.getLayoutParams();
		lp.height = FrameDensityUtils.dp2px(context, height);
		lp.width = FrameDensityUtils.dp2px(context, width);
		rightImageView.setLayoutParams(lp);
		return this;
	}

	public InfoItem setRightImage(float width, float height) {
		return setRightImageByPx(FrameDensityUtils.dp2px(context, width),
				FrameDensityUtils.dp2px(context, height));
	}

	/**
	 * 设置顶部分割线是否可见
	 */
	public InfoItem setTopDividerVisable(boolean b) {
		topDividerView.setVisibility(b ? View.VISIBLE : View.GONE);
		return this;
	}

	/**
	 * 设置底部分割线是否可见
	 */
	public InfoItem setBottomDividerVisable(boolean b) {
		bottomDividerView.setVisibility(b ? View.VISIBLE : View.GONE);
		return this;
	}

	public InfoItem setTitleText(String text, int size, int color) {
		setTitleText(text);
		setTitleTextColor(color);
		setTitleTextSize(size);
		return this;
	}

	public InfoItem setTitleText(Spanned text, int size, int color) {
		titleTextView.setText(text);
		setTitleTextColor(color);
		setTitleTextSize(size);
		return this;
	}


	public InfoItem setDescText(String text, int size, int color) {
		setDescText(text);
		setDescTextColor(color);
		setDescTextSize(size);
		return this;
	}

	public InfoItem setDescText(Spanned text, int size, int color) {
		descTextView.setText(text);
		setDescTextColor(color);
		setDescTextSize(size);
		return this;
	}

	/**
	 * 设置左边文字的最大宽度
	 *
	 * @param width
	 */
	public InfoItem setLeftTextWidth(float width) {
		titleTextView.setMaxWidth(FrameDensityUtils.dp2px(context, width));
		return this;
	}

	/**
	 * 设置右边文字的字体
	 *
	 * @param tf
	 */
	public InfoItem setRightTextTypeFace(Typeface tf) {
		descTextView.setTypeface(tf);
		return this;
	}

	/**
	 * 设置点击时变色
	 *
	 * @param b
	 */
	public InfoItem setClickColorChange(boolean b) {
		if (b) {
			if (Build.VERSION.SDK_INT >= 16) {
				parentLayout.setBackground(context.getResources().getDrawable(R.drawable.frame_bg_common_item_pressed));
			} else {
				parentLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.frame_bg_common_item_pressed));
			}
		}
		return this;
	}

	public void setRightImageVisable(boolean b) {
		rightImageView.setVisibility(b ? View.VISIBLE : View.GONE);
	}

}
