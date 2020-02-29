package com.android.tabbed_view_pager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flipkart.android.proteus.ProteusView;

/**
 * Created by Prasad Rao on 28-02-2020 18:23
 **/
public class PlaceHolderFragment extends Fragment {

    private ProteusView proteusView;

    private PlaceHolderFragment(ProteusView proteusView) {
        this.proteusView = proteusView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return proteusView.getAsView();
    }

    public static PlaceHolderFragment newInstance(ProteusView proteusView) {
        return new PlaceHolderFragment(proteusView);
    }
}
