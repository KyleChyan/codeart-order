package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * 投妆类型枚举
 * @date 2019年04月16日 23:21
 */
@Getter
public enum OrderTypeEnum implements IEnum<Integer>{
    /**
     * 免费妆
     */
    FREE("免费妆",1),

    /**
     * 半指定
     */
    HALF_DESIGN("半指定",2),

    /**
     * 全指定
     */
    FULL_DESIGN("全指定",3),

    /**
     * 卸妆
     */
    CLEANSE("卸妆",4);



    @Getter
    private final String code;
    private final Integer value;

    public Integer getValue() {
        return this.value;
    }
    OrderTypeEnum( String code,Integer value) {
        this.code = code;
        this.value = value;
    }

}



