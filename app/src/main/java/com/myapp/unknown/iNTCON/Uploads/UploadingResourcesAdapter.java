package com.myapp.unknown.iNTCON.Uploads;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 2/10/2017.
 */
public class UploadingResourcesAdapter extends RecyclerView.Adapter<UploadingResourcesAdapter.UploadingResourcesViewHolder> {

    private final Context context;
    private final ArrayList<DataResourceItemProvider> uploadingDataResourcesItemList;
    private final ArrayList<Integer> uploadingProgressList;
    private final UploadingResourcesInterface uploadingResourcesInterface;

    public UploadingResourcesAdapter(Context context,
                                     ArrayList<DataResourceItemProvider> uploadingDataResourcesItemList,
                                     ArrayList<Integer> uploadingProgressList,
                                     UploadingResourcesInterface uploadingResourcesInterface)
    {
        this.context = context;
        this.uploadingDataResourcesItemList = uploadingDataResourcesItemList;
        this.uploadingProgressList = uploadingProgressList;
        this.uploadingResourcesInterface = uploadingResourcesInterface;
    }

    @Override
    public UploadingResourcesAdapter.UploadingResourcesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloading_resources_layout,parent,false);
        return new UploadingResourcesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UploadingResourcesAdapter.UploadingResourcesViewHolder holder, int position) {

        holder.title.setText(uploadingDataResourcesItemList.get(position).getTitle());
        holder.time.setText(uploadingDataResourcesItemList.get(position).getTime());
        holder.date.setText(uploadingDataResourcesItemList.get(position).getDate());

        int lastIndex = uploadingDataResourcesItemList.get(position).getTitle().lastIndexOf(".");

        setImage(uploadingDataResourcesItemList.get(position).getTitle().substring(lastIndex + 1),
                holder);

        holder.progressBar.setProgress(uploadingProgressList.get(position));


        Float fileSize = (float) (uploadingDataResourcesItemList.get(position).getSize() / 1024);

        if(fileSize > 1024)
        {
            fileSize = fileSize / 1024;
            holder.size.setText(context.getString(R.string.file_size_MB,fileSize));
        }
        else
        {
            holder.size.setText(context.getString(R.string.file_size_KB,fileSize));
        }


        if(position == 0)
        {
            holder.cancel.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return uploadingDataResourcesItemList.size();
    }


    public class UploadingResourcesViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView size;
        final TextView date;
        final TextView time;
        final ImageButton cancel;
        final ImageView resource_type;
        final ProgressBar progressBar;

            public UploadingResourcesViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.downloading_resource_title);
                size = (TextView) itemView.findViewById(R.id.downloading_resource_size);
                date = (TextView) itemView.findViewById(R.id.downloading_resource_date);
                time = (TextView) itemView.findViewById(R.id.downloading_resource_time);
                cancel = (ImageButton) itemView.findViewById(R.id.downloading_resource_cancel);
                resource_type = (ImageView) itemView.findViewById(R.id.downloading_resource_type_iv);
                progressBar = (ProgressBar) itemView.findViewById(R.id.downloading_resource_progress);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadingResourcesInterface.btn_clicked(getAdapterPosition());
                    }
                });


            }
    }


    public interface UploadingResourcesInterface{
        void btn_clicked(int position);
    }



    public void setImage(String fileExtension,UploadingResourcesViewHolder holder) {

        switch (fileExtension)
        {
            case "pdf" :
                holder.resource_type.setImageResource(R.mipmap.pdf_one);
                break;

            case "ppt" :
                holder.resource_type.setImageResource(R.mipmap.ppt);
                break;

            case "doc" :
                holder.resource_type.setImageResource(R.mipmap.doc);
                break;

            case "xls" :
                holder.resource_type.setImageResource(R.mipmap.xls);
                break;

            case "jpg" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "jpeg" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "png" :
                holder.resource_type.setImageResource(R.mipmap.jpg);
                break;

            case "rar" :
                holder.resource_type.setImageResource(R.mipmap.rar);
                break;

            case "zip" :
                holder.resource_type.setImageResource(R.mipmap.zip);
                break;

            case "txt" :
                holder.resource_type.setImageResource(R.mipmap.txt);
                break;

            default:
                holder.resource_type.setImageResource(R.mipmap.docs);
                break;
        }
    }

}
