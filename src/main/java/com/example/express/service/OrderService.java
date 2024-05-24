package com.example.express.service;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Order;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.domain.vo.req.OrderItemReq;
import com.example.express.domain.vo.req.OrderSearchReq;
import com.example.express.domain.vo.resp.OrderDetailResp;
import com.example.express.domain.vo.resp.OrderListResp;
import com.example.express.domain.vo.user.UserOrderDetailVO;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import com.example.express.domain.vo.user.UserOrderVO;

import java.util.List;

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
    ResponseResult delectOrderById(String orderId, String userId);

    /**
     * 更新订单信息
     *
     * @param order
     * @return
     */
    String updateOrderDetail(Order order);

    /**
     * 收货登记/开始施工
     *
     * @param id
     * @return
     */
    String startOrder(Integer id);

    /**
     * 施工结束/已经发货
     *
     * @param orderId,deliverPostNumber
     * @return
     */
    String sendOrder(String orderId,String deliverPostNumber);

    /**
     * 退货
     *
     * @param orderId,orderStatus,deliverPostNumber,remark
     * @return
     */
    String returnOrder(String orderId,Integer orderStatus,String deliverPostNumber,String remark);

    /**
     * 完成订单/关闭订单
     *
     * @param orderId,orderStatus
     * @return
     */
    String finishOrder(String orderId,Integer orderStatus);

}
