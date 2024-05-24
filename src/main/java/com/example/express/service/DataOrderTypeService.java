package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.bean.DataOrderType;

import java.util.List;

public interface DataOrderTypeService extends IService<DataOrderType> {
    List<DataOrderType> listAll();

    List<DataOrderType> listAllByCache();

    DataOrderType getByCache(Integer id);
}
