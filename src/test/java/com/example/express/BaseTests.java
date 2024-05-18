package com.example.express;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {"com.example.express"})
public class BaseTests {

    @Test
    public void test1(){
        // 获取当前的 LocalDateTime
        LocalDateTime now = LocalDateTime.now();

        // 定义要使用的日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        // 格式化 LocalDateTime
        String formattedDateTime = now.format(formatter);

        // 生成三位随机数
        Random random = new Random();
        int randomNumber = random.nextInt(1000); // 生成0到999的随机数

        // 将随机数格式化为三位数的字符串
        String formattedRandomNumber = String.format("%03d", randomNumber);

        // 拼接订单号
        String orderId = formattedDateTime + formattedRandomNumber;
        System.out.println(orderId);
    }


}
