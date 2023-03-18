package com.example.Party_Share;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.Party_Share.databinding.FragmentPostsListBinding;
import com.example.Party_Share.model.Model;
import com.example.Party_Share.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class PostsListFragment extends Fragment {
    FragmentPostsListBinding binding;
    PostRecyclerAdapter postrecycleradapter;
    PostsListFragmentViewModel postListViewModel;
    private BottomNavigationView bottomNavigationView;
    SharedPreferences sp;
    ViewModelProvider viewModelProvider;
    UserProfileViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bottomNavigationView = getActivity().findViewById(R.id.main_bottomNavigationView);
        binding = FragmentPostsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postrecycleradapter = new PostRecyclerAdapter(getLayoutInflater(), postListViewModel.getData());
        binding.recyclerView.setAdapter(postrecycleradapter);
        viewModelProvider = new ViewModelProvider(getActivity());
        userViewModel = viewModelProvider.get(UserProfileViewModel.class);

        postrecycleradapter.setOnItemClickListener(new PostRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Post st = postListViewModel.getData().get(pos);
                Picasso.get().invalidate(st.imgUrl);
                PostsListFragmentDirections.ActionPostsListFragmentToPostFragment action =
                        PostsListFragmentDirections.actionPostsListFragmentToPostFragment(st.title,st.details,st.location,st.label,st.imgUrl,st.id);
                Navigation.findNavController(view).navigate((NavDirections) action);
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        postListViewModel = new ViewModelProvider(this).get(PostsListFragmentViewModel.class);
    }

   @Override
    public void onStart() {
        super.onStart();
        reloadData(userViewModel.getActiveState());
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData(userViewModel.getActiveState());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    void reloadData(Boolean activeState) {
        binding.progressBar.setVisibility(View.VISIBLE);
        if(!activeState) {
            Model.instance().getAllPosts((stList) -> {
                postListViewModel.setData(stList);
                postrecycleradapter.setData(postListViewModel.getData());
                binding.progressBar.setVisibility(View.GONE);
            });
        } else {
            sp = getActivity().getSharedPreferences("user", MODE_PRIVATE);
            Model.instance().getUserPosts(sp.getString("label",""),(stList) -> {
                postListViewModel.setData(stList);
                postrecycleradapter.setData(postListViewModel.getData());
                binding.progressBar.setVisibility(View.GONE);
            });
        }
    }
}