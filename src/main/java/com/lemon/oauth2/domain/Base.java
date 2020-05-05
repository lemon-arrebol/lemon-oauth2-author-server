package com.lemon.oauth2.domain;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author houjuntao
 * @description
 * @date 2020-05-05 21:34
 */
@Data
@MappedSuperclass
public class Base implements Serializable {

    private static final long serialVersionUID = -7519418012137093264L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * 添加时间
     */
    protected Long createdTime;


    /**
     * 描述
     */
    protected String description;

}
