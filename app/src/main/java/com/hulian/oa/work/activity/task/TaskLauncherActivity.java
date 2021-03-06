package com.hulian.oa.work.activity.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.duke.dfileselector.activity.DefaultSelectorActivity;
import com.duke.dfileselector.util.FileSelectorUtils;
import com.hulian.oa.activity.BaseActivity;
import com.hulian.oa.R;
import com.hulian.oa.bean.MessageEvent_x;
import com.hulian.oa.bean.People;
import com.hulian.oa.bean.People_x;
import com.hulian.oa.net.HttpRequest;
import com.hulian.oa.net.OkHttpException;
import com.hulian.oa.net.RequestParams;
import com.hulian.oa.net.ResponseCallback;
import com.hulian.oa.utils.SPUtils;
import com.hulian.oa.utils.TimeUtils;
import com.hulian.oa.utils.ToastHelper;
import com.hulian.oa.views.AlertDialog;
import com.hulian.oa.views.NonScrollGridView;
import com.hulian.oa.utils.FullyGridLayoutManager;
import com.hulian.oa.adpter.GridImageAdapter;
import com.hulian.oa.work.activity.WriteReportActivity;
import com.hulian.oa.work.activity.expense.ExpenseApplyForPeopleActivityS;
import com.hulian.oa.work.activity.meeting.SelDepartmentActivity_meet_zb;
import com.hulian.oa.work.activity.meeting.SelDepartmentActivity_meet_zb_single;
import com.hulian.oa.work.activity.meeting.l_adapter.MeetGridViewAdapter;
import com.hulian.oa.work.activity.task.l_fragment.CompletedTaskFragment;
import com.hulian.oa.work.activity.task.l_fragment.CopymeTaskFragment;
import com.hulian.oa.work.activity.task.l_fragment.LaunchTaskFragment;
import com.hulian.oa.work.activity.task.l_fragment.UndoneTaskFragment;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

