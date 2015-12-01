package com.android.tengfenxiang.activity;

import com.android.tengfenxiang.util.ImageLoadTools;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;

/**
 * 需要异步加载图片的activity基类
 * @author ccz
 *
 */
public abstract class BaseActivity extends Activity {
	
	protected ImageLoader imageLoader;

    /**
     * 初始化布局资源文件
     */
    public abstract int initResource();

    /**
     * 初始化组件
     */
    public abstract void initComponent();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 添加监听
     */
    public abstract void addListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initResource());
        imageLoader = ImageLoader.getInstance();
        initComponent();
        initData();
        addListener();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	ImageLoadTools.clearMemoryCache();
    }

}