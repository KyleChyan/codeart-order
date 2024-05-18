package com.example.express.service;


import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.TypeOrder;
import com.example.express.domain.vo.req.TypeOrderVO;

import java.util.List;

public interface TypeOrderService {
    /**
     * 根据id获取订单类型
     *
     * @param typeOrderId
     * @return
     */
    TypeOrder getTypeOrderDetailById(Integer typeOrderId);

    /**
     * 新增订单类型
     *
     * @param typeOrder
     * @return
     */
    ResponseResult insertTypeOrder(TypeOrder typeOrder);

    /**
     * 查找订单类型列表
     *
     * @param typeOrderVO
     * @return
     */
    List<TypeOrder> getTypeOrderList(TypeOrderVO typeOrderVO);

    /**
     * 更新订单类型信息
     *
     * @param typeOrder
     * @return
     */
    ResponseResult updateTypeOrderDetail(TypeOrder typeOrder);

    /**
     * 删除订单类型信息（物理删除）
     *
     * @param typeOrderId
     * @return
     */
    ResponseResult delectTypeOrder(Integer typeOrderId);

}
