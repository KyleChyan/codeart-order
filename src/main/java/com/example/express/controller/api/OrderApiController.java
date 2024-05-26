package com.example.express.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.PaymentStatusEnum;
import com.example.express.domain.enums.PlatformsEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.OrderDescVO;
import com.example.express.domain.vo.courier.CourierOrderVO;
import com.example.express.domain.vo.req.OrderInsertReq;
import com.example.express.exception.CustomException;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderService;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * API订单接口
 * @author Kyle
 * @date 2019年04月22日 23:54
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderApiController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderService orderService;

    /** （ 新！！）
     * AJAX获取下单平台
     * - 用户：个人订单
     * @author Kyle
     * @date 2019/4/25 23:36
     */
    @GetMapping("/getPlatforms")
    @PreAuthorize("hasRole('ROLE_USER')")
    public PlatformsEnum[] getPlatforms() {
        return PlatformsEnum.values();
    }

    /**（ 新！！）
     * 获取订单池
     * - 普通用户：userId = self
     * - 配送员：courierId = self
     * - 管理员：无限制
     * @param type 0:正常订单；1：已删除订单
     * @author Kyle
     * @date 2019/4/24 22:21
     */
    @GetMapping("/pool")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public BootstrapTableVO listOrderPool(@RequestParam(required = false, defaultValue = "1") Integer current,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          String type, Integer orderStatus, String orderId, String clientNickname,
                                          String receivePostNumber, String deliverPhone, String startCreateTime, String endCreateTime,
                                          Boolean reserve,
                                          @AuthenticationPrincipal SysUser sysUser) {
        Integer isDelete = StringUtils.toInteger(type, -1);
        if(isDelete == -1) {
            throw new CustomException(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        String userId = sysUser.getId();
        StringBuilder sql = new StringBuilder();

        // 默认情况下排除订单完成和订单关闭
        boolean defaultStatusFilter = (orderStatus == null || orderStatus == -1);

        // 筛选订单状态
        if (orderStatus != null && orderStatus != -1) {
            if (orderStatus == 0) {
                // 如果选择了预订单状态
                sql.append(" AND orderlist.reserve = true");
            } else {
                // 选择了具体的订单状态
                sql.append(" AND orderlist.order_status = ").append(orderStatus);
            }
        } else {
            // 默认筛选条件，排除订单完成和订单关闭
            sql.append(" AND orderlist.order_status NOT IN (5, 9)");
        }

        //不看预订单按钮
        if (reserve) {
            if (orderStatus!=null&&orderStatus==0){
                sql.append(" AND orderlist.reserve = true");
            }else
                sql.append(" AND orderlist.reserve = false");
        }

//        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getByStatus(StringUtils.toInteger(status, -1));
        //订单号筛选
        if(StringUtils.isNotBlank(orderId)) {
            if (orderId.length() == 5) {
                sql.append(" AND orderlist.order_id like CONCAT('%',").append(orderId).append(")");
            }else {
                sql.append(" AND orderlist.order_id = ").append(orderId);
            }
        }

        //客户昵称筛选
        if(StringUtils.isNotBlank(clientNickname)) {
            sql.append(" AND client.client_nickname like CONCAT('%','").append(clientNickname).append("','%')");

        }

        //客户手机号筛选
        if(StringUtils.isNotBlank(deliverPhone)) {
            if (deliverPhone.length() == 4) {
                sql.append(" AND client.deliver_phone like CONCAT('%',").append(deliverPhone).append(")");
            }else
                sql.append(" AND client.deliver_phone = ").append(deliverPhone);
        }

        //收货运单号筛选
        if(StringUtils.isNotBlank(receivePostNumber)) {
            if (receivePostNumber.length() == 5) {
                sql.append(" AND orderlist.receive_post_number like CONCAT('%',").append(receivePostNumber).append(")");
            }else
                sql.append(" AND orderlist.receive_post_number = ").append(receivePostNumber);
        }

        //起始日期筛选
        if(StringUtils.isNotBlank(startCreateTime)) {
            sql.append(" AND orderlist.create_time > '").append(startCreateTime).append("'");
        }

        if(StringUtils.isNotBlank(endCreateTime)) {
            sql.append(" AND orderlist.create_time < '").append(endCreateTime).append("'");
        }

        System.out.println("预订单的选项是： AND orderlist.order_status NOT IN(5,9) "+reserve);





        Page page = new Page<>(current, size);
        page.setAsc("create_time");
        switch (sysUser.getRole()) {
            case USER:
//                sql.append(" AND info.user_id = '").append(userId).append("'");
                return orderService.pageUserOrderPoolVO(userId, page, sql.toString(), isDelete);
            default:
                return new BootstrapTableVO();
        }
    }

    /**（ 新！！）
     * 获取订单池详情信息
     * - 管理员：任何订单
     * - 派送员：已接的单
     * - 用户：个人订单
     * @author Kyle
     *
     */
    @GetMapping("/pool/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult showOrderDetail(@PathVariable String id,
                                         @AuthenticationPrincipal SysUser sysUser) {
        System.out.println("当前的orderid是："+id);
        // 权限校验
        switch (sysUser.getRole()) {
            case USER:
                if(!orderService.isUserOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            case COURIER:
                if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            default:
                break;
        }


        return orderService.getOrderDetailById(id);
    }

    /**（ 新！！）
     * 删除订单
     * @param orderIds
     * @author Kyle
     * @since 2018/5/14 8:53
     */
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult deleteOrder(String[] orderIds, @AuthenticationPrincipal SysUser sysUser) {

        int success = 0;
        //校验成功的返回个数
        for(String orderId : orderIds) {
            ResponseResult result = orderService.deleteOrderById(orderId,sysUser.getId());

            if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
                continue;
            }else
                success++;

            System.out.println("我得到的ResponseResult是："+result.getData());
        }

        // 返回
        Map<String, Integer> count = new HashMap<>(16);
        count.put("success", success);
        count.put("error", orderIds.length - success);
        return ResponseResult.success(count);
    }

    /**（ 新！！）
     * 提交订单
     * @param req
     * @author Kyle
     * @since 2018/5/14 8:53
     */
    @PostMapping("/sub")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult subOrder(OrderInsertReq req, @AuthenticationPrincipal SysUser sysUser) throws IOException {
//        OrderInsertReq req = (OrderInsertReq)session.getAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS);
//        req.setExtraPrice(10.00);
        System.out.println(req);


        // 生成订单
        ResponseResult result = orderService.insertOrder(req,sysUser.getId());
        if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            throw new CustomException(result);
        }
        System.out.println("我得到的ResponseResult是："+result.getData());
        return result;
    }

    /**（ 新！！）
     * 打开推进订单窗口
     * - 管理员：任何订单
     * - 派送员：已接的单
     * - 用户：个人订单
     * @author Kyle
     *
     */
    @GetMapping("/push/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult showPush(@PathVariable String id,
                                         @AuthenticationPrincipal SysUser sysUser) {
        // 权限校验
        switch (sysUser.getRole()) {
            case USER:
                if(!orderService.isUserOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            case COURIER:
                if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            default:
                break;
        }

        return orderService.getOrderDetailById(id);
    }

    /**（ 新！！）
     * 推进订单
     * @param orderId remark
     * @author Kyle
     * @since 2018/5/14 8:53
     */
    @PostMapping("/push")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult pushOrder(String orderId, String remark,String deliverPostNumber, @AuthenticationPrincipal SysUser sysUser) throws IOException {


        ResponseResult result = orderService.pushOrderById(orderId, remark,deliverPostNumber);
        System.out.println("我得到的ResponseResult是："+result);
        return result;
    }

    /**（ 新！！）
     *  订单异常化
     * - 管理员：任何订单
     * - 派送员：已接的单
     * - 用户：个人订单
     * @author Kyle
     *
     */
    @PostMapping("/abnormal")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult abnormalOrder(String orderId, String remark,
                                        @AuthenticationPrincipal SysUser sysUser) {
        ResponseResult result = orderService.abnormalOrderById(orderId, remark);
        System.out.println("我得到的ResponseResult是："+result);
        return result;
    }


    /**
     * 获取订单信息
     * - 管理员：任何订单
     * - 派送员：已接的单
     * - 用户：个人订单
     * @author Kyle
     * @date 2019/4/25 23:36
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult getOrderDesc(@PathVariable String id,
                                       @AuthenticationPrincipal SysUser sysUser) {
        // 权限校验
        switch (sysUser.getRole()) {
            case USER:
                if(!orderInfoService.isUserOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            case COURIER:
                if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
                }
                break;
            default:
                break;
        }

        OrderDescVO descVO = orderInfoService.getDescVO(id);
        if(descVO == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
        }

        return ResponseResult.success(descVO);
    }


    /**
     * 获取所有订单
     * - 普通用户：userId = self
     * - 配送员：courierId = self
     * - 管理员：无限制
     * @param type 0:正常订单；1：已删除订单
     * @author Kyle
     * @date 2019/4/24 22:21
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public BootstrapTableVO listSelfOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       String type, String startDate, String endDate, String status, String id, String payment,
                                                       @AuthenticationPrincipal SysUser sysUser) {
        Integer isDelete = StringUtils.toInteger(type, -1);
        if(isDelete == -1) {
            throw new CustomException(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        String userId = sysUser.getId();
        StringBuilder sql = new StringBuilder();

        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getByStatus(StringUtils.toInteger(status, -1));
        if(orderStatusEnum != null) {
            sql.append(" AND info.status = ").append(orderStatusEnum.getStatus());
        }

        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.getByStatus(StringUtils.toInteger(payment, -1));
        if(paymentStatusEnum != null) {
            sql.append(" AND payment.status = ").append(paymentStatusEnum.getStatus());
        }

        if(StringUtils.isNotBlank(startDate)) {
            sql.append(" AND info.create_date > '").append(startDate).append("'");
        }

        if(StringUtils.isNotBlank(endDate)) {
            sql.append(" AND info.create_date < '").append(endDate).append("'");
        }

        if(StringUtils.isNotBlank(id)) {
            sql.append(" AND info.id = ").append(id);
        }

        Page page = new Page<>(current, size);
        page.setDesc("create_date");
        switch (sysUser.getRole()) {
            case USER:
                sql.append(" AND info.user_id = '").append(userId).append("'");
                return orderInfoService.pageUserOrderVO(userId, page, sql.toString(), isDelete);
            case COURIER:
                sql.append(" AND info.courier_id = '").append(userId).append("'");
                return orderInfoService.pageCourierOrderVO(userId, page, sql.toString());
            case ADMIN:
                return orderInfoService.pageAdminOrderVO(page, sql.toString(), isDelete);
            default:
                return new BootstrapTableVO();
        }
    }

    /**
     * 获取所有状态为等待接单（支付成功）订单
     */
    @GetMapping("/wait-list")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public BootstrapTableVO<CourierOrderVO> listWaitDistOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                              @RequestParam(required = false, defaultValue = "10") Integer size,
                                                              String startDate, String endDate, String id, @AuthenticationPrincipal SysUser sysUser) {
        Page<CourierOrderVO> page = new Page<>(current, size);
        page.setDesc("create_date");

        StringBuilder sql = new StringBuilder();
        sql.append(" AND info.status = ").append(OrderStatusEnum.WAIT_DIST.getStatus());
        sql.append(" AND payment.status = ").append(PaymentStatusEnum.TRADE_SUCCESS.getStatus());

        if(StringUtils.isNotBlank(startDate)) {
            sql.append(" AND info.create_date > '").append(startDate).append("'");
        }
        if(StringUtils.isNotBlank(endDate)) {
            sql.append(" AND info.create_date < '").append(endDate).append("'");
        }
        if(StringUtils.isNotBlank(id)) {
            sql.append(" AND info.id = ").append(id);
        }

        return orderInfoService.pageCourierOrderVO(sysUser.getId(), page, sql.toString());
    }

    /**
     * 配送员批量接单
     */
    @PostMapping("/batch-accept")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult batchAccept(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchAcceptOrder(ids, sysUser.getId());
    }

    /**
     * 配送员异常订单
     */
    @PostMapping("/error")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult errorOrder(String id, String remark, @AuthenticationPrincipal SysUser sysUser) {
        if(StringUtils.isAnyBlank(id, remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > 255) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"异常信息", 255});
        }

        if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        return orderInfoService.handleOrder(id, OrderStatusEnum.ERROR, remark);
    }

    /**
     * 配送员完成订单
     */
    @PostMapping("/complete")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult completeOrder(String id, String remark, @AuthenticationPrincipal SysUser sysUser) {
        if(StringUtils.isAnyBlank(id, remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > 255) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"成功信息", 255});
        }

        if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        return orderInfoService.handleOrder(id, OrderStatusEnum.COMPLETE, remark);
    }

    /**
     * 管理员批量订单异常
     */
    @PostMapping("/batch-error")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchErrorOrder(String[] ids, String remark) {
        if(ids.length == 0 || StringUtils.isBlank(remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > 255) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"异常信息", 255});
        }

        int success = 0;
        // 循环异常订单，内部事务
        for(String orderId : ids) {
            ResponseResult result = orderInfoService.handleOrder(orderId, OrderStatusEnum.ERROR, remark);
            if(result.getCode() == ResponseErrorCodeEnum.SUCCESS.getCode()) {
                success++;
            }
        }

        // 返回
        Map<String, Integer> count = new HashMap<>(16);
        count.put("success", success);
        count.put("error", ids.length - success);
        return ResponseResult.success(count);
    }

    /**
     * 管理员批量删除订单
     * 相当于普通用户的撤销操作+删除操作
     */
    @PostMapping("/batch-remove")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchRemoveOrder(String[] ids) {
        if(ids.length == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        return orderInfoService.batchRemoveOrder(ids);
    }

    /**
     * 管理员批量分配
     */
    @PostMapping("/batch-allot")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchAllotOrder(String[] ids, String courier) {
        if(ids.length == 0 || StringUtils.isBlank(courier)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(sysUserService.getById(courier) == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.COURIER_NOT_EXIST);
        }

        return orderInfoService.batchAllotOrder(ids, courier);
    }

    /**
     * 管理员批量订单完成
     */
    @PostMapping("/batch-complete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchCompleteOrder(String[] ids, String remark) {
        if(ids.length == 0 || StringUtils.isBlank(remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > 255) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"成功信息", 255});
        }

        int success = 0;
        // 循环完成订单，内部事务
        for(String orderId : ids) {
            ResponseResult result = orderInfoService.handleOrder(orderId, OrderStatusEnum.COMPLETE, remark);
            if(result.getCode() == ResponseErrorCodeEnum.SUCCESS.getCode()) {
                success++;
            }
        }

        // 返回
        Map<String, Integer> count = new HashMap<>(16);
        count.put("success", success);
        count.put("error", ids.length - success);
        return ResponseResult.success(count);
    }

    /**
     * 用户批量删除订单，仅能删除个人订单
     * 状态为订单完成或订单异常
     * @author Kyle
     * @date 2019/4/24 23:08
     */
    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult batchDelete(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchDeleteOrder(ids, sysUser.getId());
    }

    /**
     * 用户批量撤销订单，仅能撤销个人订单
     * 状态为未接单
     * @author Kyle
     * @date 2019/4/25 0:11
     */
    @PostMapping("/batch-cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult batchCancel(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchCancelOrder(ids, sysUser.getId());
    }

    /**
     * 批量恢复订
     * - 普通用户：恢复个人订单
     * - 管理员：恢复任何订单
     * @author Kyle
     * @date 2019/4/26 1:58
     */
    @PostMapping("/batch-rollback")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseResult batchRollback(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        switch (sysUser.getRole()) {
            case ADMIN:
                return orderInfoService.batchRollback(ids, null);
            case USER:
                return orderInfoService.batchRollback(ids, sysUser.getId());
            default:
                return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }
    }
}
