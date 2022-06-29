package com.npblock.webservice.entity;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="classic_record")
public class ClassicRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private Integer maxScore;
    /**上传次数*/
    private Integer uploadCount;
    @LastModifiedDate
    private Date updateTime;

    public static ClassicRecord getDefaultClassicRecord() {
        ClassicRecord classicRecord = new ClassicRecord();
        classicRecord.setMaxScore(0);
        classicRecord.setUploadCount(1);
        return classicRecord;
    }
}
