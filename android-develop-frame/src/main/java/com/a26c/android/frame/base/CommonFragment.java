package com.a26c.android.frame.base;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by guilinlin on 16/7/20 11:20.
 * email 973635949@qq.com
 *
 * @desc Fragment 基类  使用方法 直接在getLayoutId中传入布局
 */
public abstract class CommonFragment extends Fragment {

	protected Context context;

	protected List<Call> callList;

	private Handler handler;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(getLayoutId(), null);
		context = getActivity();
		callList = new ArrayList<>();

		init(view, savedInstanceState);

		setEvent(view);

		return view;
	}


	/**
	 * 返回当前fragment需要引用的布局Id
	 */
	public abstract int getLayoutId();

	public abstract void init(View view, Bundle savedInstanceState);

	public abstract void setEvent(View view);

	protected void gotoActivity(Class activityClass, Bundle bundle) {
		Intent intent = new Intent(context, activityClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);

	}

	protected void gotoActivityForResult(Class activityClass, Bundle bundle, int requestCode) {
		Intent intent = new Intent(context, activityClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, requestCode);

	}

	protected void gotoActivityForResult(Class activityClass, int requestCode) {
		gotoActivityForResult(activityClass, null, requestCode);
	}

	protected void gotoActivity(Class activityClass) {
		gotoActivity(activityClass, null);
	}

	protected final Handler getHandler() {
		if (handler == null) {
			handler = new Handler(getActivity().getMainLooper());
		}
		return handler;
	}


	/**
	 * 将fragment显示在布局中
	 */
	protected void replaceFragment(int layoutId, Fragment fragment) {
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(layoutId, fragment);
		transaction.commit();
	}

	protected void showFragment(Fragment fragment) {
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.show(fragment);
		transaction.commit();
	}

	protected void hideFragment(Fragment fragment) {
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.hide(fragment);
		transaction.commit();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}
}
