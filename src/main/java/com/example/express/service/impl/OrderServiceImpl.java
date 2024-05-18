package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.EnumCacheService;
import com.example.express.common.util.BeanUtil;
import com.example.express.common.util.OrderUtil;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.*;
import com.example.express.domain.enums.NewOrderStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.domain.vo.req.OrderSearchReq;
import com.example.express.domain.vo.resp.OrderDetailResp;
import com.example.express.domain.vo.resp.OrderListResp;
import com.example.express.domain.vo.user.UserOrderDetailVO;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import com.example.express.mapper.OrderItemMapper;
import com.example.express.mapper.OrderMapper;
import com.example.express.service.ClientService;
import com.example.express.service.HeadService;
import com.example.express.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private DataSourceTransactionManager transactionManager;

    private final EnumCacheService enumCacheService;

    @Autowired
    public OrderServiceImpl(EnumCacheService enumCacheService) {
        this.enumCacheService = enumCacheService;
    }

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
            userOrderDetailVO.setTypeName(head.getTypeName());
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
            order.setOrderStatus(0);//订单状态
//            order.setPlatform(PlatformsEnum.TAOBAO);
            order.setUserId(uid);//userid
                // 设定时间属性
            order.setCreateTime(nowTime);
            order.setModifyTime(nowTime);
                // 设定加急
            if (req.getUrgent() ==null||!req.getUrgent()) {
                order.setDeadlineTime(nowTime.plusDays(15));//正常交付时间
            }else
                order.setDeadlineTime(nowTime.plusDays(4));//加急交付时间

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
            Head head = headService.getHeadByHeadnameAndFen(req.getTypeName(), req.getFen());
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

        vo.setTotal(selectPage.getTotal());
        vo.setRows(selectPage.getRecords());

        return vo;
    }

    @Override
    public List<OrderListResp> getOrderList(OrderSearchReq req) {
        try {
            List<OrderListResp> respList= new ArrayList<>();
            LambdaQueryWrapper<Order> orderWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<OrderItem> itemWrapper = Wrappers.lambdaQuery();
            LambdaQueryWrapper<Client> clientWrapper = Wrappers.lambdaQuery();
            //若是已完成/已关闭订单
            if (req.getOrderStatus()!=null&&req.getOrderStatus() == 5||req.getOrderStatus()!=null&&req.getOrderStatus() == 9) {
                orderWrapper.eq(Order::getOrderStatus,req.getOrderStatus());
            }
            //若存在客户昵称
            if (req.getClientNickname() != null) {
                Client client = clientService.getClientDetailByNickname(req.getClientNickname());
                orderWrapper
                        .eq(Order::getClientId,client.getClientId());
            }
            //若存在收货单号
            if (req.getReceivePostNumber() != null) {
                itemWrapper
                        .eq(req.getReceivePostNumber().length()!=5,OrderItem::getReceivePostNumber,req.getReceivePostNumber())
                        .likeLeft(req.getReceivePostNumber().length()==5,OrderItem::getReceivePostNumber,req.getReceivePostNumber())
                        .select(OrderItem::getOrderId);
                OrderItem orderItem = this.orderItemMapper.selectOne(itemWrapper);
                orderWrapper
                        .eq(Order::getOrderId,orderItem.getOrderId());
            }
            orderWrapper
                    .eq(req.getOrderId()!=null,Order::getOrderId,req.getOrderId())
                    .eq(req.getOrderStatus()!=null,Order::getOrderStatus,req.getOrderStatus())
                    .ge(req.getTotalPriceStart()!=0.0,Order::getTotalPrice,req.getTotalPriceStart())
                    .le(req.getTotalPriceEnd()!=0.0,Order::getTotalPrice,req.getTotalPriceEnd())
                    .likeLeft(req.getDeliverPostNumber()!=null&&req.getDeliverPostNumber().length()==5,Order::getDeliverPostNumber,req.getDeliverPostNumber())
                    .ge(req.getDeadlineTimeStart()!=null,Order::getDeadlineTime,req.getDeadlineTimeStart())
                    .le(req.getDeadlineTimeEnd()!=null,Order::getDeadlineTime,req.getDeadlineTimeEnd())
                    .eq(Order::getOrderDeleted,0);
            List<Order> orderList = this.orderMapper.selectList(orderWrapper);
            //加入客户昵称
            for (Order order : orderList) {
                    Client client = this.clientService.getClientDetailById(order.getClientId());
                    OrderListResp orderResp= new OrderListResp();
                    BeanUtil.copyProperties(order,orderResp);
                    orderResp.setClientNickname(client.getClientNickname());
                    orderResp.initResp();
                    respList.add(orderResp);
                }
            return respList;



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean isUserOrder(String orderId, String userId) {
        Order info = getById(orderId);
        if(info == null) {
            return false;
        }
        return info.getUserId().equals(userId);
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

    @Override
    public String delectOrder(Integer orderId) {
        return null;
    }
}
