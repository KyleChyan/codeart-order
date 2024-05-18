package com.example.express.domain.bean;


import com.baomidou.mybatisplus.annotation.*;
import com.example.express.domain.enums.NewOrderStatusEnum;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.PlatformsEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("od_order")
public class Order implements Serializable {

  /**
   orderId：订单号
  */
  @TableId(value = "order_id")
  private String orderId;

  /**
   userId：用户id
   */
  private String userId;

  /**
    clientId：客户id
  */
  private Long clientId;

  /**
   headId：娃头id
   */
  private Long headId;

  /**
   orderTypeId：订单类型
   */
  private Integer orderTypeId;

  /**
   count：娃头数量
   */
  private Integer count;

  /**
   orderPlatformId：下单平台
   */
  @TableField("platform")
  private Integer platform;

  /**
   receivePostNumber：收货单号
   */
  private String receivePostNumber;

  /**
    deliverPostNumber：发货单号
  */
  private String deliverPostNumber;

  /**
    orderStatus：订单状态
  */
  @TableField("order_status")
  private Integer orderStatus;

  /**
   orderDemand：订单需求
   */
  private String orderDemand;

  /**
    remark：备注
  */
  private String remark;

  /**
   extraPrice：此子订单额外价格
   */
  private double extraPrice;

  /**
    totalPrice：订单总价
  */
  private double totalPrice;

  /**
   urgent：加急
   */
  private Boolean urgent;

  /**
    orderDeleted：订单删除状态
  */
  private Integer orderDeleted;
  /**
    createTime：创建时间
  */
  @TableField(fill = FieldFill.INSERT)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;
  /**
    deadlineTime：交付时间
  */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime deadlineTime;
  /**
    modifyTime：修改时间
  */
  @TableField(fill = FieldFill.UPDATE)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime modifyTime;


}
