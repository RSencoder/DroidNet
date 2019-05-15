package com.mingxiangChen.droidnet.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * A base class extends {@link Dialog} designed for loading self-defined layout, inspired by the 
 * CSDN blog @see <a href="https://blog.csdn.net/sakurakider/article/details/80735400">MyDialog1</a>.
 * Create a class extending BaseDialog and put its layout and listenedItem in the constructor.
 * 
 * @author RSmxchen
 * @since 2019-05-05 15:27
 */
public class BaseDialog extends Dialog {

    private Context mContext;
    private int mLayoutResID;
    private Fragment mParentFragment;
    
    public BaseDialog(Context context, int layoutResID, Fragment fragment) {
        super(context);
        this.mContext = context;
        this.mLayoutResID = layoutResID;
        this.mParentFragment = fragment;
    }

    public Context getmContext() {
        return mContext;
    }

    public Fragment getmParentFragment() {
        return mParentFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutResID);
        setCanceledOnTouchOutside(true);  // 点击外部，Dialog消失

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);  // 设置dialog显示居中
        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*7/8;  // 设置dialog宽度为屏幕的7/8
        getWindow().setAttributes(lp);
    }
}
