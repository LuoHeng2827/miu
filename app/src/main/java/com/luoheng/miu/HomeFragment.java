package com.luoheng.miu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.luoheng.miu.bean.Unit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    @BindView(R.id.city_button)
    Button cityButton;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.include_error_page)
    View errorPage;
    @BindView(R.id.include_search_layout)
    View searchLayout;
    @BindView(R.id.search_edit)
    EditText searchEdit;
    @BindView(R.id.error_msg_tv)
    TextView errorMsg;
    Context mContext;
    List<Unit> mUnitList=new ArrayList<>();
    static Handler handler=null;
    String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        ButterKnife.bind(this,view);
        ButterKnife.bind(R.layout.search_layout,searchLayout);
        ButterKnife.bind(R.layout.error_page,errorPage);
        city="北京";
        init();
        return view;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        handler = new Handler();
    }

}
