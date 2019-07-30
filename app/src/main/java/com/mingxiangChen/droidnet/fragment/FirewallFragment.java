package com.mingxiangChen.droidnet.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.mingxiangChen.droidnet.App;
import com.mingxiangChen.droidnet.R;
import com.mingxiangChen.droidnet.dialog.EditRuleDialog;
import com.mingxiangChen.droidnet.dialog.NewRuleDialog;
import com.mingxiangChen.droidnet.dialog.TableChanged;
import com.mingxiangChen.droidnet.table.InterceptRules;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FirewallFragment extends BaseFragment implements TableChanged {
    
    private SmartTable mTable;
    private Column<Integer> mPriorityColumn;
    private Column<String> mSessionColumn;
    private Column<String> mPackageNameColumn;
    private Column<String> mPatternColumn;
    private TableData<InterceptRules> mTableData;
    
    private Button mNewRuleButton;
    private NewRuleDialog mNewRuleDialog;
    private EditRuleDialog mEditRuleDialog;
    
    private ToastFirewallHandler mHandler;
    
    private static final String TAG = "FirewallFragment";
    
    @Override
    int getLayout() {
        return R.layout.fragment_firewall;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        this.mHandler = new ToastFirewallHandler();
        mHandler.setContext(getActivity());
        App.getInstance().mDroidNetVirtualGatewayFactory.setToastFirewallHandler(mHandler);
        
        mTable = view.findViewById(R.id.table_rules);

        mNewRuleDialog = new NewRuleDialog(getContext(), FirewallFragment.this);
        
        mNewRuleButton = view.findViewById(R.id.btn_new_rule);
        mNewRuleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewRuleDialog.show();
            }
        });
        
        makeSmartTable();
//        List<Column<?>> columns = new ArrayList<>();
//        columns.add(mPriorityColumn);
//        columns.add(mSessionColumn);
//        columns.add(mPackageNameColumn);
//        columns.add(mPatternColumn);
//        for (Column<?> column: columns) {
//            column.setOnColumnItemClickListener(new OnColumnItemClickListener<?>() {
//                @Override
//                public void onClick(Column<?> column, String s, Object o, int i) {
//                    
//                }
//            });
//        }
        // 搞不定这个泛型，干脆只在priority列设置点击监听器
        mPriorityColumn.setOnColumnItemClickListener(new OnColumnItemClickListener<Integer>() {
            @Override
            public void onClick(Column<Integer> column, String value, Integer integer, int position) {
//                Log.d(TAG, "mPriorityColumn:" + value + " | " + integer.toString() + " | " + position);
                mEditRuleDialog = new EditRuleDialog(getContext(), FirewallFragment.this);
                mEditRuleDialog.show();
                List<InterceptRules> rules = LitePal.where("priority = ?", integer.toString())
                        .find(InterceptRules.class);
                int priority = rules.get(0).getPriority();
                String session = rules.get(0).getSession();
                String pkgName = rules.get(0).getPackageName();
                String pattern = rules.get(0).getPattern();
                mEditRuleDialog.setPriority(priority);
                mEditRuleDialog.setSession(session);
                mEditRuleDialog.setPackageName(pkgName);
                mEditRuleDialog.setPattern(pattern);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NewRuleDialog.NEW_RULE_DIALOG_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String selectedPackageName = data.getStringExtra("selected_package_name");
                    mNewRuleDialog.setPackageName(selectedPackageName);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getContext(), "没有选择应用！", Toast.LENGTH_SHORT).show();
                }
                break;
            case EditRuleDialog.EDIT_RULE_DIALOG_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String selectedPackageName = data.getStringExtra("selected_package_name");
                    mEditRuleDialog.setPackageName(selectedPackageName);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getContext(), "没有选择应用！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    
    private void makeSmartTable() {
        mPriorityColumn = new Column<>("Priority", "priority");
        mSessionColumn = new Column<>("Session", "session");
        mPackageNameColumn = new Column<>("Package Name", "packageName");
        mPatternColumn = new Column<>("Pattern", "pattern");
        
        List<InterceptRules> rulesList = LitePal.order("priority asc").find(InterceptRules.class);
        makeSmartTable(rulesList);
    }
    
    private void makeSmartTable(List<InterceptRules> rulesList) {
        mTableData = new TableData<>("Firewall Rules", rulesList,
                mPriorityColumn, mSessionColumn, mPackageNameColumn, mPatternColumn);
        mTable.setTableData(mTableData);
    }

    @Override
    public void onTableChanged() {
        List<InterceptRules> rulesList = LitePal.order("priority asc").find(InterceptRules.class);
        makeSmartTable(rulesList);
    }
}
