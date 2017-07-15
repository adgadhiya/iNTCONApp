package com.myapp.unknown.iNTCON.Uploads;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.DataResource.DataResourceItemProvider;
import com.myapp.unknown.iNTCON.R;

import java.util.ArrayList;

/**
 * Created by UNKNOWN on 2/8/2017.
 */
public class UploadedResourceListAdapter extends RecyclerView.Adapter<UploadedResourceListAdapter.UploadedResourceListViewHolder> {

    private final Context context;
    private final ArrayList<DataResourceItemProvider> uploadedDataResourceItemList;
    private final ArrayList<Integer> uploadedErrorList;
    private final UploadedResourceListInterface uploadedResourceListInterface;


    public UploadedResourceListAdapter(Context context,
                                       ArrayList<DataResourceItemProvider> uploadedDataResourceItemList,
                                       ArrayList<Integer> uploadedErrorList,
                                       UploadedResourceListInterface uploadedResourceListInterface)
    {
        this.context = context;
        this.uploadedDataResourceItemList = uploadedDataResourceItemList;
        this.uploadedErrorList = uploadedErrorList;
        this.uploadedResourceListInterface = uploadedResourceListInterface;
    }

    @Override
    public UploadedResourceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.data_resource_item_layout,parent,false);
        return new UploadedResourceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UploadedResourceListViewHolder holder, int position) {

        holder.title.setText(uploadedDataResourceItemList.get(position).getTitle());
        holder.date.setText(uploadedDataResourceItemList.get(position).getDate());
        holder.time.setText(uploadedDataResourceItemList.get(position).getTime());

        int lastIndex = uploadedDataResourceItemList.get(position).getTitle().lastIndexOf(".");

        setImage(uploadedDataResourceItemList.get(position).getTitle().substring(lastIndex + 1),
                holder);

        Float fileSize = (float) (uploadedDataResourceItemList.get(position).getSize() / 1024);

        if(fileSize > 1024)
        {
            fileSize = fileSize / 1024;
            holder.size.setText(context.getString(R.string.file_size_MB,fileSize));
        }
        else
        {
            holder.size.setText(context.getString(R.string.file_size_KB,fileSize));
        }

        if(uploadedErrorList.get(position) == 1)
        {
            holder.title.setTextColor(Color.rgb(204,0,0));
            holder.cancel.setImageResource(R.drawable.ic_sync_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return uploadedDataResourceItemList.size();
    }


    public class UploadedResourceListViewHolder extends RecyclerView.ViewHolder {

        final ImageView resource_type;
        final TextView title;
        final TextView size;
        final TextView date;
        final TextView time;
        final ImageButton cancel;

        public UploadedResourceListViewHolder(View itemView) {
            super(itemView);

            resource_type = (ImageView) itemView.findViewById(R.id.data_resource_resource_type_iv);
            title = (TextView) itemView.findViewById(R.id.data_resource_resource_title_tv);
            size = (TextView) itemView.findViewById(R.id.data_resource_resource_size);
            cancel = (ImageButton) itemView.findViewById(R.id.data_resource_cancel);
            date = (TextView) itemView.findViewById(R.id.data_resource_resource_date);
            time = (TextView) itemView.findViewById(R.id.data_resource_resource_time);

            cancel.setVisibility(View.VISIBLE);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadedResourceListInterface.btn_clicked(getAdapterPosition());
                }
            });


        }
    }


    public interface UploadedResourceListInterface {

        void btn_clicked(int position);

    }


    private void setImage(String fileExtension,UploadedResourceListViewHolder holder) {

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
