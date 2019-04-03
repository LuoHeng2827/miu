package com.luoheng.miu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    DiscussFragment discussFragment;
    HomeFragment homeFragment;
    FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            hideAllFragment(transaction);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(homeFragment==null){
                        homeFragment=new HomeFragment();
                        transaction.add(R.id.first_content,homeFragment);
                    }
                    transaction.show(homeFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_discuss:
                    if(discussFragment==null){
                        discussFragment=new DiscussFragment();
                        transaction.add(R.id.first_content,discussFragment);
                    }
                    transaction.show(discussFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_video:
                    return true;
                case R.id.navigation_person:
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager=getSupportFragmentManager();
        initView();
    }


    private void hideAllFragment(FragmentTransaction transaction){
        if(homeFragment!=null) transaction.hide(homeFragment);
        if(discussFragment!=null) transaction.hide(discussFragment);
    }

    private void initView(){
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
