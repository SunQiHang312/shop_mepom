package com.qf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Date;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email implements Serializable {
    //标题
    private String subject;
    //发送方
    private String from;
    //目标
    private String to;
    //内容
    private String content;
    //时间
    private Date createtime;

    private File file;
}
