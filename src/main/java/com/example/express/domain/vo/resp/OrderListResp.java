package com.example.express.domain.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author Kyle
 * @Date 2024/4/6 16:26
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResp {
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
     totalPrice：订单总价
     */
    private double totalPrice;
    /**
     deadlineTime：交付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date deadlineTime;
    /**
     modifyTime：修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date modifyTime;

    /**
     剩余工期
     */
    private Integer remainTime;

    /**
     时间状态 1-剩余超过8天 2-剩余3~7天 3-剩余不足3天 0-已超时
     */
    private Integer timeStatus;

    public void initResp(){
        Date nowdate = new Date();
        this.remainTime=nowdate.getDay()-this.getDeadlineTime().getDay();
        this.timeStatus=setTimeStatus(this.remainTime);
    }

    public Integer setTimeStatus(Integer remainTime){
        Integer timeStatus =0;
        if (remainTime <=15 && remainTime >8 ) {
            timeStatus=1;
        } else if (remainTime <= 8 && remainTime > 3) {
            timeStatus=2;
        }else if (remainTime <= 3 && remainTime > 0) {
            timeStatus=3;
        }else
            timeStatus=0;
        return timeStatus;
    }
}
