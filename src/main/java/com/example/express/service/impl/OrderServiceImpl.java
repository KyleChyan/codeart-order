package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.BeanUtil;
import com.example.express.common.util.OrderUtil;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.*;
import com.example.express.domain.enums.PaymentStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.domain.vo.req.OrderUpdateReq;
import com.example.express.domain.vo.user.UserOrderDetailVO;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import com.example.express.mapper.OrderItemMapper;
import com.example.express.mapper.OrderMapper;
import com.example.express.service.ClientService;
import com.example.express.service.DataOrderTypeService;
import com.example.express.service.HeadService;
import com.example.express.service.OrderService;
import io.lettuce.core.ScriptOutputType;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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

    //加急常量值
    private final static Integer URGENT_ID=0;

    //加急常量值
    private final static Integer ABNORMAL_ID=7;

    @Override
    public ResponseResult getOrderDetailById(String orderId) {
        try {
            UserOrderDetailVO userOrderDetailVO = new UserOrderDetailVO();
            //1.查询order表，copy到vo中
            Order order = getById(orderId);
            if (order == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
            }
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
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            Order order = new Order();
            LocalDateTime nowTime = LocalDateTime.now();
            String dateFormatter = OrderUtil.generateDateFormatter(nowTime);

            //set order内的属性
            BeanUtil.copyProperties(req, order);
            order.setOrderStatus(1);//订单状态
            order.setUserId(uid);//userid
            // 设定时间属性
            order.setCreateTime(nowTime);
            order.setModifyTime(nowTime);

            /**
              设定工期
             */
//            if (req.getUrgent() ==null||!req.getUrgent()) {
//                order.setDeadlineTime(nowTime.plusDays(15));//正常交付时间
//            }else
//                order.setDeadlineTime(nowTime.plusDays(4));//加急交付时间

            //设定价格
            DataOrderType orderType = dataOrderTypeService.getByCache(req.getTypeId());
            double totalPrice = (orderType.getDeposit() + orderType.getFinalPayment()) * req.getCount() + req.getExtraPrice();
            if (req.getUrgent() == null || !req.getUrgent()) {
                req.setUrgent(false);
            }
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
    public ResponseResult updateOrder(OrderUpdateReq req, String uid) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        Order order = new Order();
        LocalDateTime nowTime = LocalDateTime.now();
        if (req.getClientNickname() != null || req.getDeliverPhone() != null
                || req.getDeliverName() != null || req.getDeliverAddress() != null) {
            Client client = new Client();
            Order selectById = orderMapper.selectById(req.getOrderId());
            Client clientDetailById = clientService.getClientDetailById(selectById.getClientId());
            BeanUtil.copyProperties(req, client);
            client.setClientId(clientDetailById.getClientId());
            clientService.updateClientDetail(client);
        }
        BeanUtil.copyProperties(req, order);

        if (Objects.equals(order.getDeliverPostNumber(), "☆ 未寄出 ☆")) {
            order.setDeliverPostNumber(null);
        }
        order.setModifyTime(nowTime);
        int updated = orderMapper.updateById(order);
        if (updated == 1) {
            transactionManager.commit(status);
            return ResponseResult.success(order.getOrderId());
        }else{
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_ERROR);
        }
    }

    @Override
    public BootstrapTableVO<UserOrderPoolVO> pageUserOrderPoolVO(String userId, Page<UserOrderPoolVO> page, String sql, int isDelete) {
        BootstrapTableVO<UserOrderPoolVO> vo = new BootstrapTableVO<>();
        IPage<UserOrderPoolVO> selectPage = orderMapper.pageUserOrderVO(page, sql, isDelete);

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
        if(order == null || StringUtils.isBlank(order.getOrderId())) {
            return false;
        }
        return order.getUserId().equals(userId);
    }

    @Override
    public Boolean isCourierOrder(String orderId, String courierId) {
        Order order = getById(orderId);
//        if(order == null || StringUtils.isBlank(order.getCourierId())) {
//            return false;
//        }
//        return order.getCourierId().equals(courierId);
        return false;
    }

    @Override
    public Integer flushOrderStatus(String orderId) {
        // 从数据库中获取订单信息
        Order order = getById(orderId);
        if (order == null) {
            return null; // 如果订单不存在，返回 null
        }

        //若订单还未开工，返回null，否则返回正常时间
        if (order.getDeadlineTime() == null) {
            return null;
        }else {
            // 转换为 LocalDate 进行天数计算
            LocalDate nowDate = LocalDateTime.now().toLocalDate();
            LocalDate deadlineDate = order.getDeadlineTime().toLocalDate();
            // 使用 ChronoUnit 类计算两个时间之间的时间间隔
            long days = ChronoUnit.DAYS.between(nowDate, deadlineDate);
            // 将剩余天数转换为 Integer 类型并返回
            return Math.toIntExact(days);
        }

    }

    @Override
    public ResponseResult deleteOrderById(String orderId, String userId) {
        try {
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            int deleted=0;

            Order order = getById(orderId);
            order.setOrderDeleted(1);
            order.setModifyTime(LocalDateTime.now());
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
    public ResponseResult pushOrderById(String orderId, String remark, String deliverPostNumber) {
        try {
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            Order order = getById(orderId);
            LocalDateTime nowTime = LocalDateTime.now();
            if (order == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
            }
            DataOrderType orderType = dataOrderTypeService.getByCache(order.getTypeId());

            // 设定工期
            if (order.getOrderStatus() == 2) {
                if (order.getUrgent() ==null||!order.getUrgent()) {
                    order.setDeadlineTime(nowTime.plusDays(orderType.getNormalPeriod()));//正常交付时间
                }else
                    order.setDeadlineTime(nowTime.plusDays(orderType.getUrgentPeriod()));//加急交付时间
            }

            //装配到新的order中
            if (deliverPostNumber != null) {
                order.setDeliverPostNumber(deliverPostNumber);
            }
            Integer orderStatus = order.getOrderStatus();
            Integer newStatus = orderStatus+1;
            order.setOrderStatus(newStatus);
            order.setModifyTime(nowTime);
            order.setRemark(remark);

            int updated = orderMapper.updateById(order);

            //失败回滚
            if (updated == 0) {
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
    public ResponseResult abnormalOrderById(String orderId, String remark) {
        try {
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);

            Order order = getById(orderId);
            if (order == null) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
            }
            order.setOrderStatus(ABNORMAL_ID);
            order.setModifyTime(LocalDateTime.now());
            order.setRemark(remark);
            int updated = orderMapper.updateById(order);
            //失败回滚
            if (updated == 0) {
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
    public ResponseResult rollbackOrderById(String orderId, String userId) {
        try {
            //初始化方法所需的变量
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(definition);
            int rollBacked=0;

            Order order = getById(orderId);
            order.setOrderDeleted(0);
            order.setModifyTime(LocalDateTime.now());
            rollBacked = orderMapper.updateById(order);
            //失败回滚
            if (rollBacked == 0) {
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
    public Map<String, Integer> getDashboardDataByUser(String userId) {
        Map<String, Integer> map = new HashMap<>();
        // 施工中的订单
        LambdaQueryWrapper<Order> buildWrapper = Wrappers.lambdaQuery();
        buildWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .eq(Order::getOrderStatus, 3);
        Integer buildCount = orderMapper.selectCount(buildWrapper);

        // 未开工的订单
        LambdaQueryWrapper<Order> readyWrapper = Wrappers.lambdaQuery();
        readyWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .and(wrapper -> wrapper.eq(Order::getOrderStatus, 1)
                        .or()
                        .eq(Order::getOrderStatus, 2));
        Integer readyCount = orderMapper.selectCount(readyWrapper);

        // 获取所有未删除订单
        LambdaQueryWrapper<Order> allOrdersWrapper = Wrappers.lambdaQuery();
        allOrdersWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0);
        List<Order> orderList = orderMapper.selectList(allOrdersWrapper);

        // 计算将超时的订单(>=7天)
        Integer remainCount = 0;
        for (Order order : orderList) {
            Integer remainDays = this.flushOrderStatus(order.getOrderId());
            if (remainDays != null && remainDays <= 7 && order.getOrderStatus() <= 3 && order.getOrderStatus() > 0) {
                remainCount++;
            }
        }

        // 获取本月第一天的开始时间和当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        // 创建查询条件
        LambdaQueryWrapper<Order> thisMonthWrapper = Wrappers.lambdaQuery();
        thisMonthWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .ge(Order::getCreateTime, firstDayOfMonth)
                .le(Order::getCreateTime, now);

        // 获取本月订单数量
        Integer monthCount = orderMapper.selectCount(thisMonthWrapper);

        map.put("readyCount", readyCount);
        map.put("buildCount", buildCount);
        map.put("remainCount", remainCount);
        map.put("monthCount", monthCount);

        return map;
    }

    @Override
    public List<Order> getMonthListByUser(String userId) {
        /*
         * 获取当月图表数据
         * */

        // 获取本月第一天的开始时间和当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        // 创建查询条件
        LambdaQueryWrapper<Order> thisMonthWrapper = Wrappers.lambdaQuery();
        thisMonthWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .ge(Order::getCreateTime, firstDayOfMonth)
                .le(Order::getCreateTime, now)
                .orderByDesc(Order::getCreateTime);

        // 获取所有本月订单
        return orderMapper.selectList(thisMonthWrapper);
    }

    @Override
    public List<Order> getYearListByUser(String userId) {
        // 获取今年的第一天的开始时间和当前时间
        LocalDate today = LocalDate.now();
        LocalDateTime firstDayOfYear = today.withDayOfYear(1).atStartOfDay();
        // 创建查询条件
        LambdaQueryWrapper<Order> thisYearWrapper = Wrappers.lambdaQuery();
        thisYearWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .ge(Order::getCreateTime, firstDayOfYear)
                .le(Order::getCreateTime, today.atStartOfDay().plusDays(1))
                .orderByDesc(Order::getCreateTime);

        // 获取今年到今天的所有订单
        return orderMapper.selectList(thisYearWrapper);
    }

    public List<Double> getMonthlySalesRevenueByUser(String userId) {
        List<Order> yearToDateOrders = getYearListByUser(userId);

        // 初始化每个月的销售额为0
        Map<Integer, Double> monthlySalesRevenueMap = new HashMap<>();
        for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
            monthlySalesRevenueMap.put(i, 0.0);
        }

        // 更新每个月的销售额
        for (Order order : yearToDateOrders) {
            LocalDateTime createTime = order.getCreateTime();
            int month = createTime.getMonthValue();
            double totalPrice = order.getTotalPrice();
            monthlySalesRevenueMap.put(month, monthlySalesRevenueMap.get(month) + totalPrice);
        }

        // 将每个月的销售额添加到列表中
        List<Double> monthlySalesRevenue = new ArrayList<>(monthlySalesRevenueMap.values());

        return monthlySalesRevenue;
    }

    @Override
    public Double flushProportion(String userId) {
        // 获取交易成功的订单数量
        LambdaQueryWrapper<Order> successDealWrapper = Wrappers.lambdaQuery();
        successDealWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .eq(Order::getOrderStatus,5);
        Integer successCount = orderMapper.selectCount(successDealWrapper);
        // 获取交易失败的订单数量
        LambdaQueryWrapper<Order> failedDealWrapper = Wrappers.lambdaQuery();
        failedDealWrapper.eq(Order::getUserId, userId)
                .eq(Order::getOrderDeleted, 0)
                .in(Order::getOrderStatus,7,8,9);
        Integer failedCount = orderMapper.selectCount(failedDealWrapper);

        Double percentage = 0.0;
        //
        if (failedCount == 0 && successCount == 0) {
            return 100.0;
        } else {
            //计算交易成功订单百分比
            percentage = Math.round((double) successCount / (failedCount+successCount) * 100 * 100.0) / 100.0;
        }

        System.out.println("percentage的值是：：：："+percentage);
        return percentage;
    }

}
