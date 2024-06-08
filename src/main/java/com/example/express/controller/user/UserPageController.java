package com.example.express.controller.user;

import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.domain.bean.Order;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.user.UserInfoVO;
import com.example.express.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 普通用户页面 Controller
 * @date 2019年04月20日 23:29
 */
@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserPageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserFeedbackService feedbackService;
    @Autowired
    private UserEvaluateService userEvaluateService;
    @Autowired
    private OrderEvaluateService orderEvaluateService;
    @Autowired
    private DataCompanyService dataCompanyService;
    @Autowired
    private OrderService orderService;

//    /**
//     * 仪表盘页面(已弃用)
//     */
//    @RequestMapping("/dashboard")
//    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
//        map.put("frontName", sysUserService.getFrontName(sysUser));
//
//        String score = userEvaluateService.getScoreFromCache(sysUser.getId());
//        int evaluateCount = orderEvaluateService.countEvaluate(sysUser.getId(), SysRoleEnum.USER);
//
//        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
//        map.put("evaluateDesc", userDesc);
//
//        Map<String, Integer> data1 = orderInfoService.getUserDashboardData(sysUser.getId());
//        String orderDesc = "未支付订单数：：" + data1.get("waitPayment") +
//                "，等待接单数：：" + data1.get("wait") +
//                "，正在派送数：" + data1.get("transport");
//        map.put("orderDesc", orderDesc);
//
//        Map<String, Integer> data2 = feedbackService.getUserDashboardData(sysUser.getId());
//        String feedbackDesc = "正在处理的反馈数：" + data2.get("process") +
//                "，未处理的反馈数：" + data2.get("wait");
//        map.put("feedbackDesc", feedbackDesc);
//
//        return "user/dashboard";
//    }

    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        Map<String, Integer> data = orderService.getDashboardDataByUser(sysUser.getId());
        map.put("readyCount", data.get("readyCount"));
        map.put("buildCount", data.get("buildCount"));
        map.put("remainCount", data.get("remainCount"));

        return "user/dashboard";
    }

    /**
     * 仪表盘页面
     */
    @RequestMapping("/newDashboard")
    public String shownewDashboard(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        Map<String, Integer> data = orderService.getDashboardDataByUser(sysUser.getId());

        List<Order> dailyRevenueList = orderService.getMonthListByUser(sysUser.getId());
        List<Double> monthSalesRevenue = orderService.getMonthlySalesRevenueByUser(sysUser.getId());
        Double dealProportion = orderService.flushProportion(sysUser.getId());

        // 创建一个从本月第一天到当前日期的日期列表
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        for (LocalDate date = firstDayOfMonth; !date.isAfter(currentDate); date = date.plusDays(1)) {
            dates.add(date);
        }

        // 初始化每日销售额和销售量为0
        Map<LocalDate, Double> dailySalesRevenueMap = new TreeMap<>();
        Map<LocalDate, Integer> dailySalesCountMap = new TreeMap<>();
        for (LocalDate date : dates) {
            dailySalesRevenueMap.put(date, 0.0);
            dailySalesCountMap.put(date, 0);
        }

        // 遍历本月的订单列表
        for (Order order : dailyRevenueList) {
            // 获取订单的创建时间
            LocalDateTime createTime = order.getCreateTime();
            // 从订单创建时间中提取日期（年月日）
            LocalDate createDate = createTime.toLocalDate();

            // 更新销售额
            double totalRevenue = dailySalesRevenueMap.get(createDate) + order.getTotalPrice();
            dailySalesRevenueMap.put(createDate, totalRevenue);

            // 更新销售量
            int totalCount = dailySalesCountMap.get(createDate) + 1;
            dailySalesCountMap.put(createDate, totalCount);
        }

        // 将每日销售额和销售量转换为列表
        List<Double> daySalesRevenue = new ArrayList<>(dailySalesRevenueMap.values());
        List<Integer> daySalesCount = new ArrayList<>(dailySalesCountMap.values());


        System.out.println("salesRevenue"+ daySalesRevenue+",,salesCount"+daySalesCount+",,monthSalesRevenue"+monthSalesRevenue);
        map.put("readyCount", data.get("readyCount"));
        map.put("buildCount", data.get("buildCount"));
        map.put("remainCount", data.get("remainCount"));
        map.put("monthCount", data.get("monthCount"));
        map.put("daySalesRevenue", daySalesRevenue);
        map.put("daySalesCount", daySalesCount);
        map.put("monthSalesRevenue", monthSalesRevenue);
        map.put("dealProportion", dealProportion);

        return "user/newDashboard";
    }
    /**
     * 下单页面
     */
    @RequestMapping("/order")
    public String showOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/order";
    }

    /**
     * 新下单页面
     */
    @RequestMapping("/newOrder")
    public String showNewOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/newOrder";
    }

    /**
     * 订单池
     */
    @RequestMapping("/orderPool")
    public String showOrderPool(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/orderPool";
    }

    /**
     * 支付页面
     * @author Kyle
     * @date 2019/4/23 0:00
     */
    @RequestMapping("/order/place")
    public String placeOrder(OrderInfo orderInfo, ModelMap map, HttpSession session, @AuthenticationPrincipal SysUser sysUser) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("order", orderInfo);
        map.put("company", dataCompanyService.getByCache(orderInfo.getCompany()).getName());
        session.setAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS, orderInfo);
        return "user/payment";
    }

    /**
     * 订单新增页面
     * @author Kyle
     * @date 2019/4/23 0:00
     */
    @RequestMapping("/order/newplace")
    public String createOrder(OrderInfo orderInfo, ModelMap map, HttpSession session, @AuthenticationPrincipal SysUser sysUser) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("order", orderInfo);
        map.put("company", dataCompanyService.getByCache(orderInfo.getCompany()).getName());
        session.setAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS, orderInfo);
        return "user/payment";
    }

    /**
     * 订单列表页面
     */
    @RequestMapping("/history")
    public String showHistory(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/history";
    }

    /**
     * 评价中心页面
     */
    @RequestMapping("/evaluate")
    public String showEvaluate(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("score", userEvaluateService.getScoreFromCache(sysUser.getId()));
        return "user/evaluate";
    }

    /**
     * 个人中心页面
     */
    @RequestMapping("/info")
    public String showInfoPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "user/info";
    }

    /**
     * 回收站页面
     */
    @RequestMapping("/recycle")
    public String showRecyclePage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/recycle";
    }

    /**
     * 操作日志页面
     */
    @RequestMapping("/log")
    public String showLogPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/log";
    }

    /**
     * 反馈建议页面
     */
    @RequestMapping("/feedback")
    public String showFeedbackPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/feedback";
    }

    /**
     * 收货地址页面
     */
    @RequestMapping("/address")
    public String showAddressPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/address";
    }
}
