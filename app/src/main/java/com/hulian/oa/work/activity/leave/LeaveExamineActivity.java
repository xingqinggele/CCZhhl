package com.hulian.oa.work.activity.leave;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hulian.oa.activity.BaseActivity;
import com.hulian.oa.R;
import com.hulian.oa.agency.fragment.UpcomFragment;
import com.hulian.oa.bean.Leave;
import com.hulian.oa.bean.People;
import com.hulian.oa.net.HttpRequest;
import com.hulian.oa.net.OkHttpException;
import com.hulian.oa.net.RequestParams;
import com.hulian.oa.net.ResponseCallback;
import com.hulian.oa.socket.activity.NoticeWorkActivity;
import com.hulian.oa.utils.SPUtils;
import com.hulian.oa.utils.StatusBarUtil;
import com.hulian.oa.utils.ToastHelper;
import com.hulian.oa.views.AlertDialog;
import com.hulian.oa.utils.FullyGridLayoutManager;
import com.hulian.oa.work.activity.leave.l_adapter.LeaveExamineAdapter;
import com.hulian.oa.work.activity.leave.l_fragment.LeaveApprovedFragment;
import com.hulian.oa.work.activity.leave.l_fragment.LeavePendFragment;
import com.hulian.oa.work.activity.mypreview.PicturePreviewActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 请假审批详情
 */
public class LeaveExamineActivity extends BaseActivity {
    @BindView(R.id.tv_chaosong_person_qgl)
    TextView tv_chaosong_person_qgl;
    @BindView(R.id.recycler2)
    RecyclerView recyclerView;
    //请假事由
    @BindView(R.id.tv_leave_reason)
    TextView tv_leave_reason;
    //请假时长
    @BindView(R.id.tv_duration)
    TextView tv_duration;
    //开始时间
    @BindView(R.id.tv_start)
    TextView tv_start;
    //结束时间
    @BindView(R.id.tv_end)
    TextView tv_end;
    //查看他的历史记录
    @BindView(R.id.tv_check_history)
    TextView tv_check_history;
    //驳回
    @BindView(R.id.tv_disagree)
    RadioButton tv_disagree;
    //同意
    @BindView(R.id.tv_agree)
    RadioButton tv_agree;
    AlertDialog myDialog;
    private LeaveExamineAdapter adapter;
    //已经选择图片
    private List<LocalMedia> selectList = new ArrayList<>();
    //图片放大预览测试
    private String[] images = {};
    private int mCount = 1;
    @BindView(R.id.tv_bohui)
    TextView tv_bohui;
    private String bohui = "";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode_white(this);
        setContentView(R.layout.work_leave_examine);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        myDialog = new AlertDialog(this).builder();
        bohui = tv_bohui.getText().toString();
        getData();
        getheitData();

    }
    private void getData() {
        RequestParams params = new RequestParams();
        params.put("id", getIntent().getStringExtra("id"));
        HttpRequest.get_WorkLeave(params, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    tv_leave_reason.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("remark").substring(0,result.getJSONObject("data").getJSONObject("workLeave").getString("remark").length()-3));
                    tv_duration.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("duration"));
                    tv_start.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("startTime"));
                    tv_end.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("endTime"));
                    if ("null".equals(result.getJSONObject("data").getJSONObject("workLeave").getString("describe"))){
                        tv_chaosong_person_qgl.setText("");
                    }else {
                        tv_chaosong_person_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("describe"));
                    }

                    if(!result.getJSONObject("data").getJSONObject("workLeave").getString("picture").equals("null")) {
                        images = result.getJSONObject("data").getJSONObject("workLeave").getString("picture").split(",");
                        init(images);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Toast.makeText(mContext, "请求失败=" + failuer.getEmsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @OnClick({R.id.tv_disagree,R.id.tv_agree,R.id.tv_check_history,R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_check_history://查看历史记录
                Intent itent=new Intent(LeaveExamineActivity.this,LeaveHistoryActivity.class);
                itent.putExtra("id",getIntent().getStringExtra("createByid"));
                startActivity(itent);
                break;
            case R.id.tv_disagree://驳回
                postData("2",bohui);
                break;
            case R.id.tv_agree://同意
                postData("1",bohui);
                break;
            case R.id.iv_back://返回
                finish();
                break;
        }
    }

    private void postData(String state,String approver) {
        RequestParams params = new RequestParams();
        params.put("ids", getIntent().getStringExtra("id"));
        params.put("nowApprove", SPUtils.get(LeaveExamineActivity.this, "userId", "").toString());
        params.put("nowApproveName", SPUtils.get(LeaveExamineActivity.this, "nickname", "").toString());
        if(!state.equals("")){
            params.put("state", state);
        }
        if(!"".equals(approver)){
            params.put("approvalOpinions", approver);
        }
        HttpRequest.get_Work_edit(params, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    ToastHelper.showToast(mContext, result.getString("msg"));
                    if( result.getString("code").equals("0")){
                        EventBus.getDefault().post(new LeavePendFragment());
                        EventBus.getDefault().post(new LeaveApprovedFragment());
                        EventBus.getDefault().post(new UpcomFragment());
                        // 消息通知
                        EventBus.getDefault().post(new NoticeWorkActivity());


                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(OkHttpException failuer) {
                Toast.makeText(mContext, "请求失败=" + failuer.getEmsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化图片信息
    private void init(String[] images) {
        for (int i=0;i<images.length;i++){
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(images[i]);
            selectList.add(localMedia);
        }
        //图片信息适配
        FullyGridLayoutManager manager = new FullyGridLayoutManager(LeaveExamineActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new LeaveExamineAdapter(mContext);
        adapter.setList(selectList);
        recyclerView.setAdapter(adapter);

        //图片信息大图预览
        adapter.setOnItemClickListener(new LeaveExamineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                Intent intent = new Intent(mContext, PicturePreviewActivity.class);
                intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectList);
                intent.putExtra(PictureConfig.EXTRA_POSITION, position);
                mContext.startActivity(intent);
            }
        }) ;
    }
    public void onEventMainThread(People ev) {
        myDialog.setGone().setTitle("提示").setMsg("确定转交给 "+ev.getUserName()+" 吗?").setNegativeButton("取消",null).setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData("",ev.getUserId());
            }
        }).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 获取请假历史长度
    private void getheitData() {
        RequestParams params = new RequestParams();
        params.put("pageStart", mCount * 10 - 9 + "");
        params.put("pageEnd", mCount * 10 + "");
        params.put("createBy",getIntent().getStringExtra("createByid"));
        HttpRequest.get_listWorkLeave(params, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<Leave> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<Leave>>() {}.getType());
                    tv_check_history.setText("查看TA的历史记录("+memberList.size()+")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Toast.makeText(mContext, "请求失败=" + failuer.getEmsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
