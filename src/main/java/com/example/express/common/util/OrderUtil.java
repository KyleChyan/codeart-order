package com.example.express.common.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @Author Kyle
 * @Date 2024/4/16 18:14
 * @Version 1.0
 */

public class OrderUtil {


//    private static TypeOrderService typeOrderService;

    /**    时间戳模板 yyyyMMddHHmm   */
    public static final String PATTERN= "yyyyMMddHHmm";

    /**
     * 生成时间戳yyyyMMddHHmm
     */
    public static String generateDateFormatter(LocalDateTime nowTime) {

        // 定义要使用的日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);

        // 格式化并return LocalDateTime
        return nowTime.format(formatter);
    }

    /**
     * 获取订单号
     */
    public static String generateOrderId(LocalDateTime nowTime) {

        //格式化时间
        String formattedDateTime = generateDateFormatter(nowTime);

        // 生成三位随机数
        Random random = new Random();
        int randomNumber = random.nextInt(1000); // 生成0到999的随机数

        // 将随机数格式化为三位数的字符串
        String formattedRandomNumber = String.format("%03d", randomNumber);

        // 拼接订单号

        return formattedDateTime + formattedRandomNumber;
    }
    /**
     * 获取子订单价格（仅定金）
     */
//    public static double getItemPrice(OrderItemReq req) {
//        OrderType typeOrder = typeOrderService.getTypeOrderDetailById(req.getTypeId());
//        return req.getExtraPrice() + typeOrder.getDeposit() * req.getHeadCount();
//    }

    /**
     * 获取订单总价格（仅定金）
     */
//    public static double getTotalPrice(OrderItemReq req) {
//        OrderType typeOrder = typeOrderService.getTypeOrderDetailById(req.getTypeId());
//        return req.getExtraPrice() + typeOrder.getDeposit() * req.getHeadCount();
//    }
}
