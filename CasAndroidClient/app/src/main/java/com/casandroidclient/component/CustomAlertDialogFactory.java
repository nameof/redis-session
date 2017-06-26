package com.casandroidclient.component;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

/**
 * @author clarechen 2014-10-28 对话框生成器
 */
public class CustomAlertDialogFactory {

    /**
     * 创建弹出loading框
     *
     * @param context
     * @param message    提示信息，默认为"请稍候..."
     * @param cancelable 是否可退出
     * @return
     */
    public static ProgressDialog createProgressDialog(Context context,
                                                      String message, boolean cancelable) {
        String msg = TextUtils.isEmpty(message) ? "正在加载..." : message;
        LoadingDialog dlg = new LoadingDialog(context, msg);
        dlg.setCancelable(cancelable);
        return dlg;
    }
}
