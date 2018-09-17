package com.noknown.framework.security.service.impl;

import com.noknown.framework.common.base.BaseServiceImpl;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.ObjectUtil;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> BaseUserDetails的实现类
 * @author guodong
 */
public abstract class AbstractUseDetailsrServiceImpl<T extends BaseUserDetails> extends BaseServiceImpl<T, Integer> implements UserDetailsService<T> {

	@Value("${security.auth.email:true}")
	private boolean emailAuth;

	@Value("${security.auth.mobile:true}")
	private boolean mobileAuth;

	@Override
	public BaseUserDetails updateUserDetails(BaseUserDetails ud) throws ServiceException {
		BaseUserDetails udDetails = getRepository().getOne(ud.getId());
		if (udDetails == null) {
			throw new ServiceException("用户不存在");
		}

		if (super.clazz.isInstance(udDetails)) {
			List<String> ignore = new ArrayList<>();
			if (emailAuth) {
				ignore.add("email");
			}
			if (mobileAuth) {
				ignore.add("mobile");
			}
			T tUd = (T) udDetails;
			ObjectUtil.copy(tUd, ud, ignore);
			getRepository().save(tUd);
			return tUd;
		} else {
			throw new ServiceException("不支持该用户类型");
		}
	}

	@Override
	public BaseUserDetails getUserDetail(Integer id) {
		return getRepository().findById(id).orElse(null);
	}


}
