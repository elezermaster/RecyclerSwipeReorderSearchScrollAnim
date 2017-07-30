package com.jsontodb.undoswipe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jsontodb.undoswipe.R;
import com.jsontodb.undoswipe.handler.ItemTouchHelperViewHolder;
//import com.jsontodb.undoswipe.databinding.ItemDataBinding;
import com.jsontodb.undoswipe.model.Item;

/**
 * Created by elezermaster on 30/07/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder implements
        ItemTouchHelperViewHolder,View.OnClickListener {
    private static final String TAG = ItemViewHolder.class.getSimpleName();
    //private ItemDataBinding itemDataBinding;
    protected RelativeLayout container;
    protected TextView tvItemName;
    protected TextView tvItemMobile;
    protected ImageView ivReorder;
    protected RelativeLayout relativeReorder;

//    public ItemViewHolder(ItemDataBinding dataBinding){
//        super(dataBinding.getRoot());
//        this.itemDataBinding = dataBinding;
//    }
//
//    public void bind(Item item){
//        this.itemDataBinding.setViewModel(item);
//    }

//    public ItemDataBinding getItemDataBinding(){
//        return this.itemDataBinding;
//    }

    public ItemViewHolder(final View v) {
        super(v);
        container = (RelativeLayout) v.findViewById(R.id.container);
        tvItemName = (TextView) v.findViewById(R.id.tvItemName);
        tvItemMobile = (TextView) v.findViewById(R.id.tvItemMobile);
        ivReorder = (ImageView) v.findViewById(R.id.ivReorder);
        relativeReorder = (RelativeLayout) v.findViewById(R.id.relativeReorder);
        v.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(TAG, "mobile: "+ tvItemMobile.getText());

                Intent dialerIntent = new Intent(Intent.ACTION_DIAL);
                dialerIntent.setData(Uri.parse("tel:"+tvItemMobile.getText()));
                dialerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(dialerIntent);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemSelected(Context context) {
        container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        tvItemName.setTextColor(ContextCompat.getColor(context, R.color.white));
        tvItemMobile.setTextColor(ContextCompat.getColor(context, R.color.white));
        ivReorder.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onItemClear(Context context) {
        container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        ivReorder.setColorFilter(ContextCompat.getColor(context, R.color.textlight), PorterDuff.Mode.SRC_IN);
        tvItemName.setTextColor(ContextCompat.getColor(context, R.color.textlight));
        tvItemMobile.setTextColor(ContextCompat.getColor(context, R.color.textlight));
    }

}
