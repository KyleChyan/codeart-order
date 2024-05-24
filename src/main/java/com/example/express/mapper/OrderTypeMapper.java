package com.example.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.domain.bean.Order;
import com.example.express.domain.bean.OrderType;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import org.apache.ibatis.annotations.Param;


public interface OrderTypeMapper extends BaseMapper<OrderType> {


}
