package com.work.fyc.MyWork.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.work.fyc.MyWork.Circle;
import com.work.fyc.MyWork.MainActivity;
import com.work.fyc.MyWork.R;
import com.work.fyc.MyWork.entity.CardEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainPostsAdapter extends RecyclerView.Adapter<MainPostsAdapter.PostVH> {

    public interface OnItemClick {
        void onLook(int position);

        void onDelete(int position);

        void onSetTop(int position);
    }

    private Context context;
    private ArrayList<CardEntity> cardsList;
    private OnItemClick clickListener;

    private String sID;

    public MainPostsAdapter(Context context, ArrayList<CardEntity> cardsList, OnItemClick clickListener) {
        this.context = context;
        this.cardsList = cardsList;
        this.clickListener = clickListener;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    @NonNull
    @Override
    public PostVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_listview_post_item, viewGroup, false);
        return new PostVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostVH postVH, int position) {
        CardEntity item = cardsList.get(position);
        postVH.personImgView.setImageResource(R.drawable.bg);
        postVH.tvName.setText(item.name);
        postVH.ivPic.setImageResource(R.drawable.section);
        postVH.tvContent.setText(item.contents);
        postVH.tvTitleC.setText(item.title);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(Long.parseLong(item.date));
        postVH.tvDate.setText(format1.format(date));

        SharedPreferences sharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        if ("1".equals(sharedPreferences.getString("power", ""))
                || MainActivity.FLAG == 2
                || sharedPreferences.getString("id", "").equals(sID))
            postVH.tvDelete.setVisibility(View.VISIBLE);
        else
            postVH.tvDelete.setVisibility(View.GONE);

        if ("1".equals(sharedPreferences.getString("power", "")))
            postVH.tvSetTop.setVisibility(View.VISIBLE);
        else
            postVH.tvSetTop.setVisibility(View.GONE);

        postVH.tvLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = postVH.getAdapterPosition();
                if (newPosition >= 0 && newPosition < getItemCount())
                    clickListener.onLook(newPosition);
            }
        });
        postVH.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = postVH.getAdapterPosition();
                if (newPosition >= 0 && newPosition < getItemCount())
                    clickListener.onDelete(newPosition);
            }
        });
        postVH.tvSetTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = postVH.getAdapterPosition();
                if (newPosition >= 0 && newPosition < getItemCount())
                    clickListener.onSetTop(newPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    class PostVH extends RecyclerView.ViewHolder {
        TextView tvName;
        Circle personImgView;
        ImageView ivPic;
        TextView tvContent;
        TextView tvTitleC;
        TextView tvDate;
        TextView tvLook;
        TextView textView5;
        TextView tvDelete;
        TextView tvSetTop;

        PostVH(@NonNull View convertView) {
            super(convertView);
            personImgView = convertView.findViewById(R.id.person);
            tvName = convertView.findViewById(R.id.name);
            ivPic = convertView.findViewById(R.id.pic);
            tvContent = convertView.findViewById(R.id.content);
            tvTitleC = convertView.findViewById(R.id.title_c);
            tvDate = convertView.findViewById(R.id.date);
            tvLook = convertView.findViewById(R.id.look);
            tvDelete = convertView.findViewById(R.id.del);
            tvSetTop = convertView.findViewById(R.id.top);
        }


    }
}
