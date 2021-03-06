package com.hulian.oa.work.activity.expense.l_adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hulian.oa.R;
import com.hulian.oa.bean.Expense;
import com.hulian.oa.work.activity.expense.ExpenseApplyResultActivity;
import com.hulian.oa.work.activity.expense.ExpenseExamineActivityS;

import java.util.ArrayList;
import java.util.List;

public class L_ExpenseApplyLaunchAdapter extends RecyclerView.Adapter <L_ExpenseApplyLaunchAdapter.ViewHolder>{
    private Context mContext;
    private List<Expense> dataList = new ArrayList<>();
    public void addAllData(List<Expense> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.dataList.clear();
    }

    public L_ExpenseApplyLaunchAdapter(Context context) {
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //报销标题
        public TextView tv_title;
        //报销时间
        public TextView tv_time;
        //报销金额
        public TextView tv_expense_monkey;
        //审批状态
        public TextView tv_state;
        public TextView tv_dpname;

        //已审批
        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_expense_monkey = itemView.findViewById(R.id.tv_expense_monkey);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_dpname = itemView.findViewById(R.id.tv_dpname);
        }
    }

    @Override
    public L_ExpenseApplyLaunchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apply_launch_expense, parent, false);
        return new L_ExpenseApplyLaunchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(L_ExpenseApplyLaunchAdapter.ViewHolder holder, final int position) {
        holder.tv_title.setText(dataList.get(position).getCreateName() + "发起的报销");
        holder.tv_time.setText(dataList.get(position).getCreateTime().split(" ")[0]+"");
        holder.tv_expense_monkey.setText(dataList.get(position).getMoney()+"元");
        holder.tv_dpname.setText(dataList.get(position).getDeptName()+"");

        if(dataList.get(position).getState().equals("0")){
            holder.tv_state.setText("待审批");
            holder.tv_state.setTextColor(ContextCompat.getColor(mContext,R.color.bg_yellow_a));
            holder.tv_state.setBackground(ContextCompat.getDrawable(mContext,R.drawable.baoxiao_state_yellow));
        }
        else if (dataList.get(position).getState().equals("1")){
            holder.tv_state.setText("已审批");
            holder.tv_state.setTextColor(ContextCompat.getColor(mContext,R.color.bg_blue_a));
            holder.tv_state.setBackground(ContextCompat.getDrawable(mContext,R.drawable.baoxiao_state_blue));
        }
        else if (dataList.get(position).getState().equals("2")){
            holder.tv_state.setText("已驳回");
            holder.tv_state.setTextColor(ContextCompat.getColor(mContext,R.color.bg_red_a));
            holder.tv_state.setBackground(ContextCompat.getDrawable(mContext,R.drawable.baoxiao_state_red));
        }else if (dataList.get(position).getState().equals("3")){
            holder.tv_state.setText("审批中");
            holder.tv_state.setTextColor(ContextCompat.getColor(mContext,R.color.bg_yellow_a));
            holder.tv_state.setBackground(ContextCompat.getDrawable(mContext,R.drawable.baoxiao_state_yellow));
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
