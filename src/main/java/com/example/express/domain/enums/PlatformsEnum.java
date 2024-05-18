package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * 下单平台枚举
 * @date 2019年04月16日 23:21
 */
@Getter
public enum PlatformsEnum implements IEnum<Integer>{
    /**
     * 淘宝
     */
    TAOBAO( "淘宝",1),

    /**
     * 闲鱼
     */
    XIANYU("闲鱼",2),

    /**
     * 小红书
     */
    XIAOHONGSHU("小红书",3),

    /**
     * 微信
     */
    WECHAT("微信",4),

    /**
     * 拼多多
     */
    PDD("拼多多",5);

    private final String code;
    private final Integer value;

    PlatformsEnum(String code, Integer value) {
        this.code = code;
        this.value = value;
    }

}



