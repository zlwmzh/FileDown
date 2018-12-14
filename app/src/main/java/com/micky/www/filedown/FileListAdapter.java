package com.micky.www.filedown;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/12/14.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder>{

    private List<ListBean> mList;
    private Context context;

    public FileListAdapter(List<ListBean> list, Context context)
    {
        this.mList = list;
        this.context = context;
    }

    @Override
    public FileListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(FileListViewHolder holder, int position) {
       holder.textView.setText(mList.get(position).getTitle());
       holder.mProgressBar.setProgress(mList.get(position).getPercent());
       holder.itemView.setTag(mList.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class FileListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        ProgressBar mProgressBar;
        public FileListViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cover);
            textView = itemView.findViewById(R.id.title);
            mProgressBar = itemView.findViewById(R.id.item_progress);
        }
    }
}
