package com.example.express.service;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Order;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.domain.vo.req.OrderUpdateReq;
import com.example.express.domain.vo.user.UserOrderPoolVO;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * 根据id获取订单
     *
     * @param orderId
     * @return
     */
    ResponseResult getOrderDetailById(String orderId);

    /**
     * 新增订单
     *
     * @param req
     * @return
     */
    ResponseResult insertOrder(OrderInsertReq req, String uid);

    /**
     * 新增订单
     *
     * @param req
     * @return
     */
    ResponseResult updateOrder(OrderUpdateReq req, String uid);

    /**
     * 前端page条件搜索
     *
     * @param
     * @return
     */
    BootstrapTableVO<UserOrderPoolVO> pageUserOrderPoolVO(String userId, Page<UserOrderPoolVO> page, String sql, int isDelete);

    /**
     * 是否是某位用户的订单
     * @param orderId userId
     * @date 2019/4/26 0:53
     */
    Boolean isUserOrder(String orderId, String userId);

    /**
     * 是否是某位courier的订单
     * @param orderId userId
     * @date 2019/4/26 0:53
     */
    Boolean isCourierOrder(String orderId, String courierId);

    /**
     * 刷新订单状态
     *
     * @param orderId
     * @return
     */
    Integer flushOrderStatus(String orderId);

    /**
     * 删除订单信息（逻辑删除）
     *
     * @param orderId
     * @return
     */
    ResponseResult deleteOrderById(String orderId, String userId);

    /**
     * 推进订单
     *
     * @param orderId, remark
     * @return
     */
    ResponseResult pushOrderById(String orderId, String remark, String deliverPostNumber);

    /**
     * 订单异常化
     *
     * @param orderId, remark
     * @return
     */
    ResponseResult abnormalOrderById(String orderId, String remark);

    /**
     * 还原订单（逻辑删除）
     *
     * @param orderId
     * @return
     */
    ResponseResult rollbackOrderById(String orderId, String userId);

    /**
     * 根据用户获取仪表台数据
     *
     * @param userId
     * @return
     */
    Map<String, Integer> getDashboardDataByUser(String userId);

    /**
     * 根据用户获取当月所有订单
     *
     * @param userId
     * @return
     */
    List<Order> getMonthListByUser(String userId);

    /**
     * 根据用户获取当年所有订单
     *
     * @param userId
     * @return
     */
    List<Order> getYearListByUser(String userId);

    /**
     * 获取当年的月销售额
     *
     * @param userId
     * @return
     */
    List<Double> getMonthlySalesRevenueByUser(String userId);

    /**
     * 刷新交易成功比率
     *
     * @param userId
     * @return
     */
    Double flushProportion(String userId);


}
