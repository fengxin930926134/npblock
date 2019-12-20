package com.np.block.core.model;

import org.litepal.crud.LitePalSupport;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 关卡实体类
 *
 * @author fengxin
 */
@Getter
@Setter
public class Stage extends LitePalSupport {
    private Integer num;
    private String name;
    // 阻碍方块
    private List<UnitBlock> hinderBlocks;
    // 消除行数（过关条件）
    private Integer complete;
    // 下落速度
    private Integer speed;
}
