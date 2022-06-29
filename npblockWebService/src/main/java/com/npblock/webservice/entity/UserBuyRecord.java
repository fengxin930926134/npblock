package com.npblock.webservice.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="user_buy_record")
public class UserBuyRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private Boolean alreadyUse;
    @CreatedDate
    private Date createDate;

    public UserBuyRecord(Integer userId, Integer goodsId, Boolean alreadyUse) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.alreadyUse = alreadyUse;
    }

    public UserBuyRecord(){}
}
