package com.jj.investigation.openfire.view;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jj.investigation.openfire.R;


/**
 * 自定义的一个loading等待框
 */
public class LoadingDialog {
    private Context context = null;
    private Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
        createDialog();
    }

    public Dialog createDialog() {
        if (dialog == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.loading_dialog, null);
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

            dialog = new Dialog(context, R.style.CustomProgressDialog);
            dialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        return dialog;
    }

    /**
     * setMessage 提示内容
     */
    public LoadingDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        return this;
    }

    public void showDialog(String message) {
        if (dialog != null && !dialog.isShowing()) {
            setMessage(message);
            dialog.show();
        }
    }

    public void hideDialog() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
