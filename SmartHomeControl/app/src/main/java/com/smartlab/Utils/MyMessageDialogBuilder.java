package com.smartlab.Utils;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIWrapContentScrollView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public class MyMessageDialogBuilder extends QMUIDialog.MessageDialogBuilder {

    public MyMessageDialogBuilder(Context context) {
        super(context);
    }

    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent, Context context) {
        super.onCreateContent(dialog, parent, context);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof QMUIWrapContentScrollView) {
                QMUIWrapContentScrollView scrollView = (QMUIWrapContentScrollView) view;
                for (int j = 0; j < scrollView.getChildCount(); j++) {
                    View view1 = parent.getChildAt(j);
                    if (view1 instanceof TextView) {
                        ((TextView) view1).setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
            }
        }
    }
}
