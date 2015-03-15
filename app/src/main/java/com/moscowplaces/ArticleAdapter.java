package com.moscowplaces;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moscowplaces.network.entities.Content;

import java.util.List;

public class ArticleAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    public List<Content> data;

    public ArticleAdapter(List<Content> data, Activity activity) {
        this.activity = activity;
        this.data = data;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Content content = data.get(position);
        viewHolder.title.setText(content.title);

        // Append categories to the title using spannable for smaller text size.
        if (content.categoryNames != null) {
            String categories = "\n" + content.categoryNames;
            Spannable span = new SpannableString(content.title + categories);
            span.setSpan(new RelativeSizeSpan(0.6f), content.title.length(), span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.title.setText(span);
        }

        // Load and cache photo with Glide.
        if (content.address_cover_1x != null && content.address_cover_1x.filename != null) {
            String photoUrl = PhotoUrlUtil.buildUrl(content.address_cover_1x.filename);
            Glide.with(activity)
                    .load(photoUrl)
                    .into(viewHolder.photo);
        }

        return convertView;
    }

    public class ViewHolder {

        public ImageView photo;
        public TextView title;

        public ViewHolder(View view) {
            photo = (ImageView) view.findViewById(R.id.imageView);
            title = (TextView) view.findViewById(R.id.title);
        }

    }
}
