package com.np.block.core.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import lombok.Data;

/**
 * 谈话的item
 *
 * MultiItemEntity是框架封装好的实现多item的方式
 * @author fengxin
 */
@Data
public class TalkItem implements MultiItemEntity {
    public static final int TYPE_LEFT = 1;
    public static final int TYPE_RIGHT = 2;

    private String headSculpture;
    private String content;
    private int itemType;

    public TalkItem(String headSculpture, String content, int itemType) {
        this.headSculpture = headSculpture;
        this.content = content;
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }
}
