package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Head;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.mapper.HeadMapper;
import com.example.express.service.HeadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Kyle
 * @Date 2024/4/1 23:13
 * @Version 1.0
 */

@Service
public class HeadServiceImpl implements HeadService {

    @Resource
    private HeadMapper headMapper;
    @Override
    public Head getHeadDetailById(Long headId) {
        return headMapper.selectById(headId);
    }

    @Override
    public Head getHeadDetailByHeadname(String name) {
        if (name == null) {
            return null;
        }
        LambdaQueryWrapper<Head> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.like(Head::getHeadName,name);
        return headMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Head getHeadByHeadnameAndFen(String name,String fen) {
        if (name == null || fen == null ) {
            return null;
        }
        LambdaQueryWrapper<Head> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.like(Head::getHeadName,name)
                .eq(Head::getFen,fen);
        return headMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public ResponseResult insertHead(Head head) {
        int inserted = headMapper.insert(head);
        if (inserted == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_FAILED);
        } else if (inserted == 1) {
            return ResponseResult.success(head.getHeadId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_ERROR);
    }

    @Override
    public List<Head> getHeadList(Head head) {
        LambdaQueryWrapper<Head> lambdaQueryWrapper = Wrappers.lambdaQuery();

        lambdaQueryWrapper
                .like(head.getHeadName()!=null,Head::getHeadName,head.getHeadName())
                .eq(head.getFen()!=null,Head::getFen,head.getFen());
//                .eq(head.getSkin()!=null,Head::getSkin,head.getSkin());

        return  this.headMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public ResponseResult updateHeadDetail(Head head) {
        LambdaUpdateWrapper<Head> lambdaUpdateWrapper =new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .eq(Head::getHeadId,head.getHeadId())
                .set(head.getHeadName()!=null,Head::getHeadName,head.getHeadName())
                .set(head.getFen()!=null,Head::getFen,head.getFen());
//                .set(head.getSkin()!=null,Head::getSkin,head.getSkin());
        int updated = headMapper.update(head, lambdaUpdateWrapper);
        if (updated == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_FAILED);
        } else if (updated == 1) {
            return ResponseResult.success(head.getHeadId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_ERROR);
    }

    @Override
    public ResponseResult delectHead(Integer headId) {

        int deleted = this.headMapper.deleteById(headId);
        if (deleted == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_FAILED);
        } else if (deleted == 1) {
            return ResponseResult.success(headId);
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.HEAD_ERROR);
    }
    
}
