package com.mingxiangChen.droidnet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * I don't know how to explain now...just imitates the NetworkPacketCapture project.
 * 
 * @author RSmxchen
 * @since 2019-05-04 13:49
 */
public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    abstract int getLayout();
}
