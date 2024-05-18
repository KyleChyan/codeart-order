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
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("od_head")
public class Head implements Serializable {

  /**
   headId：headId
   */
  @TableId(value = "head_id")
  private Long headId;

  /**
   typeName：娃头名称
   */
  private String typeName;

  /**
   fen：几分娃头
   */
  private String fen;

//  /**
//   skin：肤色（已弃用）
//   */
//  private String skin;

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

  public Head(String typeName, String fen) {
    this.typeName=typeName;
    this.fen=fen;
  }

  public Head(Long headId, String typeName, String fen, LocalDateTime createTime, String remark) {
    this.headId = headId;
    this.typeName = typeName;
    this.fen = fen;
    this.createTime = createTime;
    this.remark = remark;
  }

  public Head() {
  }
}
