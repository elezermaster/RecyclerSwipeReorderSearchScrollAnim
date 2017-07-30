package com.jsontodb.undoswipe.model;

import android.databinding.BindingAdapter;
import android.widget.ImageButton;

/**
 * Created by elezermaster on 30/07/2017.
 */

public class CustomSetters {
    @BindingAdapter("itemImageSrc")
    public static void setImgSrc(ImageButton view, int resId){
        view.setImageDrawable(view.getContext().getDrawable(resId));
        //here you can load image with Glide or any other libs
    }
}
