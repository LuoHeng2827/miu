package com.luoheng.miu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import com.luoheng.miu.bean.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.luoheng.miu.OwnerFragment.REQUEST_CHOOSE_PHOTO;
import static com.luoheng.miu.OwnerFragment.REQUEST_PHOTO_CUT;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    DiscussFragment discussFragment;
    HomeFragment homeFragment;
    FragmentManager fragmentManager;
    OwnerFragment ownerFragment;
    static User user;


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
                case R.id.navigation_owner:
                    if(ownerFragment==null){
                        ownerFragment=new OwnerFragment();
                        transaction.add(R.id.first_content,ownerFragment);
                    }
                    transaction.show(ownerFragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHOOSE_PHOTO){
            super.onActivityResult(requestCode, resultCode, data);
        }
        else if(requestCode==REQUEST_PHOTO_CUT){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager=getSupportFragmentManager();
        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra("user");
        initView();
    }


    private void hideAllFragment(FragmentTransaction transaction){
        if(homeFragment!=null) transaction.hide(homeFragment);
        if(discussFragment!=null) transaction.hide(discussFragment);
        if(ownerFragment!=null) transaction.hide(ownerFragment);
    }

    private void initView(){
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_discuss);

    }

}
