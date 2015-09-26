package com.goxapps.goxreader;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BottomMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BottomMenuFragment extends Fragment {

    // TODO ebook type? current settings?
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;

    @Bind(R.id.menu_button_one)
    View mMenuButton;

    int mToolbarSize;

    @BindInt(android.R.integer.config_shortAnimTime)
    int MENU_ANIM_DURATION;

    private int mSystemNavigationHeight;

    boolean menuShown;
    boolean menuExpanded;

    /**
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomMenuFragment newInstance(String param1, String param2) {
        BottomMenuFragment fragment = new BottomMenuFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSystemNavigationHeight = AppUtils.getNav(getActivity());
        mToolbarSize = AppUtils.getActionBarHeight(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_bottom_menu, null);

        ButterKnife.bind(this, rootView);


        Toast.makeText(getActivity(), "system bar : " + mSystemNavigationHeight, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "action bar: " + mToolbarSize, Toast.LENGTH_SHORT).show();

        rootView.setTranslationY(mToolbarSize * 2);

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (menuExpanded) {
                    animateCollapse();
                    menuExpanded = false;
                } else {
                    animateExpand();
                    menuExpanded = true;
                }
            }
        });




        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMenuButton.getLayoutParams().height = mSystemNavigationHeight;
        mMenuButton.setBackgroundColor(getResources().getColor(R.color.hard_orange));


        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
//        params.bottomMargin = mSystemNavigationHeight;

        params.height = (3 * mToolbarSize + mSystemNavigationHeight);

//        rootView.setPadding(0, 0, 0, mSystemNavigationHeight);
//        rootView.setTranslationY(-mSystemNavigationHeight);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public void onSystemNavigationToggled(boolean visible) {
        menuShown = visible;
        if(menuShown) {
            animateShow();
        } else {
            animateHide();
        }
    }

    public void animateHide() {
        animateMenyTranslation(mToolbarSize * 3 + mSystemNavigationHeight);
        menuExpanded = false;
    }

    public void animateShow() {
        animateMenyTranslation(mToolbarSize * 2);
    }

    private void animateExpand() {
        animateMenyTranslation(0);
    }

    private void animateCollapse() {
        animateMenyTranslation(mToolbarSize * 2);
    }

    private void animateMenyTranslation(float ofFloat) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", ofFloat);
        animator.setDuration(MENU_ANIM_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

}
