package com.micky.www.filedown;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.micky.www.filedownlibrary.DownloadConfig;

import java.util.List;

/**
 * Created by Administrator on 2018/12/14.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder>{

    private List<ListBean> mList;
    private Context context;
    private ListItemOnClickerListener mListListener;

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
        String imageTag = (String) holder.imageView.getTag();
       String cover = mList.get(position).getCover();
        if (!TextUtils.equals(imageTag,cover))
        {

        }
       holder.imageView.setImageURI(mList.get(position).getCover());
       holder.tvPercentSpeed.setText(mList.get(position).getPercent()+"%    "+mList.get(position).getSpeed());
       int status = mList.get(position).getDownStatus();
       holder.tvDownStatus.setText(status == DownloadConfig.STATUS_START ? "开始下载":status == DownloadConfig.STATUS_DOWNNING ? "正在下载":
       status == DownloadConfig.STATUS_PAUSE ? "已暂停":status == DownloadConfig.STATUS_COMPLETE ? "已完成":status == DownloadConfig.STATUS_ERROR ? "下载错误":"其他问题");

    }

    @Override
    public void onBindViewHolder(FileListViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty())
        {
            super.onBindViewHolder(holder, position, payloads);
        }else
        {
            //ListBean listBean = mList.get(position);
            holder.tvPercentSpeed.setText(mList.get(position).getPercent()+"%    "+mList.get(position).getSpeed());
            holder.mProgressBar.setProgress(mList.get(position).getPercent());
            int status = mList.get(position).getDownStatus();
            holder.tvDownStatus.setText(status == DownloadConfig.STATUS_START ? "开始下载":status == DownloadConfig.STATUS_DOWNNING ? "正在下载":
                    status == DownloadConfig.STATUS_PAUSE ? "已暂停":status == DownloadConfig.STATUS_COMPLETE ? "已完成":status == DownloadConfig.STATUS_ERROR ? "下载错误":status == DownloadConfig.STATUS_WAIT ? "等待下载":"其他问题");
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setListener(ListItemOnClickerListener listener)
    {
        this.mListListener = listener;
    }

    class FileListViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        TextView textView;
        TextView tvPercentSpeed;
        TextView tvDownStatus;
        ProgressBar mProgressBar;
        public FileListViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cover);
            textView = itemView.findViewById(R.id.title);
            tvPercentSpeed = itemView.findViewById(R.id.percentandspeed);
            tvDownStatus = itemView.findViewById(R.id.item_downstatus);
            mProgressBar = itemView.findViewById(R.id.item_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListListener.onItemClicker(v,getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListListener.onItemLongClicker(v,getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
