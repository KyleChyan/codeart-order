package com.example.express.service;



import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.Client;
import com.example.express.domain.bean.Head;

import java.util.List;

public interface HeadService {
    /**
     * 根据id获取娃头
     *
     * @param headId
     * @return
     */
    Head getHeadDetailById(Long headId);

    /**
     * 根据名称获取娃头
     *
     * @param name
     * @return
     */
    Head getHeadDetailByHeadname(String name);

    /**
     * 根据名称和几分获取娃头
     *
     * @param name
     * @return
     */
    Head getHeadByHeadnameAndFen(String name,String fen);

    /**
     * 新增娃头
     *
     * @param head
     * @return
     */
    ResponseResult insertHead(Head head);

    /**
     * 查找娃头列表
     *
     * @param head
     * @return
     */
    List<Head> getHeadList(Head head);

    /**
     * 更新娃头信息
     *
     * @param head
     * @return
     */
    ResponseResult updateHeadDetail(Head head);

    /**
     * 删除娃头信息（物理删除）
     *
     * @param headId
     * @return
     */
    ResponseResult delectHead(Integer headId);

}
