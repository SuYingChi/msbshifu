package com.msht.master.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msht.master.FunctionView.PriceTable;
import com.msht.master.Model.PriceCategoryModel;
import com.msht.master.R;

import java.util.ArrayList;

/**
 * Created by hong on 2017/4/20.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {
    private ArrayList<PriceCategoryModel.PriceCategory> data;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemClickTypeListener listener;
    public interface OnItemClickTypeListener {
        void setOnItemClick(View view, int childPosition,PriceCategoryModel.PriceCategory.ChildCategory model);
    }
    public void SetOnItemClickListener( OnItemClickTypeListener listener) {
        this.listener = listener;
    }
    public MyExpandableAdapter(Context context, ArrayList<PriceCategoryModel.PriceCategory> datas) {
        this.data=datas;
        inflater = LayoutInflater.from(context);
        this.mContext=context;
    }

    @Override
    public int getGroupCount() {
        if (data==null){
            return 0;
        }else {
            return data.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (data==null){
            return 0;
        }else {
            return data.get(groupPosition).child.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (data==null){
            return null;
        }else {
            return data.get(groupPosition);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (data==null){
            return null;
        }else {
            return data.get(groupPosition).child.get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup viewHolder;
        if(convertView==null){
            //初始化控件管理器对象
            viewHolder = new ViewHolderGroup();
            //重新加载布局
            convertView = inflater.inflate(R.layout.item_price_table, null);
            //给组元素绑定ID
            viewHolder.tv_maintype = (TextView) convertView.findViewById(R.id.id_view_title);
            //给组元素箭头绑定ID
            viewHolder.img_arrow= (ImageView) convertView.findViewById(R.id.id_right_icon);
            //convertView将viewHolder设置到Tag中，以便再次绘制ExpandableListView时从Tag中取出viewHolder;
            convertView.setTag(viewHolder);
        }else {//如果convertView不为空，即getScrapView得到废弃已缓存的view
            //从Tag中取出之前存入的viewHolder
            viewHolder = (ViewHolderGroup) convertView.getTag();
        }
        //设置组值
        viewHolder.tv_maintype.setText(data.get(groupPosition).name);
        //如果组是展开状态
        if (isExpanded) {
            //箭头向下
            viewHolder.img_arrow.setImageResource(R.drawable.forward);
        }else{//如果组是伸缩状态
            //箭头向右
            viewHolder.img_arrow.setImageResource(R.drawable.downward);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild viewHolderChild;
        /*当convertView为空，也就是没有废弃已缓存 view时，将执行下面方法，调用layoutinflate的
         * inflate()方法来加载一个view。
         * 如有不懂，请点击：http://blog.csdn.net/libmill/article/details/49644743
         */
        if(convertView==null){
            //重新加载布局
            convertView = inflater.inflate(R.layout.item_price_type, null);
            //初始化控件管理器（自己命名的）
            viewHolderChild = new ViewHolderChild();
            //绑定控件id
            viewHolderChild.RselectItem=(RelativeLayout) convertView.findViewById(R.id.id_select_item);
            viewHolderChild.tv_childtype = (TextView) convertView.findViewById(R.id.id_second_type);
            /*convertView的setTag将viewHolderChild设置到Tag中，以便系统第二次绘制
                ExpandableListView时从Tag中取出
            */
            convertView.setTag(viewHolderChild);
        }else{
            //当convertView不为空时，从Tag中取出viewHolderChild
            viewHolderChild = (ViewHolderChild) convertView.getTag();
        }
        //给子元素的TextView设置值
        viewHolderChild.tv_childtype.setText(data.get(groupPosition).child.get(childPosition).name);
        //返回视图对象，这里是childPostion处的视图
        viewHolderChild.RselectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceCategoryModel.PriceCategory.ChildCategory model=data.get(groupPosition).child.get(childPosition);
                if(listener!=null){
                    listener.setOnItemClick(v,childPosition,model);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewHolderGroup{
        TextView tv_maintype;
        ImageView img_arrow;
    }
    class ViewHolderChild{
        TextView tv_childtype;
        RelativeLayout RselectItem;
    }

}
