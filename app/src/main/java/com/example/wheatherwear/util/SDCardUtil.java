package com.example.wheatherwear.util;

import android.os.Environment;

import java.io.File;

public class SDCardUtil {
    public static final String FILE_DIR="/TodayWear";
    public static final String IMAGE_DIR="/Image";

    /**
     * 检查sd卡是否可用
     * @return
     */
    public static boolean checkSdCard(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取sd卡的文件路径
     * @return
     */
    public static String getSdPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }

    /**
     * 创建目录
     * @param fileDir
     * @return
     */
    public static File createFileDir(String fileDir){
        String path=getSdPath()+fileDir;
        File filePath=new File(path);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        return filePath;
    }

}
