package com.example.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.domain.bean.Order;
import com.example.express.domain.vo.user.UserOrderPoolVO;
import com.example.express.domain.vo.user.UserOrderVO;
import org.apache.ibatis.annotations.Param;


public interface OrderMapper extends BaseMapper<Order> {

    IPage<UserOrderPoolVO> pageUserOrderVO(Page<UserOrderPoolVO> page, @Param("sql") String selectSql, @Param("has_delete") int isDelete);

}
