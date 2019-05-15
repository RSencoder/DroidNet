package com.mingxiangChen.droidnet.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.activity.PackageListActivity;
import com.mingxiangChen.droidnet.fragment.FirewallFragment;
import com.mingxiangChen.droidnet.table.InterceptRules;

import org.litepal.LitePal;

import java.util.List;

/**
 * A {@link BaseDialog} loads editRule layout for updating 
 * {@link com.mingxiangChen.droidnet.table.InterceptRules}.
 * 
 * @author RSmxchen
 * @since 2019-05-05 15:55
 */
public class EditRuleDialog extends BaseDialog implements View.OnClickListener{

    public static final int EDIT_RULE_DIALOG_REQUEST_CODE = 2800;
    private static final String TAG = "EditRuleDialog";
    private int[] mListenedItem;
    
    private EditText mPriority, mSession, mPackageName, mPattern;
    
    public EditRuleDialog(Context context, Fragment fragment) {
        super(context, R.layout.dialog_edit_rule, fragment);
        this.mListenedItem = new int[] {
                R.id.btn_editRule_selectPkg,
                R.id.btn_editRule_cancel,
                R.id.btn_editRule_delete,
                R.id.btn_editRule_confirm
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPriority = findViewById(R.id.editRule_priority);
        mPriority.setFocusable(false);  // 设置为不可编辑状态（失去焦点）
        mPriority.setFocusableInTouchMode(false);  // 设置为不可编辑状态（输入模式失去焦点）
        mSession = findViewById(R.id.editRule_session);
        mPackageName = findViewById(R.id.editRule_pkgName);
        mPattern = findViewById(R.id.editRule_pattern);
        
        // 注册按钮的点击监听事件
        for (int id: mListenedItem) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_editRule_selectPkg:
                Intent intent = new Intent(getmContext(), PackageListActivity.class);
                getmParentFragment().startActivityForResult(intent, EDIT_RULE_DIALOG_REQUEST_CODE);
                break;
            case R.id.btn_editRule_cancel:
                dismiss();
                break;
            case R.id.btn_editRule_delete:
                if (handleDelete() == 1) {
                    Log.i(TAG, "delete succeed!");
                    ((FirewallFragment) getmParentFragment()).onTableChanged();
                } else {
                    Log.i(TAG, "delete error!");
                }
                dismiss();
                break;
            case R.id.btn_editRule_confirm:
                if (handleConfirm()) {
                    Log.i(TAG, "update succeed!");
                    ((FirewallFragment) getmParentFragment()).onTableChanged();
                } else {
                    Log.w(TAG, "update failed!" );
                }
                dismiss();
                break;
        }
    }

    public void setPriority(int priority) { mPriority.setText(Integer.toString(priority)); }
    public void setSession(String session) { mSession.setText(session); }
    public void setPackageName(String packageName) {
        mPackageName.setText(packageName);
    }
    public void setPattern(String pattern) { mPattern.setText(pattern); }
    
    private int handleDelete() {
        return LitePal.deleteAll(InterceptRules.class, "priority = ?", mPriority.getText().toString());
    }
    
    private boolean handleConfirm() {
        List<InterceptRules> rules = LitePal.where("priority = ?", mPriority.getText().toString())
                .find(InterceptRules.class);
        if (rules.size() != 1) {
            return false;
        }
        InterceptRules rule = rules.get(0);
        rule.setSession(mSession.getText().toString());
        rule.setPackageName(mPackageName.getText().toString());
        rule.setPattern(mPattern.getText().toString());
        return rule.save();
    }
}
