package com.jsontodb.undoswipe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jsontodb.undoswipe.R;
import com.jsontodb.undoswipe.Typefaces;
import com.jsontodb.undoswipe.handler.FavImgClickHandler;
import com.jsontodb.undoswipe.helper.Contact;
import com.jsontodb.undoswipe.helper.DatabaseHandler;
import com.jsontodb.undoswipe.handler.ItemTouchHelperAdapter;
import com.jsontodb.undoswipe.model.Item;
import com.l4digital.fastscroll.FastScroller;
//import com.jsontodb.undoswipe.databinding.ItemDataBinding;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter,
        FastScroller.SectionIndexer {

    private static final String TAG = ItemAdapter.class.getSimpleName();
    private OnStartDragListener dragStartListener;

    @Override
    public String getSectionText(int position) {
        return itemList.get(position).getItemName().substring(0,1);
    }


    public interface OnStartDragListener {

        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    //public interface OnStartDragListener {
    //    void onStartDrag(RecyclerView.ViewHolder viewHolder);
    //}

    private final Context context;
    public static List<Item> itemList = new ArrayList<>();
    private TextView tvNumber;
    private LayoutInflater layoutInflater;

    public ItemAdapter(Context context, OnStartDragListener dragStartListener, TextView tvNumber) {
        this.context = context;
        this.dragStartListener=dragStartListener;
        this.tvNumber=tvNumber;

    }

    @Override
    public void onItemDismiss(final int position) {

        final Item itemToDelete =new Item();
        itemToDelete.setItemName(itemList.get(position).getItemName());
        itemToDelete.setItemMobile(itemList.get(position).getItemMobile());
        Contact contactToDelete = new Contact();
        contactToDelete.setName(itemToDelete.getItemName());
        contactToDelete.setPhoneNumber(itemToDelete.getItemMobile());

        notifyItemRemoved(position);
        itemList.remove(position);
        notifyItemRangeChanged(0, getItemCount());
        tvNumber.setText(String.valueOf(itemList.size()));
        //remove from db
         DatabaseHandler db = new DatabaseHandler(context);
        db.deleteContact(contactToDelete);

        final Snackbar snackbar =  Snackbar
                .make(tvNumber,context.getResources().getString(R.string.item_deleted), Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.white))
                .setAction(context.getResources().getString(R.string.item_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       itemList.add(position, itemToDelete);
                        notifyItemInserted(position);
                        tvNumber.setText(String.valueOf(itemList.size()));

                    }
                });


        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView tvSnack = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        TextView tvSnackAction = (TextView) snackbar.getView().findViewById( android.support.design.R.id.snackbar_action );
        tvSnack.setTextColor(Color.WHITE);
        tvSnack.setTypeface(Typefaces.getRobotoMedium(context));
        tvSnackAction.setTypeface(Typefaces.getRobotoMedium(context));
        snackbar.show();


       Runnable runnableUndo = new Runnable() {

            @Override
            public void run() {
                tvNumber.setText(String.valueOf(itemList.size()));
                snackbar.dismiss();
            }
        };
        Handler handlerUndo=new Handler();handlerUndo.postDelayed(runnableUndo,2500);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    public void addItem(int position, Item item) {

        itemList.add(position, item);
        notifyItemInserted(position);
        tvNumber.setText(String.valueOf(itemList.size()));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(viewGroup.getContext());//.inflate(R.layout.grocery_adapter, viewGroup, false);
        }
        //ItemDataBinding itemDataBinding = ItemDataBinding.inflate(layoutInflater, viewGroup, false );
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grocery_adapter, viewGroup, false);
        return new ItemViewHolder(itemView);
        //return new ItemViewHolder(itemDataBinding);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final Item item = itemList.get(position);
        //holder.bind(item);
        //ItemDataBinding dataBinding = holder.getItemDataBinding();
//        dataBinding.setHandler(new FavImgClickHandler() {
//            @Override
//            public void onFavImgClick() {
//                if(item.itemImageSrc.get() == R.drawable.ic_dehaze_white_24dp ){
//                    item.itemImageSrc.set(R.drawable.if_head_24x24);
//                }else{
//                    item.itemImageSrc.set(R.drawable.ic_dehaze_white_24dp);
//                }
//            }
//        });

        //final Item item = itemList.get(position);
        holder.tvItemName.setText(item.getItemName());
        holder.tvItemMobile.setText(item.getItemMobile());
        holder.relativeReorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }



}
