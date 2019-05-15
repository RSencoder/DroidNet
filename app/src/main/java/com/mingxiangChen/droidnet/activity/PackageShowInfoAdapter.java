package com.mingxiangChen.droidnet.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mingxiangChen.droidnet.R;

import java.util.List;

/**
 * The adapter class for recyclerView in {@link PackageListActivity}.
 * 
 * @author RXmxchen
 * @since 2019-05-05
 */
public class PackageShowInfoAdapter extends 
        RecyclerView.Adapter<PackageShowInfoAdapter.ViewHolder> {

    private List<PackageShowInfo> mPackageShowInfos;
    private Activity mActivity;  // 持有Activity的引用，处理recyclerView所在Activity的intent数据传递

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView packageIcon;
        TextView packageName;
        View packageView;

        public ViewHolder(View view) {
            super(view);
            packageIcon = view.findViewById(R.id.img_package);
            packageName = view.findViewById(R.id.txt_packageName);
            packageView = view;
        }
    }
    
    public PackageShowInfoAdapter(List<PackageShowInfo> packageShowInfos, Activity activity) {
        this.mPackageShowInfos = packageShowInfos;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_package, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.packageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                PackageShowInfo pkgInfo = mPackageShowInfos.get(position);
                Intent intent = new Intent();
                intent.putExtra("selected_package_name", pkgInfo.packageName);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PackageShowInfo info = mPackageShowInfos.get(position);
        viewHolder.packageIcon.setImageDrawable(info.packageIcon);
        viewHolder.packageName.setText(info.appName);
    }

    @Override
    public int getItemCount() {
        return mPackageShowInfos.size();
    }
}
