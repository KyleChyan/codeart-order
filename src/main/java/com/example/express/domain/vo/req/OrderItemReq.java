package com.example.express.domain.vo.req;

import lombok.Data;

/**
 * @Author Kyle
 * @Date 2024/4/16 17:41
 * @Version 1.0
 */
@Data
public class OrderItemReq {
    /**
     typeId：订单类型
     */
    private Integer typeId;
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
     extraPrice：此子订单额外价格
     */
    private double extraPrice;
    /**
     orderDemand：订单需求
     */
    private String remark;
}
