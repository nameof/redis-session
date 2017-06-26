package com.casandroidclient.component;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.casandroidclient.R;


public class LoadingDialog extends ProgressDialog {

    private Context mContext;
    private String msg;
    private TextView content;

    public LoadingDialog(Context context, String message) {
        super(context, R.style.Theme_dialog);
        this.mContext = context;
        this.msg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.progress_loding_layout,
                null);
        content = (TextView) contentView.findViewById(R.id.content);
        content.setText(msg);
        setCanceledOnTouchOutside(false);
        setContentView(contentView);
    }

    public void setMessage(CharSequence c) {
        content.setText(c);
    }

}