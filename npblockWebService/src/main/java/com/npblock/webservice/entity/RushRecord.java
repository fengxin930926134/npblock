package com.npblock.webservice.entity;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="rush_record")
public class RushRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer maxPass;
    private Integer score;
    @LastModifiedDate
    private Date updateTime;

    public static RushRecord getDefaultRushRecord() {
        RushRecord rushRecord = new RushRecord();
        rushRecord.setMaxPass(1);
        rushRecord.setScore(0);
        return rushRecord;
    }
}
