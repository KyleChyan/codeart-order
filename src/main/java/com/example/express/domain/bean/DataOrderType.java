package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单类型数据
 * @author xiangsheng.wu
 * @date 2019年04月24日 14:52
 */
@Data
public class DataOrderType implements Serializable {
    /**
     orderId：订单号
     */
    @TableId(value = "type_id")
    private Integer typeId;

    /**
     typeName：订单类型名称
     */
    private String typeName;

    /**
     deposit：定金
     */
    private double deposit;

    /**
     finalPayment：尾款
     */
    private double finalPayment;

    /**
     remark：备注
     */
    private String remark;
}
