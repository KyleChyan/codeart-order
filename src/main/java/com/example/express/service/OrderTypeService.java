package com.example.express.service;



import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Head;
import com.example.express.domain.bean.OrderType;

import java.util.List;

public interface OrderTypeService {
    /**
     * 根据id获取娃头
     *
     * @param typeId
     * @return
     */
    ResponseResult getOrderTypeDetailById(Long typeId);



}
