package com.yahya.shadow.MyRatingHistory;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yahya.shadow.R;

public class RatingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView mRaterPic;
    public TextView mRaterUser,mRateNum;
    public RatingBar mRateVal;

    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mRaterPic = itemView.findViewById(R.id.raterPic);
        mRaterUser = itemView.findViewById(R.id.rateUsername);
        mRateVal = itemView.findViewById(R.id.rateVal);
        mRateNum = itemView.findViewById(R.id.rateNum);
    }

    @Override
    public void onClick(View v) {

    }
}
