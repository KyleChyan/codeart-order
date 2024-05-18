package com.example.express.service;


import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Client;

import java.util.List;

public interface ClientService {

    /**
     * 根据id获取客户
     *
     * @param clientId
     * @return
     */
    Client getClientDetailById(Long clientId);

    /**
     * 根据昵称获取客户
     *
     * @param clientNickname
     * @return
     */
    Client getClientDetailByNickname(String clientNickname);

    /**
     * 新增客户
     *
     * @param client
     * @return
     */
    ResponseResult insertClient(Client client);

    /**
     * 查找客户列表
     *
     * @param client
     * @return
     */
    List<Client> getClientList(Client client);

    /**
     * 更新客户信息
     *
     * @param client
     * @return
     */
    ResponseResult updateClientDetail(Client client);

    /**
     * 删除客户信息（逻辑删除）
     *
     * @param clientId
     * @return
     */
    ResponseResult delectClient(Integer clientId);
}
