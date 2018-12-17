package com.micky.www.filedownlibrary;

import java.text.DecimalFormat;

/**
 * Created by Micky on 2018/12/17.
 * 字符串处理
 */

public class SpeedUtils {

    /**
     * long的速度转换为字符串
     * @param file
     * @return
     */
    public static String FormetFileSize(long file)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (0 < file & file < 1024)
        {
            fileSizeString = df.format((double) file) + "b/s";
        }else if (file < 1048576 || file == 0)
        {
            fileSizeString = df.format((double) file / 1024) + "kb/s";
        } else if (file < 1073741824){
            fileSizeString = df.format((double) file / 1048576) + "m/s";
        }else
        {
            fileSizeString = df.format((double) file / 1073741824) + "g/s";
        }
        return fileSizeString;
    }

}
