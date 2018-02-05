package com.yizu.intelligentpiano.utils;

import android.os.Environment;
import android.os.StatFs;

import com.yizu.intelligentpiano.constens.Constents;

import java.io.File;

/**
 * Created by liuxiaozhu on 2017/9/22.
 * All Rights Reserved by YiZu
 */

public class SDCardUtils {
    /**
     * 获取sd卡的绝对路径
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        if (isExistSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }
    }

    /**
     * 检查是否存在sd卡
     *
     * @return
     */
    private static boolean isExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SD卡剩余空间的大小
     *
     * @return mb
     */
    public static float getSDFreeSize() {
        float size = 0f;
        if (getExternalStorageDirectory().equals("")) {
            return size;
        }
        //取得SD卡文件路径
        File path = new File(getExternalStorageDirectory());
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
//        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
        if (freeBlocks * blockSize > 0) {
            size = (freeBlocks * blockSize / 1024 / 1024);
        }
        return size;
    }

    /**
     * 获取SD卡空间的大小
     *
     * @return
     */
    public static String getSDAllSize() {
        String size = "0MB";
        if (getExternalStorageDirectory().equals("")) {
            return size;
        }
        //取得SD卡文件路径
        File path = new File(getExternalStorageDirectory());
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
//        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
        if (allBlocks * blockSize > 0) {
            size = (allBlocks * blockSize / 1024 / 1024) + "MB";
        }
        return size;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 查看文件是否存在
     *
     * @param filePath
     * @return
     */
    public static String getIsHave(String filePath) {
        String url = getExternalStorageDirectory().concat(filePath);
        File file = new File(url);
        if (file.exists()) {
            return url;
        } else {
            return "";
        }
    }

    /**
     * 创建文件或文件夹
     *
     * @param filePath
     */
    public static void creatFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建本地文件
     */
    public static void creatFile() {
        final String sd = SDCardUtils.getExternalStorageDirectory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!sd.equals("")) {
                    //智能钢琴
                    SDCardUtils.creatFile(sd.concat(Constents.PIANO_URL));
                    //apk
                    SDCardUtils.creatFile(sd.concat(Constents.APK_URL));
                    //video
                    SDCardUtils.creatFile(sd.concat(Constents.VIDEO_URL));
                    //歌曲
                    SDCardUtils.creatFile(sd.concat(Constents.XML));
                }
            }
        }).start();
    }
}
