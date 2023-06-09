package com.example.Party_Share;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.Party_Share.databinding.FragmentAddPostBinding;
import com.example.Party_Share.model.Model;
import com.example.Party_Share.model.Post;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AddNewPostFragment extends Fragment {
    FragmentAddPostBinding binding;
    LatLng location;
    String locationName;
    Double x, y;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;
    SharedPreferences sp;
    Boolean isAvatarSelected = false;
    private BottomNavigationView bottomNavigationView;
    ViewModelProvider viewModelProvider;
    MapsFragmentModel viewModel;

    public static AddNewPostFragment newInstance(LatLng location, String locationName) {
        AddNewPostFragment newPostFragment = new AddNewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        bundle.putString("locationName", locationName);
        newPostFragment.setArguments(bundle);
        return newPostFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        sp = getContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            this.location = bundle.getParcelable("location");
            this.locationName = bundle.getString("locationName");
        }

        FragmentActivity parentActivity = getActivity();
        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.addNewPostFragment);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, this, Lifecycle.State.RESUMED);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
                    MapsFragmentModel viewModel = viewModelProvider.get(MapsFragmentModel.class);
                    binding.avatarImg.setImageBitmap(result);
                    Bundle bundle = new Bundle();
                    if(viewModel.getSavedInstanceStateData() != null){
                        bundle = viewModel.getSavedInstanceStateData();
                    }
                    bundle.putParcelable("imgBitmap",result);
                    viewModel.setSavedInstanceStateData(bundle);
                    isAvatarSelected = true;
                }
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    binding.avatarImg.setImageURI(result);
                    isAvatarSelected = true;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bottomNavigationView = getActivity().findViewById(R.id.main_bottomNavigationView);
        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModelProvider = new ViewModelProvider(getActivity());
        viewModel = viewModelProvider.get(MapsFragmentModel.class);
        if (this.locationName != null) {
            binding.address.setText(locationName);
        }

        binding.addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.mapsFragment, savedInstanceState);
            }
        });
        binding.saveBtn.setOnClickListener(view1 -> {

            String title = binding.Description.getText().toString();
            String details = binding.postDes.getText().toString();
            String location = binding.address.getText().toString();
            String label=sp.getString("label","");
            String id=title;
            try {
                MessageDigest digest=MessageDigest.getInstance("SHA-256");
                byte[] hash=digest.digest((title+
                        Timestamp.now().getSeconds()+
                        Timestamp.now().getNanoseconds()+"")
                        .getBytes(StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : hash) {
                    stringBuilder.append(String.format("%02x", b & 0xff));
                }
                id=stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Post post = new Post(id,title, "", details,  location,label, Timestamp.now().getSeconds());
            if (details.equals("") || title.equals("")) {
                Toast.makeText(getContext(), "missing title or details ", Toast.LENGTH_LONG).show();
            } else {
                if (isAvatarSelected) {
                    binding.avatarImg.setDrawingCacheEnabled(true);
                    binding.avatarImg.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) binding.avatarImg.getDrawable()).getBitmap();
                    Model.instance().uploadImage(id, bitmap, url -> {
                        if (url != null) {
                            post.setImgUrl(url);
                        }
                        Model.instance().addPost(post, (unused) -> {
                            Navigation.findNavController(view1).popBackStack();
                        });
                    });
                } else {
                    Model.instance().addPost(post, (unused) -> {
                        Navigation.findNavController(view1).navigate(R.id.postsListFragment);
                    });
                }
            }
        });
        binding.cancelBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack(R.id.postsListFragment, false));
        binding.cameraButton.setOnClickListener(view1 -> {
            cameraLauncher.launch(null);
        });


        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        MapsFragmentModel viewModel = viewModelProvider.get(MapsFragmentModel.class);
        Bundle savedInstanceStateData = viewModel.getSavedInstanceStateData();
        if(savedInstanceStateData != null) {
            this.location = viewModel.getSavedInstanceStateData().getParcelable("location");
            this.locationName = viewModel.getSavedInstanceStateData().getString("locationName");
            if(locationName != null) {
                binding.address.setText(locationName);
            }
            Bitmap bitmap = viewModel.getSavedInstanceStateData().getParcelable("imgBitmap");
            if (bitmap != null){
                binding.avatarImg.setImageBitmap(bitmap);
            }
        } else {
            viewModel.setSavedInstanceStateData(new Bundle());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);
        viewModel.setSavedInstanceStateData(new Bundle());
    }
}