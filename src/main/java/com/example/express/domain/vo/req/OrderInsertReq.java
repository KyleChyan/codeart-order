package com.example.express.domain.vo.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.express.domain.enums.PlatformsEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Kyle
 * @Date 2024/4/6 16:30
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInsertReq {

    /**
       clientNickname：客户昵称
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
       headName：娃头名称
     */
    private String headName;

    /**
       fen：几分娃头
     */
    private String fen;

    /**
     count：娃头数量
     */
    private Integer count;

    /**
       typeId：订单类型
     */
    private Integer typeId;

    /**
       orderPlatformId：下单平台
     */
    private Integer platform;

    /**
       receivePostNumber：收货单号
     */
    private String receivePostNumber;

    /**
       orderDemand：订单需求
     */
    private String orderDemand;

    /**
      remark：备注
     */
    private String remark;

    /**
     urgent：加急
     */
    private Boolean urgent;

    /**
     extraPrice：此子订单额外价格
     */
    private double extraPrice;

    /**
     totalPrice：订单总价
     */
    private double totalPrice;

}
