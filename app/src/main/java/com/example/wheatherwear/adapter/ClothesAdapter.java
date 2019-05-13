package com.example.wheatherwear.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wheatherwear.R;
import com.example.wheatherwear.bean.ImageBean;

import java.util.List;

/**
 * @author lxh
 * @date 2019/5/13
 */
public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {

    private List<ImageBean> imageList;
    private Context context;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView clothesImage;
        TextView clothesName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clothesImage = (ImageView) itemView.findViewById(R.id.clothes_image);
            clothesName = (TextView) itemView.findViewById(R.id.clothes_name);
        }
    }
    public ClothesAdapter(List<ImageBean> imageList){
        this.imageList=imageList;
    }


    @NonNull
    @Override
    public ClothesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.clothes_item,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClothesAdapter.ViewHolder viewHolder, int i) {
        ImageBean imageBean=imageList.get(i);
        Glide.with(context).load(imageBean.getImagePath()).into(viewHolder.clothesImage);
        viewHolder.clothesName.setText(imageBean.getImageName());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
