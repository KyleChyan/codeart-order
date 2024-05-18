package com.example.express.domain.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author Kyle
 * @Date 2024/4/6 16:30
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchReq {
    /**
     orderId：订单号
     */
    private String orderId;
    /**
     clientNickname：客户昵称
     */
    private String clientNickname;
    /**
     orderStatus：订单状态
     */
    private Integer orderStatus;

    /**
     totalPriceStart：订单总价起始
     */
    private double totalPriceStart;
    /**
     totalPriceEnd：订单总价区间
     */
    private double totalPriceEnd;
    /**
    receivePostNumber：收货单号
     */
    private String receivePostNumber;
    /**
     deliverPostNumber：发货单号
     */
    private String deliverPostNumber;
    /**
     deadlineTimeStart：交付时间起始
     */
    private Date deadlineTimeStart;
    /**
     deadlineTimeEnd：交付时间区间
     */
    private Date deadlineTimeEnd;
}
