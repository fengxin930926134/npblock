package com.np.block.core.model;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 关卡实体类
 *
 * @author fengxin
 */
@Getter
@Setter
@ToString
public class Stage extends LitePalSupport {
    /**关卡类型*/
    private String stageType;
    /**关卡名*/
    private String name;
    /**关卡图标路径*/
    private String icoPath;
    /**阻碍用的俄罗斯方块*/
    private List<Tetris> hinderTetris = new ArrayList<>();
    /**消除行数（过关条件）*/
    private Integer complete;
    /**下落速度*/
    private Integer speed;

    /**
     * 连缀查询查询关联表数据
     *
     * @return 所有的阻碍俄罗斯方块
     */
    public List<Tetris> getAllTetris() {
        return LitePal.where("stage_id = ?", String.valueOf(this.getBaseObjId())).find(Tetris.class);
    }

}
