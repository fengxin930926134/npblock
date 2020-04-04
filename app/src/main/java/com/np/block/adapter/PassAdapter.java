package com.np.block.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.np.block.R;
import com.np.block.core.model.Stage;
import java.util.List;

/**
 * 关卡适配器
 * @author fengxin
 */
public class PassAdapter extends BaseAdapter {

    private Context mContext;
    private List<Stage> stageList;
    private int pass;

    public PassAdapter(Context context,  List<Stage> stageList, int pass) {
        this.mContext = context;
        this.stageList = stageList;
        this.pass = pass;
    }


    @Override
    public int getCount() {
        return stageList.size();
    }

    @Override
    public Object getItem(int position) {
        return stageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_icon_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemImg = convertView.findViewById(R.id.pass_ico);
            viewHolder.itemText = convertView.findViewById(R.id.pass_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemImg.setImageResource(getResource(stageList.get(position).getIcoPath()));
        viewHolder.itemText.setText(stageList.get(position).getName());
        if (position >= pass) {
            convertView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(),
                    R.color.colorGrayTransparent_25, null));
        }
        return convertView;
    }

    /**
     * 获取某图片
     * @param imageName imageName
     * @return int
     */
    private int getResource(String imageName){
        if (imageName == null) {
            return 0;
        }
        //如果没有在"mipmap"下找到imageName,将会返回0
        return mContext.getResources().getIdentifier(imageName, "mipmap", mContext.getPackageName());
    }

    private class ViewHolder {
        ImageView itemImg;
        TextView itemText;
    }
}
