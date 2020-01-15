/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.dict;

import com.vc.onlinepay.persistent.entity.dict.Dict;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 字典持久化接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@Repository
public interface DictMapper {

	/**
	 * @描述:根据类型获取系统字典列表
	 * @作者:nada
	 * @时间:2018/12/7
	 **/
	List<Dict> findTypeList(Dict dict);
	
	/**
	 * @描述:获取代付提现配置列表
	 * @作者:nada
	 * @时间:2017年12月15日 下午5:30:22
	 */
	List<Dict> findCashConfig(Dict dict);
	
}
