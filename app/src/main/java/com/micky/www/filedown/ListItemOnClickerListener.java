package com.micky.www.filedown;

import android.view.View;

/**
 * @ClassName ListItemOnClickerListener
 * @Description 列表点击事件监听接口
 * @Author Micky
 * @Date 2018/12/16 15:15
 * @Version 1.0
 */
public interface ListItemOnClickerListener {
    /**
     * 点击事件
     * @param view
     * @param position
     */
    void onItemClicker(View view, int position);

    /**
     * 长按事件
     * @param view
     * @param position
     */
    void onItemLongClicker(View view, int position);
}
