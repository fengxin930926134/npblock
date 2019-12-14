package com.np.block.core.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 测试实体类
 *
 * @author fengxin
 */

@Getter
@Setter
@ToString
public class Test extends LitePalSupport {
    @Column(unique = true, defaultValue = "这是个默认值")
    private String name;
}
