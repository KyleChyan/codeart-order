package com.example.express.domain.bean;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("od_order_item")
public class OrderItem {

  /*
    id：订单物品id
  */
  @TableId(value = "id",type = IdType.ID_WORKER_STR)
  private Integer id;
  /*
    orderId：订单号
  */
  private String orderId;
  /**
    orderTypeId：订单类型
  */
  private Integer orderTypeId;
  /**
    headId：娃头id
  */
  private Integer headId;
  /**
    receivePostNumber：收货单号
  */
  private String receivePostNumber;
  /**
    orderPlatformId：下单平台
  */
  private Integer orderPlatformId;
  /**
    headName：娃头名称
  */
  private String headName;
  /**
    headFen：几分娃头
  */
  private String headFen;
  /**
    headCount：娃头数量
  */
  private Integer headCount;
  /**
    orderDemand：订单需求
  */
  private String orderDemand;
  /**
    orderPrice：此子订单价格
  */
  private double orderPrice;
  /**
    extraPrice：此子订单额外价格
  */
  private double extraPrice;
  /**
    createTime：创建时间
  */
  @TableField(fill = FieldFill.INSERT)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;
  /**
    modifyTime：修改时间
  */
  @TableField(fill = FieldFill.UPDATE)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date modifyTime;


}
