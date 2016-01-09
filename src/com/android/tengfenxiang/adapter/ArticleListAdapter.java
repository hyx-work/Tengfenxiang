package com.android.tengfenxiang.adapter;

import java.util.List;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Article;
import com.bumptech.glide.Glide;

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

	public ArticleListAdapter(Activity context, List<Article> articles) {
		this.context = context;
		this.articles = articles;
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
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.viewCount = (TextView) convertView
					.findViewById(R.id.view_count);
			viewHolder.likeCount = (TextView) convertView
					.findViewById(R.id.like_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 利用缓存框架加载图片
		Glide.with(context).load(articles.get(position).getThumbnails())
				.centerCrop().placeholder(R.drawable.ic_empty).crossFade()
				.into(viewHolder.image);

		viewHolder.title.setText(articles.get(position).getTitle());
		viewHolder.content.setText(articles.get(position).getContent());

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
		public TextView content;
		public TextView viewCount;
		public TextView likeCount;
	}

}