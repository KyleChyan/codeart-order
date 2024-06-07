package com.example.express.domain.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author Kyle
 * @Date 2024/5/10 14:41
 * @Version 1.0
 */

@Data
@Builder
public class UserOrderPoolVO {
    /**
     orderId：订单号
     */
    private String orderId;
    /**
     clientNickname：客户昵称
     */
    private String clientNickname;
    /**
     orderPlatformId：下单平台
     */
    private Integer platform;
    /**
     typeId：订单类型
     */
    private Integer typeId;
    /**
     typeId：订单类型
     */
    private String typeName;
    /**
     orderStatus：订单状态
     */
    private Integer orderStatus;
    /**
     remainDays：剩余工期
     */
    private Integer remainDays;
    /**
     reserve：预约单
     */
    private Boolean reserve;
    /**
     urgent：加急
     */
    private Boolean urgent;
    /**
     totalPrice：订单总价
     */
    private double totalPrice;
    /**
     deadlineTime：交付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date deadlineTime;
    /**
     createTime：创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     modifyTime：修改（删除时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date modifyTime;
}
