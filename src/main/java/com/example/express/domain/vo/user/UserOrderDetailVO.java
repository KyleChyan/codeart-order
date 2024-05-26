package com.example.express.domain.vo.user;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderDetailVO implements Serializable {

  /**
   orderId：订单号
  */
  @TableId(value = "order_id")
  private String orderId;

  /**
    clientId：客户id
  */
  private String clientNickname;

  /**
   deliverName：收货人名称
   */
  private String deliverName;

  /**
   deliverPhone：收货人手机号
   */
  private String deliverPhone;

  /**
   deliverAddress：收货人地址
   */
  private String deliverAddress;

  /**
   typeName：娃头名称
   */
  private String headName;

  /**
   fen：几分娃头
   */
  private String fen;

  /**
   typeId：订单类型
   */
  private String typeName;

  /**
   count：娃头数量
   */
  private Integer count;

  /**
   orderPlatformId：下单平台
   */
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
   reserve：预约单
   */
  private Boolean reserve;

  /**
   extraPrice：此子订单额外价格
   */
  private double extraPrice;

  /**
    totalPrice：订单总价
  */
  private double totalPrice;

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


}
