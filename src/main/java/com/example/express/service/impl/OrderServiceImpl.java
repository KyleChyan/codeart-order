package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.BeanUtil;
import com.example.express.common.util.OrderUtil;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.*;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.domain.vo.user.UserOrderDetailVO;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import com.example.express.mapper.OrderItemMapper;
import com.example.express.mapper.OrderMapper;
import com.example.express.service.ClientService;
import com.example.express.service.DataOrderTypeService;
import com.example.express.service.HeadService;
import com.example.express.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private ClientService clientService;

    @Resource
    private HeadService headService;

    @Resource
    private DataOrderTypeService dataOrderTypeService;

    @Resource
    private DataSourceTransactionManager transactionManager;

    private final static Integer URGENT_ID=0;

    @Override
    public ResponseResult getOrderDetailById(String orderId) {
        try {
            UserOrderDetailVO userOrderDetailVO = new UserOrderDetailVO();
            //1.查询order表，copy到vo中
            Order order = getById(orderId);
            if (order == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
            }
            System.out.println("查询到的order是："+order);
            BeanUtil.copyProperties(order, userOrderDetailVO);
            //设定订单类型名称
            userOrderDetailVO.setTypeName(dataOrderTypeService.getByCache(order.getTypeId()).getTypeName());
            if (order.getDeliverPostNumber() == null) {
                userOrderDetailVO.setDeliverPostNumber("☆ 未寄出 ☆");
            }
            //2.根据order的clientid查询出信息，set到vo中
            Client client = clientService.getClientDetailById(order.getClientId());
            if (client == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
            }
            userOrderDetailVO.setClientNickname(client.getClientNickname());
            userOrderDetailVO.setDeliverName(client.getDeliverName());
            userOrderDetailVO.setDeliverPhone(client.getDeliverPhone());
            userOrderDetailVO.setDeliverAddress(client.getDeliverAddress());
            //3.根据order的headid查询出typename和fen，set到vo中
            Head head = headService.getHeadDetailById(order.getHeadId());
            if (head == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_ERROR);
            }
            userOrderDetailVO.setHeadName(head.getHeadName());
            userOrderDetailVO.setFen(head.getFen());

            return ResponseResult.success(userOrderDetailVO);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public ResponseResult insertOrder(OrderInsertReq req, String uid) {
        try {
            System.out.println(req);
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            Order order = new Order();
            LocalDateTime nowTime = LocalDateTime.now();
            String dateFormatter = OrderUtil.generateDateFormatter(nowTime);

            //set order内的属性
            BeanUtil.copyProperties(req, order);
            order.setOrderStatus(1);//订单状态
//            order.setPlatform(PlatformsEnum.TAOBAO);
            order.setUserId(uid);//userid
                // 设定时间属性
            order.setCreateTime(nowTime);
            order.setModifyTime(nowTime);
            /**
              设定工期
             */
            if (req.getUrgent() ==null||!req.getUrgent()) {
                order.setDeadlineTime(nowTime.plusDays(15));//正常交付时间
            }else
                order.setDeadlineTime(nowTime.plusDays(4));//加急交付时间

            //设定价格
            DataOrderType orderType = dataOrderTypeService.getByCache(req.getTypeId());
            double totalPrice = (orderType.getDeposit() + orderType.getFinalPayment()) * req.getCount() + req.getExtraPrice();
            if (req.getUrgent()) {//加急收取额外费用
                DataOrderType orderTypeUrgent = dataOrderTypeService.getByCache(URGENT_ID);
                totalPrice+=(orderTypeUrgent.getDeposit()+orderTypeUrgent.getFinalPayment())* req.getCount();
            }
            order.setTotalPrice(totalPrice);

            //设定订单号
            String orderId = OrderUtil.generateOrderId(nowTime);
            order.setOrderId(orderId);

            //设定客户id和客户信息，查找是否存在客户信息，不存在则新增,
            Client client = clientService.getClientDetailByNickname(req.getClientNickname());
            Client newClient = new Client();
            if (client == null) {
                BeanUtil.copyProperties(req,newClient);
                newClient.setCreateTime(nowTime);
                newClient.setClientId(Long.valueOf(dateFormatter));
                newClient.setRemark("");
                ResponseResult insertedClient = clientService.insertClient(newClient);
                if (insertedClient.getCode()!=0) {
                    transactionManager.rollback(status);
                    return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
                }
                order.setClientId(newClient.getClientId());
            }else
                order.setClientId(client.getClientId());

            //设定娃头id和信息，查找是否存在娃头信息，不存在则新增
            Head head = headService.getHeadByHeadnameAndFen(req.getHeadName(), req.getFen());
            Head newHead = new Head();
            if (head == null) {
                BeanUtil.copyProperties(req,newHead);
                newHead.setCreateTime(nowTime);
                newHead.setHeadId(Long.valueOf(dateFormatter));
                newHead.setRemark("");
                ResponseResult insertedHead = headService.insertHead(newHead);
                if (insertedHead.getCode()!=0) {
                    transactionManager.rollback(status);
                    return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
                }
                order.setHeadId(newHead.getHeadId());
            }else
                order.setHeadId(head.getHeadId());


            int insertedOrder = orderMapper.insert(order);
            if (insertedOrder == 1) {
                transactionManager.commit(status);
                return ResponseResult.success(orderId);
            }else{
                transactionManager.rollback(status);
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_CREATE_ERROR);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BootstrapTableVO<UserOrderPoolVO> pageUserOrderPoolVO(String userId, Page<UserOrderPoolVO> page, String sql, int isDelete) {
        BootstrapTableVO<UserOrderPoolVO> vo = new BootstrapTableVO<>();
        IPage<UserOrderPoolVO> selectPage = orderMapper.pageUserOrderVO(page, sql, isDelete);
/*

        for(UserOrderPoolVO orderVO : selectPage.getRecords()) {
            // 设置下单平台
            if (orderVO.getPlatform() != null) {
                String platformLabel = enumCacheService.getEnumLabel("platforms_enum", orderVO.getPlatform());
                orderVO.setPlatformName(platformLabel);
            }
            // 设置订单类型
            if(orderVO.getOrderStatus()!= null) {

                orderVO.setOrderStatusName(enumCacheService.getEnumLabel("new_order_status_enum", orderVO.getPlatform()));
            }
            // 设置是否可以评分

        }
*/
        //设定特殊字段渲染到前端
        for (UserOrderPoolVO poolVO : selectPage.getRecords()) {
            poolVO.setRemainDays(this.flushOrderStatus(poolVO.getOrderId()));
            poolVO.setTypeName(this.dataOrderTypeService.getByCache(poolVO.getTypeId()).getTypeName());
        }

        vo.setTotal(selectPage.getTotal());
        vo.setRows(selectPage.getRecords());

        return vo;
    }

    @Override
    public Boolean isUserOrder(String orderId, String userId) {
        Order order = getById(orderId);
        if(order == null) {
            return false;
        }
        return order.getUserId().equals(userId);
    }

    @Override
    public Integer flushOrderStatus(String orderId) {
        // 从数据库中获取订单信息
        Order order = getById(orderId);
        if (order == null) {
            return null; // 如果订单不存在，返回 null
        }

        // 获取订单的和截止时间
        LocalDateTime deadlineTime = order.getDeadlineTime();

        // 获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();

        // 使用 Duration 类计算两个时间之间的时间间隔
        Duration duration = Duration.between(nowTime, deadlineTime);
        // 将时间间隔转换为天数
        long days = duration.toDays();
        // 将剩余天数转换为 Integer 类型并返回
        return Math.toIntExact(days+1);
    }

    @Override
    public ResponseResult delectOrderById(String orderId, String userId) {
        try {
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            int deleted=0;

            Order order = getById(orderId);
            order.setOrderDeleted(1);
            deleted = orderMapper.updateById(order);
            //失败回滚
            if (deleted == 0) {
                transactionManager.rollback(status);
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_ERROR);
            }

            transactionManager.commit(status);
            return ResponseResult.success(orderId);
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String updateOrderDetail(Order order) {
        return null;
    }

    @Override
    public String startOrder(Integer id) {
        return null;
    }

    @Override
    public String sendOrder(String orderId, String deliverPostNumber) {
        return null;
    }

    @Override
    public String returnOrder(String orderId, Integer orderStatus, String deliverPostNumber, String remark) {
        return null;
    }

    @Override
    public String finishOrder(String orderId, Integer orderStatus) {
        return null;
    }


}
