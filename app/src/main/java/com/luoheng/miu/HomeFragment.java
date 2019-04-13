package com.luoheng.miu;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.luoheng.miu.bean.OnLoadMoreListener;
import com.luoheng.miu.bean.Unit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final int UPDATE_UNIT_LIST=1;
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
    /*@BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;*/
    UnitAdapter mUnitAdapter;
    Context mContext;
    List<Unit> mUnitList=new ArrayList<>();
    DaMaiSpider spider=new DaMaiSpider();
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

    private void openVerifyActivity(){
        Intent intent=new Intent(getContext(),DaMaiVerifyActivity.class);
        startActivityForResult(intent,1);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==getActivity().RESULT_OK){
                String cookies=data.getStringExtra("cookies");
                refreshData(spider.getCurrentPage()+"");
            }
        }
    }

    private void init(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.what){
                    case UPDATE_UNIT_LIST:
                        mUnitAdapter.notifyDataSetChanged();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        spider.setListener(new DaMaiSpider.OnCompletedListener() {
            @Override
            public void onSuccess(List<Unit> unitList, int currentPage, int totalPage) {
                //setRefreshing(false);
                mUnitList.addAll(unitList);
                handler.sendEmptyMessage(UPDATE_UNIT_LIST);
            }

            @Override
            public void onFailed(String errMsg) {
                //setRefreshing(false);
                errorPage.setVisibility(View.VISIBLE);
            }
        });
        /*refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int currentPage=spider.getCurrentPage();
                int totalPage=spider.getTotalPage();
                if(currentPage<totalPage){
                    refreshData(currentPage+1+"");
                }
            }
        });*/
        mUnitAdapter=new UnitAdapter(mUnitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUnitAdapter);
        recyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            protected void loadMore(int itemCount, int lastItem) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentPage=spider.getCurrentPage();
                        int totalPage=spider.getTotalPage();
                        if(currentPage<totalPage){
                            refreshData(currentPage+1+"");
                        }
                        else{
                            mUnitAdapter.setOver();
                        }
                    }
                },1000);
            }
        });
        //setRefreshing(true);
        //refreshData("1");
    }


    /*private void setRefreshing(boolean refreshing){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        });
    }*/

    private void refreshData(String page) {
        if(spider.needVerify)
            openVerifyActivity();
        spider.clawUnitList(city, page);
        /*if(refreshLayout.isRefreshing())
            setRefreshing(false);*/
    }

    class UnitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final static int TYPE_CONTENT=0;
        private final static int TYPE_FOOTER=1;
        List<Unit> mUnitList;
        FootViewHolder footViewHolder;
        UnitAdapter(List<Unit> unitList){
            mUnitList=unitList;
        }

        public void setOver(){
            footViewHolder.setOver();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            mContext=viewGroup.getContext();
            if(viewType==TYPE_CONTENT){
                View view=LayoutInflater.from(mContext)
                        .inflate(R.layout.item_unit,viewGroup,false);
                return new ItemViewHolder(view);
            }
            else{
                View view=LayoutInflater.from(mContext)
                        .inflate(R.layout.footer_item,viewGroup,false);
                footViewHolder=new FootViewHolder(view);
                return footViewHolder;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(getItemViewType(i)==TYPE_FOOTER){

            }
            else{
                ItemViewHolder itemViewHolder=(ItemViewHolder)viewHolder;
                Unit unit=mUnitList.get(i);
                Glide.with(mContext)
                        .load(unit.getVerticalPic())
                        .into(itemViewHolder.unitImage);
                itemViewHolder.unitPrice.setText(unit.getPrice()+"");
                itemViewHolder.unitTitle.setText(unit.getName());
                itemViewHolder.unitShowTime.setText(unit.getShowTime());
                itemViewHolder.unitVenueCity.setText(unit.getVenueCity());
                itemViewHolder.unitStatus.setText(unit.getStatus());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position==mUnitList.size())
                return TYPE_FOOTER;
            return TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            return mUnitList.size()+1;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.unit_pic)
            ImageView unitImage;
            @BindView(R.id.unit_title)
            TextView unitTitle;
            @BindView(R.id.unit_show_time)
            TextView unitShowTime;
            @BindView(R.id.unit_venue_city)
            TextView unitVenueCity;
            @BindView(R.id.unit_price)
            TextView unitPrice;
            @BindView(R.id.unit_status)
            TextView unitStatus;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
        class FootViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.progressBar)
            ContentLoadingProgressBar progressBar;
            @BindView(R.id.footer_tv)
            TextView footerTv;
            public FootViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
            public void setOver(){
                progressBar.setVisibility(View.INVISIBLE);
                footerTv.setVisibility(View.VISIBLE);
            }
        }
    }
}
