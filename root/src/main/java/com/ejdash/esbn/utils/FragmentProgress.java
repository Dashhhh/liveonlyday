package com.ejdash.esbn.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ejdash.esbn.R;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentProgress.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentProgress#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProgress extends Fragment {

    Dialog dialog;
    Handler mHandler;
    private OnFragmentInteractionListener mListener;
    private Context context;
    private  fr.castorflex.android.circularprogressbar.CircularProgressBar mProgressBar;

    public FragmentProgress() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentProgress.
     */

    public static FragmentProgress newInstance() {
        FragmentProgress fragment = new FragmentProgress();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void vodListRefreshHandler() {
//
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.progress_circle);
//        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
//        lp.dimAmount = 0.5f;
//        lp.alpha = 0.5f;
//
//        dialog.show();
//
//
//        mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 800);
        mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable
                .Builder(context)
                .colors(getResources().getIntArray(R.array.progressColor))
                .sweepSpeed(1f)
                .strokeWidth(0.5f)
                .build());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_fragment_progress, container, false);
        context = view.getContext();
        mProgressBar = view.findViewById(R.id.mProgressVOD);


        vodListRefreshHandler(); // 다이얼로그 창 호출

        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
