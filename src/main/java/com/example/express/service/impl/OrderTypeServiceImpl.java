package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Head;
import com.example.express.domain.bean.Order;
import com.example.express.domain.bean.OrderType;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.mapper.HeadMapper;
import com.example.express.mapper.OrderMapper;
import com.example.express.mapper.OrderTypeMapper;
import com.example.express.service.HeadService;
import com.example.express.service.OrderTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Kyle
 * @Date 2024/4/1 23:13
 * @Version 1.0
 */

@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, OrderType> implements OrderTypeService {

    @Resource
    private OrderTypeMapper orderTypeMapper;
    @Override
    public ResponseResult getOrderTypeDetailById(Long typeId) {
        try{
            OrderType orderType = orderTypeMapper.selectById(typeId);
            if (orderType == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_FAILED);
            }

            return ResponseResult.success(orderType);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
