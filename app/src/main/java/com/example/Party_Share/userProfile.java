package com.example.Party_Share;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.Party_Share.databinding.FragmentUserProfileBinding;
import com.example.Party_Share.model.Model;
import com.example.Party_Share.model.WeatherDetails;
import com.example.Party_Share.model.WeatherModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class userProfile extends Fragment {

    private UserProfileViewModel mViewModel;
    FragmentUserProfileBinding binding;
    private BottomNavigationView bottomNavigationView;
    SharedPreferences sp;

    public static userProfile newInstance() {
        return new userProfile();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        mViewModel = viewModelProvider.get(UserProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        bottomNavigationView = getActivity().findViewById(R.id.main_bottomNavigationView);
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        sp = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        binding.profileLabel.setText(sp.getString("label",""));
        binding.profileEmail.setText(sp.getString("email",""));
        binding.logout.setOnClickListener((view1 -> {
            Model.instance().signOut();
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            Intent i = new Intent(getContext(), LogInActivity.class);
            Bundle bundle =
                    ActivityOptionsCompat.makeCustomAnimation(
                    getContext(), android.R.anim.fade_in, android.R.anim.fade_out)
                    .toBundle();
            startActivity(i,bundle);
            getActivity().finish();
        }));
        updateCurrentWeatherTextLabel(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment profilePostListFragment = new PostsListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_posts_frame,profilePostListFragment);
        transaction.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onStart() {
        super.onStart();
        mViewModel.setActiveState(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setActiveState(true);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setActiveState(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);
    }
    private void updateCurrentWeatherTextLabel(View view) {
        MainActivity activity = (MainActivity) getActivity();
        TextView weatherLabel = view.findViewById(R.id.currentWeather);
        LiveData<WeatherDetails> data = WeatherModel.instance.getCurrentWeatherForLocation(activity.locationLatitude, activity.locationLongitude);
        data.observe(getViewLifecycleOwner(), weatherDetails -> {
            String weatherText = "Weather in your area: " + weatherDetails.getTemperature() + " °C";
            weatherLabel.setText(weatherText);
        });
    }
}