package com.hulian.oa.work.activity.expense.l_adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hulian.oa.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseApproverAdapter extends RecyclerView.Adapter<ExpenseApproverAdapter.ViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<String> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    private int mPosition;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;
    private OnItemDeleteListener onItemDeleteListener;

    public interface onAddPicClickListener {
        void onAddPicClick(int position,View view);
    }

    public ExpenseApproverAdapter(Context context, onAddPicClickListener mOnAddPicClickListener, OnItemDeleteListener onItemDeleteListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
        this.onItemDeleteListener = onItemDeleteListener;

    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView iv_image;
        LinearLayout ll_del;
        TextView tv_duration;
        TextView tv_name;
        public ViewHolder(View view) {
            super(view);
            mImg =  view.findViewById(R.id.fiv);
            ll_del =  view.findViewById(R.id.ll_del);
            tv_duration =  view.findViewById(R.id.tv_duration);
            tv_name = view.findViewById(R.id.tv_name);
            iv_image = view.findViewById(R.id.iv_image);
        }
    }

    @Override
    public int getItemCount() {
            return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_baoxiao_approver,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        //少于8张，显示继续添加的图标
//        if (getItemViewType(position) == TYPE_CAMERA) {
//            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.addimg);
//            viewHolder.mImg.setBackground(drawable);
//            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnAddPicClickListener.onAddPicClick(position,v);
//                }
//            });
////            viewHolder.ll_del.setVisibility(View.INVISIBLE);
//        } else {
//            viewHolder.ll_del.setVisibility(View.VISIBLE);
//            viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int index = viewHolder.getAdapterPosition();
//                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
//                    // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
//                    if (index != RecyclerView.NO_POSITION) {
//                        list.remove(index);
//                        onItemDeleteListener.onItemDelete(position,view);
//                        notifyItemRemoved(index);
//                        notifyItemRangeChanged(index, list.size());
//                    }
//                }
//            });
            if (position>0){
                viewHolder.iv_image.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.iv_image.setVisibility(View.GONE);
            }
            viewHolder.tv_name.setText(list.get(position));

//        }
    }
    public  void setPosition(int mPosition){
        this.mPosition = mPosition;
    }
    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
    protected OnItemDeleteListener mItemDeleteListener;

    public interface OnItemDeleteListener {
        void onItemDelete(int position,View v);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.mItemDeleteListener = listener;
    }
}
