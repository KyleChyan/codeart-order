package com.example.express.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Client;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.mapper.ClientMapper;
import com.example.express.service.ClientService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    @Resource
    private ClientMapper clientMapper;
    @Override
    public Client getClientDetailById(Long clientId) {
        return clientMapper.selectById(clientId);
    }

    @Override
    public Client getClientDetailByNickname(String clientNickname) {
        if (clientNickname == null) {
            return null;
        }
        LambdaQueryWrapper<Client> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.like(Client::getClientNickname,clientNickname);
        return clientMapper.selectOne(lambdaQueryWrapper);
    }


    @Override
    public ResponseResult insertClient(Client client) {
        int inserted = clientMapper.insert(client);
        if (inserted == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
        } else if (inserted == 1) {
            return ResponseResult.success(client.getClientId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_FAILED);
    }

    @Override
    public List<Client> getClientList(Client client) {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (client.getIsDeleted() == 0) {
            return null;
        }
        lambdaQueryWrapper
                .like(client.getDeliverName()!=null,Client::getDeliverName,client.getDeliverName())
                .like(client.getDeliverAddress()!=null,Client::getDeliverAddress,client.getDeliverAddress())
                .like(client.getClientNickname()!=null,Client::getClientNickname,client.getClientNickname())
                .eq(client.getDeliverPhone()!=null&&client.getDeliverPhone().length()==11,Client::getDeliverPhone,client.getDeliverPhone())
                .likeLeft(client.getDeliverPhone()!=null&&client.getDeliverPhone().length()==4,Client::getDeliverPhone,client.getDeliverPhone());

        return  this.clientMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public ResponseResult updateClientDetail(Client client) {

        int updated = clientMapper.updateById(client);
        if (updated == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
        } else if (updated == 1) {
            return ResponseResult.success(client.getClientId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_FAILED);
    }

    @Override
    public ResponseResult delectClient(Integer clientId) {
        Client client = this.clientMapper.selectById(clientId);
        LambdaUpdateWrapper<Client> lambdaUpdateWrapper =new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .eq(Client::getClientId,client.getClientId())
                .set(Client::getIsDeleted,0);
        int deleted = clientMapper.update(client, lambdaUpdateWrapper);
        if (deleted == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_ERROR);
        } else if (deleted == 1) {
            return ResponseResult.success(client.getClientId());
        }else
            return ResponseResult.failure(ResponseErrorCodeEnum.CLIENT_FAILED);
    }
}
