package com.hulian.oa.L_launched.L_Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hulian.oa.L_launched.L_Bean.L_MailBean;
import com.hulian.oa.L_launched.L_Bean.L_MailBean_details;
import com.hulian.oa.R;
import com.hulian.oa.bean.SecondMail_bean_x;

import java.util.ArrayList;
import java.util.List;

public class L_MailAdapter_x extends BaseAdapter
{
    private List<L_MailBean_details.DataBean.FileListBean> listText = new ArrayList<>();
    private Context context;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    public L_MailAdapter_x(List<L_MailBean_details.DataBean.FileListBean> listText, Context context) {
        this.listText = listText;
        this.context = context;
    }

    @Override
    public int getCount() {
        //return返回的是int类型，也就是页面要显示的数量。
        return listText.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            //通过一个打气筒 inflate 可以把一个布局转换成一个view对象
            view = View.inflate(context, R.layout.work_mail_par_listview_item, null);
        } else {
            view = convertView;//复用历史缓存对象
        }

        TextView radioText = (TextView) view.findViewById(R.id.tv_file_name_x);
        radioText.setText(listText.get(position).getFileName());
        return view;
    }


}
