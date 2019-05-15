package com.mingxiangChen.droidnet.dialog;

/**
 * A interface invoked when dialog operates the table 
 * {@link com.mingxiangChen.droidnet.table.InterceptRules}. SmartTable calls the interface method to
 * update UI data.
 * 
 * @author RSmxchen
 * @since 2019-05-09 16:40
 */
public interface TableChanged {
    void onTableChanged();
}
