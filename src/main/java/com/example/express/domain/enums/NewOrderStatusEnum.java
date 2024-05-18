package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 订单状态枚举
 * @date 2019年04月16日 23:21
 */
@Getter
public enum NewOrderStatusEnum implements IEnum<Integer> {
    /**
     * 未收到货
     */
    READY_RECEIVE("未收到货", 0),
    /**
     * 准备开工
     */
    READY_TO_START("准备开工", 1),

    /**
     * 施工中
     */
    ABUILDING("施工中", 2),

    /**
     * 已超时
     */
    TIME_OUT("已超时", 3),

    /**
     * 已发货
     */
    DELIVERED("已发货", 4),

    /**
     * 订单完成
     */
    COMPLETE("订单完成", 5),

    /**
     * 退货中
     */
    RETURNS("退货中", 8),

    /**
     *  订单异常
     */
    ERROR("订单异常", 9);

    private final String code;
    private final Integer value;

    NewOrderStatusEnum(String code, int value) {
        this.code = code;
        this.value = value;
    }

    public static NewOrderStatusEnum getByStatus(Integer status) {
        return Arrays.stream(values()).filter(e -> e.getValue() == status).findFirst().orElse(null);
    }

    public Integer getCode() {
        return this.value;
    }
}
