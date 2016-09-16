package com.edgar.restapiapplication.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edgar.restapiapplication.R;
import com.edgar.restapiapplication.activities.MainActivity;
import com.edgar.restapiapplication.api.Api;
import com.edgar.restapiapplication.model.User;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoFragment extends Fragment {

    public static final String LOG = "myLogs";
    public static final String NOT_FOUND_KEY = "key";

    @Bind(R.id.name_surname)
    TextView txtName;
    @Bind(R.id.bio)
    TextView bio;
    @Bind(R.id.image)
    ImageView image;

    public static String userName;

    public UserInfoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.userinfo_fragment, container, false);
        ButterKnife.bind(this, v);

        Bundle bundle = this.getArguments();
        userName = bundle.getString(UserName.KEY);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("User Name Information ");

        tryReguest();
        return v;
    }


    @OnClick(R.id.show_repositories)
    void onClickRepositoriesSearch() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(UserName.KEY, userName);
        transaction.replace(R.id.containerMain, listFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void tryReguest() {
        Api.getInstance().getApiInterface().getUserInformation(userName)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        Log.d(LOG, "ON RESPONSE");
                        int code = response.code();

                        if (code == 200) {

                            final String responseName = response.body().getName();
                            final String responseBio = response.body().getBio();
                            final String responseAvatar = response.body().getAvatar_url();

                            if (responseName != null) txtName.setText(responseName);
                            else txtName.setHint(R.string.unknown);
                            if (responseBio != null) bio.setText(responseBio);
                            else bio.setHint(R.string.user_dont_have_any_information);
                            if (responseAvatar != null) {
                                Picasso.with(image.getContext())
                                        .load(responseAvatar)
                                        .fit()
                                        .into(image);
                            }
                        } else {
                            FragmentTransaction tr = getFragmentManager().beginTransaction();
                            tr.replace(R.id.containerMain, new UserName());
                            tr.commit();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d(LOG, "Errrooeee");
                    }
                });

    }
}
