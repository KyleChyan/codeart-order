package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("od_order_type")
public class OrderType {

  /**
   orderId：订单号
   */
  @TableId(value = "type_id")
  private Integer typeId;

  /**
   typeName：订单类型名称
   */
  private String typeName;

  /**
   deposit：定金
   */
  private double deposit;

  /**
   finalPayment：尾款
   */
  private double finalPayment;

  /**
   remark：备注
   */
  private String remark;

  /**
   createTime：创建时间
   */
  @TableField(fill = FieldFill.INSERT)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
   modifyTime：修改时间
   */
  @TableField(fill = FieldFill.UPDATE)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime modifyTime;
}
