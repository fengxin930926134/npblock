package com.npblock.webservice.entity;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="rank_record")
public class RankRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    /**最大排位积分*/
    private Integer maxRankScore;
    /**当前排位积分*/
    private Integer rankScore;
    /**最高排名*/
    private Integer maxRank;
    /**连败次数*/
    private Integer maxRepeatedDefeats;
    /**连胜次数*/
    private Integer maxWinRepeatedly;
    /**是否晋级赛*/
    private Boolean riseInRank;
    @LastModifiedDate
    private Date updateTime;

    public static RankRecord getDefaultRankRecord() {
        RankRecord rankRecord = new RankRecord();
        rankRecord.setRankScore(1000);
        rankRecord.setMaxRankScore(1000);
        rankRecord.setMaxRepeatedDefeats(0);
        rankRecord.setMaxWinRepeatedly(0);
        rankRecord.setRiseInRank(false);
        return rankRecord;
    }
}
