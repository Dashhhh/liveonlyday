package com.ejdash.esbn.utils;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class BlurDialogCustom extends DialogFragment {

    public BlurDialogCustom() {
    }

    public static BlurDialogCustom newInstance() {
        return new BlurDialogCustom();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.setCanceledOnTouchOutside(true);


        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        Bitmap b1 = view.getDrawingCache();

        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        final int width = getActivity().getWindow().getDecorView().getWidth();
        final int height = getActivity().getWindow().getDecorView().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height-statusBarHeight);

        //define this only once if blurring multiple times
        RenderScript rs = RenderScript.create(getActivity());

        //this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(rs, b); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(b);

        alertDialog.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), b));

        return alertDialog;
    }
}