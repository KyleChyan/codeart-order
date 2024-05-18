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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("od_type_order")
public class TypeOrder implements Serializable {

  /**
    typeId：id
  */
  @TableId(value = "typeId")
  private Integer typeId;

  /**
    typeName：类型名称
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
    createTime：创建时间
  */
  @TableField(fill = FieldFill.INSERT)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;

  /**
    remark：备注
  */
  private String remark;

}
