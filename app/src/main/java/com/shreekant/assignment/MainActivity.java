package com.shreekant.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shreekant.assignment.network.InternetConnection;
import com.shreekant.assignment.pojo.UserList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;
import static com.shreekant.assignment.PaginationListener.PAGE_START;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;

    @BindView(R.id.swipe_refreshLayout)
    SwipeRefreshLayout swipe_refreshLayout;

    @BindView(R.id.recycleview_user)
    RecyclerView recycleview_user;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;



    UserAdapter userAdapter;
    LinearLayoutManager linearLayoutManager;

    //
    private int currentPage = PAGE_START;
    String itemCount = "5";
    List<UserList.Datum> datumList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        datumList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recycleview_user.setLayoutManager(linearLayoutManager);
        userAdapter = new UserAdapter(MainActivity.this, new ArrayList<>());
        recycleview_user.setAdapter(userAdapter);


        swipe_refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refreshLayout.setRefreshing(true);
                onResume();
            }
        });


        recycleview_user.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    getUserList(currentPage++);

                }
            }
        });

    }

    void getUserList(int page) {
        /**
         GET List Users
         **/
        Call<UserList> call2 = apiInterface.doGetUserList(String.valueOf(page), itemCount);
        call2.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                UserList userList = response.body();
                if(page == PAGE_START){
                    swipe_refreshLayout.setRefreshing(false);
                    datumList = userList.data;
                } else {
                    datumList.addAll(userList.data);
                }
                userAdapter = new UserAdapter(MainActivity.this, datumList);
                recycleview_user.setAdapter(userAdapter);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (InternetConnection.checkConnection(MainActivity.this)) {
            getUserList(PAGE_START);
        } else {
            InternetConnection.showNetworkDialog(MainActivity.this);
        }
    }


    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CategoryInfoHolder> {
        Context context;
        List<UserList.Datum> responseList;

        UserAdapter(Context context, List<UserList.Datum> responseList) {
            this.context = context;
            this.responseList = responseList;
        }

        @NonNull
        @Override
        public CategoryInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_row_user, parent, false);
            CategoryInfoHolder categoryInfoHolder = new CategoryInfoHolder(view);
            return categoryInfoHolder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CategoryInfoHolder holder, int position) {
            holder.tv_name.setText(responseList.get(position).first_name + " " + responseList.get(position).last_name);
            holder.tv_email.setText(responseList.get(position).email);
            Picasso.with(context)
                    .load(responseList.get(position).avatar)
                    .placeholder(R.drawable.user_dp)
                    .into(holder.img_profile);
        }

        @Override
        public int getItemCount() {
            return responseList.size();
        }

        public class CategoryInfoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvName)
            TextView tv_name;
            @BindView(R.id.tvEmail)
            TextView tv_email;
            @BindView(R.id.img_profile)
            ImageView img_profile;

            public CategoryInfoHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }





}