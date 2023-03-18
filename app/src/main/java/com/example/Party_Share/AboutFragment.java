package com.example.Party_Share;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.Party_Share.databinding.FragmentAboutBinding;


public class AboutFragment extends Fragment {
    FragmentAboutBinding aboutBinding;
    int d = 1;
    int m = 0;
    int y = 2023;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aboutBinding = FragmentAboutBinding.inflate(inflater, container, false);
        setDate();
        aboutBinding.dateEditBtn.setOnClickListener((view)->{
            Dialog dialog = new DatePickerDialog(getContext(),(datePicker,yy,mm,dd)->{
                y = yy;
                m = mm;
                d = dd;
                setDate();
            },y,m,d);
            dialog.show();
        });


        aboutBinding.dateInputEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Dialog dialog = new DatePickerDialog(getContext(),(datePicker,yy,mm,dd)->{
                        y = yy;
                        m = mm;
                        d = dd;
                        setDate2();
                    },y,m,d);
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
        return aboutBinding.getRoot();
    }
    void setDate2(){
        aboutBinding.dateInputEt.setText("" + d + "/" + (m +1) + "/" + y);
    }
    void setDate(){
        aboutBinding.dateTv.setText("" + d + "/" + (m +1) + "/" + y);
    }
}