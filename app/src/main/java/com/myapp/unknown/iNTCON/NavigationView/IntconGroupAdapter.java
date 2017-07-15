package com.myapp.unknown.iNTCON.NavigationView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.unknown.iNTCON.OtherClasses.GroupProfile;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by UNKNOWN on 2/20/2017.
 */
public class IntconGroupAdapter extends RecyclerView.Adapter<IntconGroupAdapter.IntconGroupHolder> {

    private final Context context;
    private final ArrayList<GroupProfile> groupProfileArrayList;
    private final IntconGroupInterface intconGroupInterface;

    public IntconGroupAdapter(Context context,
                              ArrayList<GroupProfile> groupProfileArrayList,
                              IntconGroupInterface intconGroupInterface)
    {
        this.context = context;
        this.groupProfileArrayList = groupProfileArrayList;
        this.intconGroupInterface = intconGroupInterface;
    }


    @Override
    public IntconGroupAdapter.IntconGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.intcon_groups_layout,parent,false);
        return new IntconGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(final IntconGroupAdapter.IntconGroupHolder holder, int position) {

        holder.textView.setText(groupProfileArrayList.get(position).getGroup_name());

        Glide.with(context)
                .load(groupProfileArrayList.get(position).getGroup_profile_path())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(250,160)
                .bitmapTransform(new RoundedCornersTransformation(context,10,0))
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return groupProfileArrayList.size();
    }


    public class IntconGroupHolder extends RecyclerView.ViewHolder {

        final RelativeLayout relativeLayout;
        final ImageView imageView;
        final TextView textView;

        public IntconGroupHolder(View itemView) {
            super(itemView);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.intcon_group_layout);
            imageView = (ImageView) itemView.findViewById(R.id.intcon_group_iv);
            textView = (TextView) itemView.findViewById(R.id.intcon_group_tv);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intconGroupInterface.handleItemClicked(getAdapterPosition());
                }
            });
        }
    }


    public interface IntconGroupInterface{
        void handleItemClicked(int position);
    }


}
