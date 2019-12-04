package com.np.block.core.model;

import lombok.Data;
import java.util.Date;

@Data

public class Users {
    private Integer id;
    private String name;
    private String phone;
    private String password;
    private String openId;
    private String gameName;
    private String token;
    private Long tokenTime;
    private Date createDate;
    private String headSculpture;
    private Integer classicScore;
    private Integer rankScore;
    /**
     * 1.男 2.女
     */
    private Integer sex;
}
