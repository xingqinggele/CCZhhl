package com.hulian.oa.work.file.admin.activity.instruct.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hulian.oa.BuildConfig;
import com.hulian.oa.R;
import com.hulian.oa.bean.Fab2;
import com.hulian.oa.pad.PAD_zhiling_XF;
import com.hulian.oa.utils.SPUtils;
import com.hulian.oa.views.fabVIew.FabAttributes;
import com.hulian.oa.views.fabVIew.OnFabClickListener;
import com.hulian.oa.views.fabVIew.SuspensionFab_qgl;
import com.hulian.oa.work.file.admin.activity.PostOrderActivity;
import com.hulian.oa.work.file.admin.activity.SecondInstructActivity;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

/**
 * Created by qgl on 2019/12/17 14:15.
 */
public class Fra_post_order_qgl extends Fragment {
    @BindView(R.id.ll_order)
    LinearLayout llOrder;
    Unbinder unbinder;
    SuspensionFab_qgl fabTop;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_order_post_qgl, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        if (SPUtils.get(getActivity(), "isLead", "").equals("0")) {
            llOrder.setVisibility(View.VISIBLE);
        } else {
            llOrder.setVisibility(View.GONE);
        }

        fabTop = (SuspensionFab_qgl) view.findViewById(R.id.fab_top);
//构建展开按钮属性
        FabAttributes collection = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#FF6F3D"))
                .setSrc(getResources().getDrawable(R.mipmap.daiban_gongwen_icon))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(1)
                .build();
        FabAttributes email = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#00B5B9"))
                .setSrc(getResources().getDrawable(R.mipmap.daiban_huiyi_icon))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(2)
                .build();
        FabAttributes renwu = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#4768F3"))
                .setSrc(getResources().getDrawable(R.mipmap.daiban_renwu_icon))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(3)
                .build();
        FabAttributes qingjia = new FabAttributes.Builder()
                .setBackgroundTint(Color.parseColor("#F64250"))
                .setSrc(getResources().getDrawable(R.mipmap.daiban_qingjia_icon))
                .setFabSize(FloatingActionButton.SIZE_NORMAL)
                .setPressedTranslationZ(10)
                .setTag(4)
                .build();
//添加菜单
        fabTop.addFab(collection, email, renwu, qingjia);
        //    fabTop.setAnimationManager(new FabAlphaAnimate(fabTop));
//设置菜单点击事件
        fabTop.setFabClickListener(new OnFabClickListener() {
            @Override
            public void onFabClick(FloatingActionButton fab, Object tag) {
                if (Integer.parseInt(tag + "") == 1) {
                    fabTop.closeAnimate();
                    EventBus.getDefault().post("公文审批");
                } else if (Integer.parseInt(tag + "") == 2) {
                    fabTop.closeAnimate();
                    EventBus.getDefault().post("会议安排");
                } else if (Integer.parseInt(tag + "") == 3) {
                    fabTop.closeAnimate();
                    EventBus.getDefault().post("任务协同");
                } else {
                    fabTop.closeAnimate();
                    EventBus.getDefault().post("请假审批");
                }
            }
        });

        return view;
    }

    public void onEventMainThread(Fab2 event_x) {
        fabTop.closeAnimate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}