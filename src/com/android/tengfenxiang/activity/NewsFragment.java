package com.android.tengfenxiang.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.tengfenxiang.adapter.SimpleListAdapter;
import com.android.tengfenxiang.util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;

public class NewsFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private ImageLoader imageLoader;
	private int position;

	public static NewsFragment newInstance(int position) {
		NewsFragment f = new NewsFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);

		ListView v = new ListView(getActivity());
		List<String> infos = new ArrayList<String>();
		infos.add("testtest");
		SimpleListAdapter adapter = new SimpleListAdapter(getActivity(), infos);
		v.setAdapter(adapter);
		v.setLayoutParams(params);

		fl.addView(v);
		return fl;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoadUtil.clearMemoryCache();
	}
}