package com.hulian.oa.work.activity.file;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;


import com.hulian.oa.activity.BaseActivity;
import com.hulian.oa.R;
import com.hulian.oa.work.adapter.Followadapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Admin_logsActivity extends BaseActivity {
    Followadapter adapter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.listview)
    ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_logs);
        ButterKnife.bind(this);
        intRecycle();
    }
    private void intRecycle() {
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(i + "111");
        }
        adapter = new Followadapter(this, list);
        listview.setAdapter(adapter);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
