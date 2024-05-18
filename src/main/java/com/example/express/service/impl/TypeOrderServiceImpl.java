package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.TypeOrder;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.req.TypeOrderVO;
import com.example.express.mapper.TypeOrderMapper;
import com.example.express.service.TypeOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TypeOrderServiceImpl implements TypeOrderService {

    @Resource
    private TypeOrderMapper typeOrderMapper;
    @Override
    public TypeOrder getTypeOrderDetailById(Integer typeOrderId) {
        return typeOrderMapper.selectById(typeOrderId);
    }


    @Override
    public ResponseResult insertTypeOrder(TypeOrder typeOrder) {
        int inserted = typeOrderMapper.insert(typeOrder);
        if (inserted == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TYPE_ORDER_FAILED);
        } else if (inserted == 1) {
            return ResponseResult.success(typeOrder.getTypeId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.TYPE_ORDER_ERROR);
    }

    @Override
    public List<TypeOrder> getTypeOrderList(TypeOrderVO typeOrderVO) {
        LambdaQueryWrapper<TypeOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();

        lambdaQueryWrapper
                .like(typeOrderVO.getTypeName()!=null,TypeOrder::getTypeName,typeOrderVO.getTypeName())
                .ge(typeOrderVO.getDepositStart()!=0.0,TypeOrder::getDeposit,typeOrderVO.getDepositStart())
                .le(typeOrderVO.getDepositEnd()!=0.0,TypeOrder::getDeposit,typeOrderVO.getDepositEnd())
                .ge(typeOrderVO.getFinalPaymentStart()!=0.0,TypeOrder::getFinalPayment,typeOrderVO.getFinalPaymentStart())
                .le(typeOrderVO.getFinalPaymentEnd()!=0.0,TypeOrder::getFinalPayment,typeOrderVO.getFinalPaymentEnd())
                .ge(typeOrderVO.getCreateTimeStart()!=null,TypeOrder::getCreateTime,typeOrderVO.getCreateTimeStart())
                .le(typeOrderVO.getCreateTimeEnd()!=null,TypeOrder::getCreateTime,typeOrderVO.getCreateTimeEnd());

        return  this.typeOrderMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public ResponseResult updateTypeOrderDetail(TypeOrder typeOrder) {
        LambdaUpdateWrapper<TypeOrder> lambdaUpdateWrapper =new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .eq(TypeOrder::getTypeId,typeOrder.getTypeId())
                .set(typeOrder.getTypeName()!=null,TypeOrder::getTypeName,typeOrder.getTypeName())
                .set(typeOrder.getDeposit()!=0.0,TypeOrder::getDeposit,typeOrder.getDeposit())
                .set(typeOrder.getFinalPayment()!=0.0,TypeOrder::getFinalPayment,typeOrder.getFinalPayment())
                .set(typeOrder.getRemark()!=null,TypeOrder::getRemark,typeOrder.getRemark());
        int updated = typeOrderMapper.update(typeOrder, lambdaUpdateWrapper);
        return null;
    }

    @Override
    public ResponseResult delectTypeOrder(Integer typeOrderId) {

        int deleted = this.typeOrderMapper.deleteById(typeOrderId);
        return null;
    }

}
