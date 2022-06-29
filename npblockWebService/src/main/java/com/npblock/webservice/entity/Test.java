package com.npblock.webservice.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name="Test")
public class Test {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
}