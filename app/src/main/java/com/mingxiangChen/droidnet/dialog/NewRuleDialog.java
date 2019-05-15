package com.mingxiangChen.droidnet.dialog;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.activity.MainActivity;
import com.mingxiangChen.droidnet.activity.PackageListActivity;
import com.mingxiangChen.droidnet.fragment.FirewallFragment;
import com.mingxiangChen.droidnet.table.InterceptRules;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * A {@link BaseDialog} loads newRule layout for inserting a new rule in
 * {@link com.mingxiangChen.droidnet.table.InterceptRules}.
 *
 * @author RSmxchen
 * @since 2019-05-05 16:10
 */
public class NewRuleDialog extends BaseDialog implements View.OnClickListener {

    private int[] mListenedItem;
    public static final int NEW_RULE_DIALOG_REQUEST_CODE = 4396;
    private static final String TAG = "NewRuleDialog";

    private EditText mAfterPriority, mSession, mPackageName, mPattern;

    public NewRuleDialog(Context context, Fragment fragment) {
        super(context, R.layout.dialog_new_rule, fragment);
        this.mListenedItem = new int[]{
                R.id.btn_newRule_selectPkg,
                R.id.btn_newRule_cancel,
                R.id.btn_newRule_confirm
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAfterPriority = findViewById(R.id.newRule_priorityAfter);
        mSession = findViewById(R.id.newRule_session);
        mPackageName = findViewById(R.id.newRule_pkgName);
        mPattern = findViewById(R.id.newRule_pattern);
        mSession.setText("N/A");
        mPackageName.setText("N/A");
        mPattern.setText("N/A");

        // 注册按钮的点击监听事件
        for (int id : mListenedItem) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_newRule_selectPkg:
                Intent intent = new Intent(getmContext(), PackageListActivity.class);
                getmParentFragment().startActivityForResult(intent, NEW_RULE_DIALOG_REQUEST_CODE);
                break;
            case R.id.btn_newRule_cancel:
                dismiss();
                break;
            case R.id.btn_newRule_confirm:
                boolean insertResult = insertOne(Integer.parseInt(mAfterPriority.getText().toString()),
                        mSession.getText().toString(),
                        mPackageName.getText().toString(),
                        mPattern.getText().toString());
                if (insertResult) {
                    Log.i(TAG, "insert succeed!");
                    ((FirewallFragment) getmParentFragment()).onTableChanged();
                } else {
                    Log.i(TAG, "insert failed!");
                }
                dismiss();
                break;
        }
    }

    public void setPackageName(String packageName) {
        mPackageName.setText(packageName);
    }

    /**
     * insert a row in table {@link InterceptRules}.
     *
     * @param priorityAfter
     * @param session
     * @param packageName
     * @param pattern
     * @return true if insert succeed.
     */
    private boolean insertOne(int priorityAfter, String session, String packageName, String pattern) {
        // check whether the InterceptRules table has data or not
        List<InterceptRules> hasRules = LitePal.findAll(InterceptRules.class);
        if (hasRules.size() > 0) {
            // check whether the priorityAfter exists or not
            List<InterceptRules> existRule = LitePal.where("priority = ?", Integer.toString(priorityAfter))
                    .find(InterceptRules.class);
            if (existRule.size() > 0) {
                Log.d(TAG, "insertOne: " + "find existRule!");
                // all the rows that priority after existRule's plus one.
                List<InterceptRules> afters = LitePal.where("priority > ?", Integer.toString(priorityAfter))
                        .find(InterceptRules.class);
                for (InterceptRules rule : afters) {
                    int oldPriority = rule.getPriority();
                    rule.setPriority(oldPriority + 1);
                    rule.save();
                }
                // insert the new row
                InterceptRules newRule = new InterceptRules();
                newRule.setPriority(priorityAfter + 1);
                newRule.setSession(session);
                newRule.setPackageName(packageName);
                newRule.setPattern(pattern);
                return newRule.save();
            } else {
                Log.w(TAG, "insertOne: " + "didn't find priorityAfter!");
                Toast.makeText(getmContext(), "priorityAfter不存在！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            InterceptRules rule = new InterceptRules();
            rule.setPriority(1);  // if there is no data in the table, the priority should start from 1.
            rule.setSession(session);
            rule.setPackageName(packageName);
            rule.setPattern(pattern);
            return rule.save();
        }
    }
}
