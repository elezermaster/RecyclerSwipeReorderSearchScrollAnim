package com.jsontodb.undoswipe.handler;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