//发起任务
public class TaskLauncherActivity extends BaseActivity {
    //任务标题
    @BindView(R.id.et_title)
    TextView et_title;
    //任务详情
    @BindView(R.id.et_task_details)
    TextView et_task_details;
    //执行人
    @BindView(R.id.rl_opreator)
    RelativeLayout rl_opreator;
    //截止时间
    @BindView(R.id.rl_deadline)
    RelativeLayout rl_deadline;
    // 截止时间name
    @BindView(R.id.tv_deadline)
    TextView tv_deadline;
    //抄送人
    @BindView(R.id.rl_copyperson)
    RelativeLayout rl_copyperson;
    //提醒
    @BindView(R.id.rl_remind)
    RelativeLayout rl_remind;
    // 提醒name
    @BindView(R.id.tv_remind)
    TextView tv_remind;
    // 執行人nmae
    @BindView(R.id.tv_opreator)
    TextView tv_opreator;
    //发起按钮
    @BindView(R.id.tv_back_instruct)
    TextView tv_back_instruct;
    // 抄送人name
    @BindView(R.id.tv_copyperson)
    TextView tv_copyperson;
    @BindView(R.id.iv_back)
    RelativeLayout iv_back;
    private Context mContext;
    private List<People> selectList2 = new ArrayList<>();
    // 标题、任务详情、执行人、截止时间、抄送人、提醒
    private String title;
    private String context;
    private String executor;
    private String deadline;
    private String copier = "";
    private String remind = "不提醒";
    private String startTime = "";
    private int maxSelectNum = 9;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    @BindView(R.id.iimmgg)
    ImageView iimmgg;
    MeetGridViewAdapter adapter;
    @BindView(R.id.gv_test)
    NonScrollGridView gvTest;
    AlertDialog myDialog;
    // 选择文件
    @BindView(R.id.file_btn)
    TextView filebtn;
    private List<String> fileList = new ArrayList<>();
    //照片选择最大值
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    //已经选择图片
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter_grid;
    //提醒list
    List<String> Tasklist = new ArrayList<>();//提醒
    private OptionsPickerView reasonPicker;
    private String taskson = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_task_launcher);
        startTime = TimeUtils.getNowTime1();
        myDialog = new AlertDialog(this).builder();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        init();
    }

    @OnClick({R.id.rl_remind,R.id.rl_deadline,R.id.rl_opreator,R.id.iimmgg,R.id.tv_back_instruct,R.id.iv_back,R.id.iv,R.id.file_btn})
    public void onViewClicked(View view) {
        title = et_title.getText().toString().trim();
        context = et_task_details.getText().toString().trim();
        deadline = tv_deadline.getText().toString().trim();
        remind = tv_remind.getText().toString().trim();
        switch (view.getId()){
            case R.id.rl_remind:
               // startActivity(new Intent(mContext, L_TaskRemindActivity.class));
                Tasklist.clear();
                initReason();
                reasonPicker.show();
                break;
            case R.id.rl_deadline:
                selectTime(tv_deadline);
                break;
            case R.id.rl_opreator:
                Intent intent2 = new Intent(this, SelDepartmentActivity_Task_meet.class);
                intent2.putExtra("appId",SPUtils.get(mContext, "userId", "").toString());
                startActivityForResult(intent2,0);

                break;
            case R.id.iimmgg:
                if (executor==null||executor.isEmpty())
                {
                    Toast.makeText(TaskLauncherActivity.this,"请选择执行人",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(this, ExpenseApplyForPeopleActivityS.class);
                intent1.putExtra("appId",executor + "," + SPUtils.get(mContext, "userId", "").toString());
                startActivityForResult(intent1,110);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_back_instruct:
                if (title.isEmpty()) {
                  Toast.makeText(TaskLauncherActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
                  return;
                }
                else if (context.isEmpty()) {
                    Toast.makeText(TaskLauncherActivity.this,"请输入任务详情",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (executor==null||executor.isEmpty()) {
                    Toast.makeText(TaskLauncherActivity.this,"请选择执行人",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (executor==null||deadline.isEmpty()) {
                    Toast.makeText(TaskLauncherActivity.this,"请选择截止时间",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getData();
                }
                break;
            case R.id.iv:
                fl_content.setVisibility(View.GONE);
                break;
            case R.id.file_btn:
                filebtn.setCursorVisible(false);
                //文件选择器  过滤条件在 DefaultSelectorActivity.onPermissionSuccess 中添加
                DefaultSelectorActivity.startActivityForResult(this, false, true, 3);
                break;
        }

    }

    private void selectTime(TextView textView) {
        TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //判断选择开始时间是否大于当前时间
                if(TimeUtils.timeCompare(TimeUtils.getNowTime1(),getTime(date))==1){
                    ToastHelper.showToast(mContext, "请选择当前时间之后");
                }else {
                    //设置时间
                    textView.setText(getTime(date));
                }
            }
        }).setType(new boolean[]{true,true,true,true,true,false})
                .setLabel("年","月","日","时","分","秒")
                .build();
        pvTime.show();
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
    // 执行人
    public void onEventMainThread(  List<People>  event) {
        selectList2 .addAll(event);
        String uids="";
        String uname="";
        for(People params1:selectList2) {
            uids+=params1.getUserId()+",";
            uname+=params1.getUserName()+",";
        }
        executor = uids.substring(0,uids.length()-1);
    }
    //　提醒
    public void onEvent(MessageEvent_x event){
        tv_remind.setText(event.message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        loadDialog.show();
        RequestParams params = new RequestParams();
        params.put("title",title);
        params.put("details",context);
        params.put("executor",executor);
        params.put("startTime",startTime);
        params.put("endTime",deadline);
        params.put("taskReminder",remind);
        params.put("copier",copier);
        params.put("createBy", SPUtils.get(TaskLauncherActivity.this, "userId", "").toString());
        List<File> fiels = new ArrayList<>();
        for (LocalMedia imgurl : selectList) {
            fiels.add(new File(imgurl.getPath()));
        }
        for (String fileurl : fileList) {
            fiels.add(new File(fileurl));
        }
        HttpRequest.post_CoordinationRelease_add(params,fiels, new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                loadDialog.dismiss();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject obj = new JSONObject(result.toString());
                    String code = obj.getString("code");
                    String msg = obj.getString("msg");
                    if (code.equals("0"))
                    {
                        Toast.makeText(TaskLauncherActivity.this,msg,Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new LaunchTaskFragment());
                        EventBus.getDefault().post(new UndoneTaskFragment());
                        EventBus.getDefault().post(new CompletedTaskFragment());
                        EventBus.getDefault().post(new CopymeTaskFragment());
                        finish();
                    }
                    else
                    {
                        Toast.makeText(TaskLauncherActivity.this,msg,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(OkHttpException failuer) {
                loadDialog.dismiss();
                Toast.makeText(mContext, "请求失败=" + failuer.getEmsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //照片
    private void init() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(TaskLauncherActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter_grid = new GridImageAdapter(TaskLauncherActivity.this, onAddPicClickListener);
        adapter_grid.setList(selectList);
        adapter_grid.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter_grid);
        adapter_grid.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PictureSelector.create(TaskLauncherActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && requestCode == 0&&data!=null) {
            List<People> mList = (List<People>) data.getSerializableExtra("mList");
            if (mList.size() > 0) {
                String name = "";
                executor = "";
                for (People params1 : mList) {
                    executor += params1.getUserId() + ",";
                    name += params1.getUserName() + ",";
                }
                if(!executor.equals("")) executor = executor.substring(0, executor.length() - 1);
                adapter = new MeetGridViewAdapter(TaskLauncherActivity.this, mList);
                gvTest.setAdapter(adapter);
                List<People> finalMList = mList;
                gvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        myDialog.setGone().setTitle("提示").setMsg("确定删除么").setNegativeButton("取消", null).setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalMList.remove(position);
                                adapter.notifyDataSetChanged();
                                executor = "";
                                for (People params1 : mList) {
                                    executor += params1.getUserId() + ",";
                                }
                                if(!executor.equals("")) executor = executor.substring(0, executor.length() - 1);
                            }
                        }).show();
                    }
                });
            }
        }
        if (requestCode == 110&&data!=null) {
            List<People> mList = (List<People>) data.getSerializableExtra("mList");
            fl_content.setVisibility(View.VISIBLE);
            tv_copyperson.setText(mList.get(0).getUserName());
            copier = mList.get(0).getUserId();
            Log.e("ID",copier);
        }
        switch (requestCode){
            case PictureConfig.CHOOSE_REQUEST:
                // 图片选择结果回调
                selectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                for (LocalMedia media : selectList) {
                    Log.i("图片-----》", new File(media.getPath()).length() + "");
                    Log.i("压缩图片-----》", new File(media.getCompressPath()).length() + "");
                    adapter_grid.setList(selectList);
                    adapter_grid.notifyDataSetChanged();
                }
                break;
            case DefaultSelectorActivity.FILE_SELECT_REQUEST_CODE:
                printData(DefaultSelectorActivity.getDataFromIntent(data));
                break;
        }

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            initSelectImage();
        }
    };

    private void initSelectImage() {
        PictureSelector.create(TaskLauncherActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void printData(ArrayList<String> list) {
        if (FileSelectorUtils.isEmpty(list)) {
            return;
        }
        int size = list.size();
        Log.v("EMAIL", "获取到数据-开始 size = " + size);
        StringBuffer stringBuffer = new StringBuffer(" ");
        for (int i = 0; i < size; i++) {
            int a = list.get(i).length();
            String ccc = list.get(i).substring(list.get(i).lastIndexOf('/') + 1, a);
            stringBuffer.append(ccc);
            stringBuffer.append(",");
        }
        String file_name = stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1);
        filebtn.setText(file_name);
        fileList = list;
        Log.v("EMAIL", "获取到数据-结束");
    }

    private void initReason() {
        Tasklist.add("不提醒");
        Tasklist.add("截止前5分钟");
        Tasklist.add("截止前30分钟");
        Tasklist.add("截止前一个小时");
        reasonPicker = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                taskson = Tasklist.get(options1);
                tv_remind.setText(taskson);
            }
        }).setTitleText("任务提醒").setContentTextSize(22).setTitleSize(22).setSubCalSize(21).build();
        reasonPicker.setPicker(Tasklist);
    }
}