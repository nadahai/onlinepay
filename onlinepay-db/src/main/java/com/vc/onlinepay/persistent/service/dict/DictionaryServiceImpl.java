/**
 * @类名称:OrderServiceImpl.java
 * @时间:2017年6月6日上午9:09:22
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.persistent.service.dict;


import com.vc.onlinepay.exception.OnlineDbException;
import com.vc.onlinepay.persistent.entity.dict.Dictionary;
import com.vc.onlinepay.persistent.mapper.dict.DictionaryMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @描述:系統字典接口实现
 * @作者:lihai
 * @时间:2017年6月6日 上午9:09:22
 */
@Service
@Transactional (readOnly = true,rollbackFor = Exception.class)
public class DictionaryServiceImpl {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    /**
     * @描述:根据key获取字典信息
     * @作者:nada
     * @时间:2018/12/11
     **/
    public Dictionary findDictionaryByKey (Dictionary entity) throws OnlineDbException {
        return dictionaryMapper.findByKey (entity);
    }

    /**
     * @描述:授权IP字典
     * @作者:nada
     * @时间:2018/12/11
     **/
    public Dictionary findAllowedAccessIp (Dictionary dictionary) throws  OnlineDbException{
        List<Dictionary> list = dictionaryMapper.findAccessIp (dictionary);
        if (null == list || list.size () < 1) {
            return null;
        }
        return list.get (0);
    }

}

