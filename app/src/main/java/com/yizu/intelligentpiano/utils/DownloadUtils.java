/*http://www.jianshu.com/p/6816977bfdeb
 */
package com.yizu.intelligentpiano.utils;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.IDwonLoader;

/**
 * Created by liuxiaozhu on 2017/9/19.
 * All Rights Reserved by YiZu
 */

public class DownloadUtils {

    //下载器
    private DownloadManager downloadManager;
    //上下文
    private Context mContext;
    // 下载的ID
    private long downloadId;
    private String sdCardUrl = "";
    private FileType type;
    private AlertDialog mDialog;
    private IDwonLoader mIDwonLoader;
    private AlertDialog.Builder mBuilder;
    //是否需要销毁
    private boolean isDrestry = false;
    private boolean isSuccess = false;

    public DownloadUtils(Context context) {
        this.mContext = context;
        if (!SDCardUtils.getExternalStorageDirectory().equals("")) {
            sdCardUrl = SDCardUtils.getExternalStorageDirectory();
        }
    }

    // 下载apk
    public void downloadFile(String DwonUrl, String fileName, FileType types, String fileRoute, IDwonLoader iDwonLoader) {
        if (DwonUrl.equals("")) {
            return;
        }
        if (sdCardUrl.equals("")) {
            //没有sd卡
            return;
        }
        mIDwonLoader = iDwonLoader;
        type = types;
        if (SDCardUtils.getSDFreeSize() < 100.0f) {
            SDCardUtils.DeleteFolder(SDCardUtils.getExternalStorageDirectory().concat(Constents.PIANO_URL));
            SDCardUtils.creatFile();
        }
        // 创建下载任务
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DwonUrl));
        // 移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        // 在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("智能钢琴");
        request.setDescription("正在下载");
        request.setVisibleInDownloadsUi(true);
        if (type == FileType.APK) {
            SDCardUtils.DeleteFolder(sdCardUrl.concat(fileRoute) + "/" + fileName);
        }
        // 设置下载的路径
        request.setDestinationInExternalPublicDir(fileRoute.substring(1, fileRoute.length()), fileName);
        //获取DownloadManager
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        downloadId = downloadManager.enqueue(request);
        // 注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        isDrestry = true;
        isSuccess = false;
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setIcon(R.mipmap.myicon);
                mBuilder.setTitle("提示");
                mBuilder.setMessage("下载中...");
                mDialog = mBuilder.show();
            }
        });
    }

    // 广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    // 检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
//     通过下载的id查找
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //     下载暂停
                case DownloadManager.STATUS_PAUSED:
                    mBuilder.setMessage("下载暂停");
                    break;
                //     下载延迟
                case DownloadManager.STATUS_PENDING:
                    mBuilder.setMessage("下载延迟");
                    break;
                //     正在下载
                case DownloadManager.STATUS_RUNNING:
                    mBuilder.setMessage("正在下载...");
                    break;
                //     下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    mBuilder.setMessage("下载完成");
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    isSuccess = true;
                    if (type == FileType.APK) {
                        //     下载完成安装APK
                        if (mIDwonLoader != null) {
                            mIDwonLoader.apk();
                        }
                        installAPK();
                    } else if (type == FileType.VIDEO) {
                        if (mIDwonLoader != null) {
                            mIDwonLoader.video();
                        }
                        MyToast.ShowLong("视频下载成功");
                    } else {
                        //音乐文件下载成功
                        if (mIDwonLoader != null) {
                            mIDwonLoader.Xml();
                        }
                    }
                    break;
                //     下载失败
                case DownloadManager.STATUS_FAILED:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    MyToast.ShowLong("下载失败");
                    break;
            }
        }
        c.close();
        isDrestry = false;
        mContext.unregisterReceiver(receiver);
    }

    // 下载到本地后执行安装
    private void installAPK() {
        // 获取下载文件的Uri
        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
        if (downloadFileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public void onDrestry() {
        if (isDrestry)mContext.unregisterReceiver(receiver);
        if (!isSuccess) {
            downloadManager.remove(downloadId);
        }
    }

    public enum FileType {
        APK,
        VIDEO,
        XML
    }
}
