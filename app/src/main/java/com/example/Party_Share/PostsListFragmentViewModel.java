package com.example.Party_Share;

import androidx.lifecycle.ViewModel;

import com.example.Party_Share.model.Post;

import java.util.LinkedList;
import java.util.List;

public class PostsListFragmentViewModel extends ViewModel {
    private List<Post> data = new LinkedList<>();

    List<Post> getData(){
        return data;
    }

    void setData(List<Post> list){
        data = list;
    }
}
