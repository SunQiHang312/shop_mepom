package com.qf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCart implements Serializable{

    private int id;
    private int gid;
    private int uid;
    private int gnumber;
    private BigDecimal allprice;
}
