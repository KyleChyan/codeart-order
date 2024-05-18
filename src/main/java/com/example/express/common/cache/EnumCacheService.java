package com.example.express.common.cache;


import com.baomidou.mybatisplus.core.enums.IEnum;
import com.example.express.domain.enums.NewOrderStatusEnum;
import com.example.express.domain.enums.OrderTypeEnum;
import com.example.express.domain.enums.PlatformsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 下单平台枚举类
 * key: 用户ID
 */

@Component
public class EnumCacheService {

    private static final String PLATFORMS_ENUM_CACHE_KEY = "platforms_enum";
    private static final String NEW_ORDER_STATUS_ENUM_CACHE_KEY = "new_order_status_enum";
    private static final String ORDER_TYPE_ENUM_CACHE_KEY = "order_type_enum";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public EnumCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void cacheEnums() {
        cacheEnumValues(PLATFORMS_ENUM_CACHE_KEY, PlatformsEnum.values());
        cacheEnumValues(NEW_ORDER_STATUS_ENUM_CACHE_KEY, NewOrderStatusEnum.values());
        cacheEnumValues(ORDER_TYPE_ENUM_CACHE_KEY, OrderTypeEnum.values());
    }

    private void cacheEnumValues(String cacheKey, Enum<?>[] values) {
        Map<Integer, String> enumMap = new HashMap<>();
        for (Enum<?> value : values) {
            if (value instanceof IEnum) {
                IEnum<Integer> enumValue = (IEnum<Integer>) value;
                enumMap.put(enumValue.getValue(), enumValue.toString());
            }
        }
        redisTemplate.opsForHash().putAll(cacheKey, enumMap);
    }

    public String getEnumLabel(String cacheKey, Integer value) {
        return (String) redisTemplate.opsForHash().get(cacheKey, value);
    }
}
