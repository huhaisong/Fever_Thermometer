package com.dasu.ganhuo.ui.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.dasu.ganhuo.mode.logic.update.OnCheckUpdateListener;
import com.dasu.ganhuo.mode.logic.update.UpdateController;
import com.dasu.ganhuo.mode.okhttp.VersionResEntity;
import com.dasu.ganhuo.utils.ToastUtils;

import java.io.File;

/**
 * Created by dasu on 2017/4/8.
 */
public class UpdateDialog extends AlertDialog implements OnCheckUpdateListener {
    private static final String TAG = UpdateDialog.class.getSimpleName();
    private Context mContext;
    private VersionResEntity mVersionInfo;
    private ProgressDialog mProgressDialog;

    public UpdateDialog(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView();
    }

    private void initView() {
        setTitle("发现新版本");
    }

    private void initCommonUpdateView() {
        setCancelable(true);
        setButton(DialogInterface.BUTTON_POSITIVE, "升级", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                doDownload();
            }
        });
        setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }

    private void initForceUpdateView() {
        setCancelable(false);
        setButton(DialogInterface.BUTTON_POSITIVE, "开始升级", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                doDownload();
            }
        });
    }

    private void doDownload() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("正在下载");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        UpdateController.downloadApk(mContext, mVersionInfo, this);
    }

    @Override
    public void onDownloading(int progress) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(progress);
        }
    }

    @Override
    public void onDownloadFinish(boolean isSucceed, String apkPath) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (isSucceed) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        } else {
            ToastUtils.show(mContext, "下载失败，请重试", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onPreUpdate(boolean force, VersionResEntity versionInfo) {
        mVersionInfo = versionInfo;
        setMessage(mVersionInfo.getChangelog());
        if (force) {
            initForceUpdateView();
        } else {
            initCommonUpdateView();
        }
        show();
    }
}
