package com.hulian.oa.work.activity.expense.l_adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hulian.oa.R;
import com.hulian.oa.bean.Expense;
import com.hulian.oa.work.activity.expense.ExpenseExamineActivityS;

import java.util.ArrayList;
import java.util.List;

public class L_ExpenseApprovedAdapter extends RecyclerView.Adapter <L_ExpenseApprovedAdapter.ViewHolder>{
    private Context mContext;
    private List<Expense> dataList = new ArrayList<>();

    public void addAllData(List<Expense> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.dataList.clear();
    }

    public L_ExpenseApprovedAdapter(Context context) {
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //报销标题
        public TextView tv_title;
        //报销时间
        public TextView tv_time;
        //报销金额
        public TextView tv_expense_monkey;
        //已审批
        public ImageView tv_pend;
        public TextView tv_expense_dept;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_title =  itemView.findViewById(R.id.tv_title);
            tv_time =  itemView.findViewById(R.id.tv_time);
            tv_expense_monkey =  itemView.findViewById(R.id.tv_expense_monkey);
            tv_pend = itemView.findViewById(R.id.tv_pend);
            tv_expense_dept =  itemView.findViewById(R.id.tv_expense_dept);
        }
    }

    @Override
    public L_ExpenseApprovedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.l_item_apply_launch_expense, parent, false);

        return new L_ExpenseApprovedAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(L_ExpenseApprovedAdapter.ViewHolder holder, final int position) {
        holder.tv_title.setText(dataList.get(position).getCreateName() + "发起的报销");
        holder.tv_time.setText(dataList.get(position).getCreateTime().split(" ")[0]);
        holder.tv_expense_monkey.setText(dataList.get(position).getMoney()+"元");
        holder.tv_expense_dept.setText(dataList.get(position).getDeptName()+"");
        if(dataList.get(position).getState().equals("0")){
            holder.tv_pend.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.qj_daishenpi_icon_qgl));

        }
        else if (dataList.get(position).getState().equals("1")){
            holder.tv_pend.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.qj_shenpi_tongguo_icon_qgl));

        }
        else if (dataList.get(position).getState().equals("2")){
            holder.tv_pend.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.qj_bohui_icon_qgl));
        }else if (dataList.get(position).getState().equals("3")){
            holder.tv_pend.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.qj_shenpizhong_icon_qgl));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("id",dataList.get(position).getId());
                intent.setClass(mContext, ExpenseExamineActivityS.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
