package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.CommonDataCache;
import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.bean.DataOrderType;
import com.example.express.domain.bean.DataSchool;
import com.example.express.domain.bean.OrderType;
import com.example.express.mapper.DataCompanyMapper;
import com.example.express.mapper.DataOrderTypeMapper;
import com.example.express.service.DataCompanyService;
import com.example.express.service.DataOrderTypeService;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DataOrderTypeServiceImpl extends ServiceImpl<DataOrderTypeMapper, DataOrderType> implements DataOrderTypeService, ApplicationListener<ApplicationStartedEvent> {
    @Autowired
    private DataOrderTypeMapper dataOrderTypeMapper;
    @Autowired
    private RedisTemplate<String, DataOrderType> redisTemplate;

    @Override
    public List<DataOrderType> listAll() {
        return dataOrderTypeMapper.selectList(null);
    }

    @Override
    public List<DataOrderType> listAllByCache() {
        List<DataOrderType> list = redisTemplate.opsForList().range(RedisKeyConstant.DATA_ORDER_TYPE, 0, -1);
        if(list == null) {
            list = listAll();
        }
        return list;
    }

    @Override
    public boolean updateById(DataOrderType entity) {
        boolean update = super.updateById(entity);
        if(update) {
            redisTemplate.opsForHash().delete(RedisKeyConstant.DATA_ORDER_TYPE, entity.getTypeId());
        }

        return update;
    }

    @Override
    public DataOrderType getByCache(Integer id) {
        return CommonDataCache.dataOrderTypeCache.get(id);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("开始加载订单类型数据...");
        // 数据加载线程池
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("data-ordertype-loader"));
        executorService.scheduleWithFixedDelay(() -> {
            redisTemplate.delete(RedisKeyConstant.DATA_ORDER_TYPE);
            redisTemplate.opsForList().rightPushAll(RedisKeyConstant.DATA_ORDER_TYPE, listAll());
        }, 0, 10, TimeUnit.MINUTES);
    }
}
