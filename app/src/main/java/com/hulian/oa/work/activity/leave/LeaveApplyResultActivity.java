package com.hulian.oa.work.activity.leave;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hulian.oa.activity.BaseActivity;
import com.hulian.oa.R;
import com.hulian.oa.net.HttpRequest;
import com.hulian.oa.net.OkHttpException;
import com.hulian.oa.net.RequestParams;
import com.hulian.oa.net.ResponseCallback;
import com.hulian.oa.utils.StatusBarUtil;
import com.hulian.oa.work.activity.leave.l_adapter.LeaveResultAdapter;
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

/**
 * 请假（员工）结果详情
 */
public class LeaveApplyResultActivity extends BaseActivity {
    @BindView(R.id.tv_miaoshu)
    TextView tv_miaoshu;
    @BindView(R.id.tv_leave_shenqingren)
    TextView tv_leave_shenqingren;
    @BindView(R.id.tv_shenqing_time_qgl)
    TextView tv_shenqing_time_qgl;
    @BindView(R.id.tv_shenqingren_qgl)
    TextView tv_shenqingren_qgl;
    @BindView(R.id.tv_wancheng_qgl)
    TextView tv_wancheng_qgl;
    @BindView(R.id.tv_wanzcheng_time_qgl)
    TextView tv_wanzcheng_time_qgl;
    @BindView(R.id.liucheng_img_my)
    ImageView liucheng_img_my;
    @BindView(R.id.iv_result)
    SimpleDraweeView iv_result;
    //审批人
    @BindView(R.id.tv_approved_person)
    TextView tv_approved_person;
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
    @BindView(R.id.rl_chaosong)
    RelativeLayout rlChaosong;
    @BindView(R.id.tv_chaosong_person_qgl)
    TextView tvChaosongPersonQgl;
    //请假事由详细内容
    private LeaveResultAdapter adapter;
    //已经选择图片
    private List<LocalMedia> selectList = new ArrayList<>();
    //图片放大预览测试
    private String[] images = {};
    String path = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode_white(this);
        setContentView(R.layout.work_leave_result);
        ButterKnife.bind(this);
        mContext = this;
        getData();
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
                    tv_approved_person.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("approverName"));
                    String status = result.getJSONObject("data").getJSONObject("workLeave").getString("state");
                    if (status.equals("1")) {
                        liucheng_img_my.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.liucheng_icon_qgl2));
                        tv_shenqing_time_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("startTime"));
                        // 修改过一次
                        tv_shenqingren_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("approverName"));
                        tv_wancheng_qgl.setTextColor(Color.parseColor("#ff313131"));
                        tv_wanzcheng_time_qgl.setVisibility(View.VISIBLE);
                        String a = result.getJSONObject("data").getJSONObject("workLeave").getString("approvalTime");
                        String b = a.substring(0, a.length() - 8);
                        tv_wanzcheng_time_qgl.setText(b);
                    } else if (status.equals("2")) {
                        liucheng_img_my.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.liucheng_icon_qgl2));
                        tv_shenqing_time_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("startTime"));
                        tv_shenqingren_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("approverName"));
                        tv_wancheng_qgl.setText("驳回");
                        tv_wancheng_qgl.setTextColor(Color.parseColor("#ff313131"));
                        tv_wanzcheng_time_qgl.setVisibility(View.VISIBLE);
                        String a = result.getJSONObject("data").getJSONObject("workLeave").getString("approvalTime");
                        String b = a.substring(0, a.length() - 8);
                        tv_wanzcheng_time_qgl.setText(b);
                        tv_wanzcheng_time_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("approvalTime"));
                    } else {
                        liucheng_img_my.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.liucheng_icon_qgl1));
                        tv_shenqing_time_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("startTime"));
                        tv_shenqingren_qgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("nowApproveName"));
                        tv_wancheng_qgl.setText("完成");
                        tv_wancheng_qgl.setTextColor(Color.parseColor("#DBDBDB"));
                        tv_wanzcheng_time_qgl.setVisibility(View.GONE);
                    }
                    tv_leave_reason.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("cause"));
                    tv_duration.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("duration"));
                    tv_start.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("startTime"));
                    tv_end.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("endTime"));
                    if ("null".equals(result.getJSONObject("data").getJSONObject("workLeave").getString("describe"))){
                        tv_miaoshu.setText("");
                    }else {
                        tv_miaoshu.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("describe"));
                    }
                    tv_leave_shenqingren.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("remark").substring(0,result.getJSONObject("data").getJSONObject("workLeave").getString("remark").length()-3));
                    tvChaosongPersonQgl.setText(result.getJSONObject("data").getJSONObject("workLeave").getString("copier"));
                    if (!result.getJSONObject("data").getJSONObject("workLeave").getString("picture").equals("null")) {
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
    @OnClick({R.id.iv_back, R.id.iv_result})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_result:
                if (!path.equals("")){
                    Intent intent = new Intent(mContext, PicturePreviewActivity.class);
                    intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectList);
                    intent.putExtra(PictureConfig.EXTRA_POSITION, 1);
                    mContext.startActivity(intent);
                    break;
                }

        }

    }

    //初始化图片信息
    private void init(String[] images) {
//        修改了images.length
        LocalMedia localMedia = new LocalMedia();
        for (int i = 0; i < 1; i++) {
            localMedia.setPath(images[i]);
            selectList.add(localMedia);
        }
        if (localMedia.isCompressed()) {
            // 压缩过
            path = localMedia.getCompressPath();
        } else {
            // 原图
            path = localMedia.getPath();
        }
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        iv_result.setImageURI(path);

    }
}
