package com.myapp.unknown.iNTCON.DataResource;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.unknown.iNTCON.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by UNKNOWN on 2/7/2017.
 */
public class UploadResourceAdapter extends RecyclerView.Adapter<UploadResourceAdapter.UploadResourceViewholder> {

    final ArrayList<File> Files;
    final Context context;
    final UploadResourceInterface uploadResourceInterface;

    private final int TYPE_HEADER = 0;
    private final int TYPE_LIST  = 1;


    public UploadResourceAdapter(Context context, ArrayList<File> Files,UploadResourceInterface uploadResourceInterface){

        this.Files = Files;
        this.context = context;
        this.uploadResourceInterface = uploadResourceInterface;

    }

    @Override
    public UploadResourceAdapter.UploadResourceViewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEADER)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.upload_resource_header,parent,false);
            return new UploadResourceViewholder(view,viewType);

        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.upload_resource_items,parent,false);
            return new UploadResourceViewholder(view,viewType);
        }

    }

    @Override
    public void onBindViewHolder(UploadResourceAdapter.UploadResourceViewholder holder, int position) {

        if(position > 0)
        {
            holder.resource_title.setText(Files.get(position - 1).getName());
            Float fileSize = (float) (Files.get(position - 1).length() / 1024);

            if(fileSize > 1024)
            {
                fileSize = fileSize / 1024;
                holder.resource_size.setText(context.getString(R.string.file_size_MB,fileSize));
            }
            else
            {
                holder.resource_size.setText(context.getString(R.string.file_size_KB,fileSize));
            }


            String fileName = Files.get(position - 1).getName();
            int lastIndex = fileName.lastIndexOf(".");
            String fileExtension = fileName.substring(lastIndex + 1);
            setImage(fileExtension,holder);
        }


    }

    @Override
    public int getItemCount() {
        return Files.size() + 1;
    }


    public class UploadResourceViewholder extends RecyclerView.ViewHolder {

        TextView resource_title, resource_size;
        ImageView resource_type;
        ImageButton remove;

        Button uploadAll;
        Button chooseOther;

        public UploadResourceViewholder(View itemView, int viewType) {
            super(itemView);

            if(viewType == TYPE_LIST)
            {
                resource_title = (TextView)itemView.findViewById(R.id.upload_resource_title);
                resource_size = (TextView) itemView.findViewById(R.id.upload_resource_size);
                resource_type = (ImageView) itemView.findViewById(R.id.upload_resource_type_iv);
                remove = (ImageButton) itemView.findViewById(R.id.upload_resource_remove);
            }
            else
            {
                uploadAll = (Button) itemView.findViewById(R.id.upload_resource_all);
                chooseOther = (Button) itemView.findViewById(R.id.upload_resource_choose_other);
            }


            if(remove != null)
            {
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadResourceInterface.removeSelected(getAdapterPosition() - 1);
                    }
                });
            }

            if(chooseOther != null)
            {
                chooseOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadResourceInterface.chooseOtherSelected();
                    }
                });
            }

            if(uploadAll != null)
            {


                uploadAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadResourceInterface.uploadAllSelected();
                    }
                });
            }
        }
    }


    public interface UploadResourceInterface{
        void uploadAllSelected();
        void chooseOtherSelected();
        void removeSelected(int position);
    }


    private void setImage(String fileExtension,UploadResourceViewholder holder) {

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

    @Override
    public int getItemViewType(int position) {

        if(position == 0)
        {
            return TYPE_HEADER;
        }
        else
        {
            return TYPE_LIST;
        }

    }
}
