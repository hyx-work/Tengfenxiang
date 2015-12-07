package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Article;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 文章列表适配器
 * 
 * @author ccz
 * 
 */
public class ArticleListAdapter extends BaseAdapter {

	private Activity context;
	private List<Article> articles;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	public ArticleListAdapter(Activity context, List<Article> articles) {
		this.context = context;
		this.articles = articles;

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_empty)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Object getItem(int arg0) {
		return articles.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.article_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.viewCount = (TextView) convertView
					.findViewById(R.id.view_count);
			viewHolder.likeCount = (TextView) convertView
					.findViewById(R.id.like_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		imageLoader.displayImage(articles.get(position).getThumbnails(),
				viewHolder.image, options);
		viewHolder.title.setText(articles.get(position).getTitle());
		String viewCount = context.getString(R.string.view_count)
				+ articles.get(position).getViewCount();
		viewHolder.viewCount.setText(viewCount);
		String likeCount = context.getString(R.string.like_count)
				+ articles.get(position).getLikeCount();
		viewHolder.likeCount.setText(likeCount);
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public TextView title;
		public TextView viewCount;
		public TextView likeCount;
	}

}