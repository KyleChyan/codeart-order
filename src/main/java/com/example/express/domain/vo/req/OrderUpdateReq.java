package com.example.express.domain.vo.req;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Kyle
 * @Date 2024/4/6 16:30
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateReq {
    /**
     orderId：订单号
     */
    private String orderId;

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
     count：订单数量
     */
    private Integer count;

    /**
       receivePostNumber：收货单号
     */
    private String receivePostNumber;

    /**
     deliverPostNumber：发货单号
     */
    private String deliverPostNumber;

    /**
       orderDemand：订单需求
     */
    private String orderDemand;

    /**
      remark：备注
     */
    private String remark;


}
