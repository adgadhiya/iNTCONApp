package com.myapp.unknown.iNTCON.Downloads;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 2/9/2017.
 */
public class DownloadedResourcesAdapter extends RecyclerView.Adapter<DownloadedResourcesAdapter.DownloadedResourceHolder> {

    private final Context context;
    private final ArrayList<DataResourceItemProvider> downloadedDataResourceItemList;
    private final ArrayList<Integer> downloadedErrorList;
    private final DownloadResourceInterface downloadResourceInterface;


    public DownloadedResourcesAdapter(Context context,
                                      ArrayList<DataResourceItemProvider> downloadedDataResourceItemList,
                                      ArrayList<Integer> downloadedErrorList,
                                      DownloadResourceInterface downloadResourceInterface)
    {
        this.context = context;
        this.downloadedDataResourceItemList = downloadedDataResourceItemList;
        this.downloadedErrorList = downloadedErrorList;
        this.downloadResourceInterface = downloadResourceInterface;
    }

    @Override
    public DownloadedResourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.data_resource_item_layout,parent,false);
        return new DownloadedResourceHolder(view);
    }

    @Override
    public void onBindViewHolder(DownloadedResourceHolder holder, int position) {

        holder.title.setText(downloadedDataResourceItemList.get(position).getTitle());
        holder.date.setText(downloadedDataResourceItemList.get(position).getDate());
        holder.time.setText(downloadedDataResourceItemList.get(position).getTime());

        int lastIndex = downloadedDataResourceItemList.get(position).getTitle().lastIndexOf(".");

        setImage(downloadedDataResourceItemList.get(position).getTitle().substring(lastIndex + 1),
                holder);

        Float fileSize = (float) (downloadedDataResourceItemList.get(position).getSize() / 1024);

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

        if(downloadedErrorList.get(position) == 1)
        {
            holder.cancel.setVisibility(View.VISIBLE);
            holder.title.setTextColor(Color.rgb(204,0,0));
            holder.cancel.setImageResource(R.drawable.ic_sync_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return downloadedDataResourceItemList.size();
    }


    public class DownloadedResourceHolder extends RecyclerView.ViewHolder {

        final ImageView resource_type;
        final TextView title;
        final TextView size;
        final TextView date;
        final TextView time;
        final ImageButton cancel;
        final LinearLayout linearLayout;

        public DownloadedResourceHolder(View itemView) {
            super(itemView);

            resource_type = (ImageView) itemView.findViewById(R.id.data_resource_resource_type_iv);
            title = (TextView) itemView.findViewById(R.id.data_resource_resource_title_tv);
            size = (TextView) itemView.findViewById(R.id.data_resource_resource_size);
            cancel = (ImageButton) itemView.findViewById(R.id.data_resource_cancel);
            date = (TextView) itemView.findViewById(R.id.data_resource_resource_date);
            time = (TextView) itemView.findViewById(R.id.data_resource_resource_time);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.upload_resource__layout);

            cancel.setVisibility(View.VISIBLE);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadResourceInterface.btn_clicked(getAdapterPosition());
                }
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadResourceInterface.handleClicked(getAdapterPosition());
                }
            });

        }
    }

    public interface DownloadResourceInterface{
        void btn_clicked(int position);
        void handleClicked(int position);
    }

    public void setImage(String fileExtension,DownloadedResourceHolder holder) {

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
