package com.example.express.domain.bean;


import com.baomidou.mybatisplus.annotation.*;
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
@TableName("od_client")
public class Client implements Serializable {

  /**
    clientId：客户id
  */
  @TableId(value = "client_id")
  private Long clientId;

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
    isDeleted：客户注销状态
  */
  private Integer isDeleted;

  /**
    createTime：客户创建时间
  */
  @TableField(fill = FieldFill.INSERT)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
    remark：备注
  */
  private String remark;

}
