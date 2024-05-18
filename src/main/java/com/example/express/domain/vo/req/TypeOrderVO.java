package com.example.express.domain.vo.req;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class TypeOrderVO {

  /**
   * typeId：id
   */
  private Integer typeId;
  /**
   * typeName：类型名称
   */
  private String typeName;
  /**
   * deposit：定金起始
   */
  private double depositStart;
  /**
   * deposit：定金区间
   */
  private double depositEnd;
  /**
   * finalPayment：尾款起始
   */
  private double finalPaymentStart;
  /**
   * finalPayment：尾款区间
   */
  private double finalPaymentEnd;
  /**
   * createTime：创建时间起始
   */
  private Date createTimeStart;
  /**
   * createTime：创建时间区间
   */
  private Date createTimeEnd;
  /**
    remark：备注
  */
  private String remark;

}
