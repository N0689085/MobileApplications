package com.example.haveyou;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haveyou.array_item;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<array_item> madaptArray;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;


        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mTextView1 = itemView.findViewById(R.id.MainText);
            mTextView2 = itemView.findViewById(R.id.SubText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public Adapter(ArrayList<array_item> adaptArray){
        madaptArray = adaptArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.array_item,parent,false);
        ViewHolder VH = new ViewHolder(v, mListener);
        return VH;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        array_item currentItem = madaptArray.get(position);

        holder.mImageView.setImageResource(currentItem.getImage());
        holder.mTextView1.setText(currentItem.getMainText());
        holder.mTextView2.setText(currentItem.getSubText());
    }

    @Override
    public int getItemCount() {
        return madaptArray.size();
    }
}
