package com.noknown.framework.security.authentication.filter;

import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.Constants;
import com.noknown.framework.security.authentication.SureAuthenticationInfo;
import com.noknown.framework.security.authentication.util.JwtTokenUtil;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserDetailsService;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guodong
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	UserService userService;


	/**
	 * 使用时注入
	 */
	@Autowired
	private UserDetailsService udService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${security.jwt.header:jwtheader}")
	private String tokenHeader;

	@Value("${security.jwt.tokenHead:common}")
	private String tokenHead;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(this.tokenHeader);
		if (authHeader != null && authHeader.startsWith(tokenHead)) {
			final String authToken = authHeader.substring(tokenHead.length());
			User user = jwtTokenUtil.getUserFromToken(authToken);
			HttpSession session = request.getSession();
			if (user != null) {
				if (jwtTokenUtil.validateToken(authToken, user)) {
					user.setAuthenticated(true);

					List<GrantedAuthority> gaList = new ArrayList<>();
					BaseUserDetails ud = null;
					SureAuthenticationInfo saInfo;
					try {
						ud = udService.getUserDetail(user.getId());


						List<Role> roleList = user.getRoles();
						if (roleList != null && roleList.size() > 0) {
							for (Role role : roleList) {
								GrantedAuthority ga = new SimpleGrantedAuthority(role.getName());
								gaList.add(ga);
							}
						}
						saInfo = new SureAuthenticationInfo(SureAuthenticationInfo.AUTH_TYPE_UP, user, ud, roleList, gaList);
						saInfo.setPrincipal(user.getId());
						saInfo.setCredentials(user.getPassword());
						List<ThirdPartyAccount> tpaList = userService.getThirdPartyList(user.getId());
						saInfo.setTpaList(tpaList);
						SecurityContextHolder.getContext().setAuthentication(saInfo);
						session.setAttribute(Constants.SURE_LOGIN_INFO, saInfo);
						session.setAttribute(Constants.SURE_LOGIN_USER_NAME, saInfo.getUd().getFullName());
						session.setAttribute(Constants.SURE_LOGIN_USER_ID, saInfo.getUser().getId());
						session.setAttribute(Constants.SURE_LOGIN_USER, saInfo.getUser());
						session.setAttribute(Constants.SURE_LOGIN_USER_DETAIL, saInfo.getUd());
						session.setAttribute(Constants.SURE_LOGIN_USER_ROLES, saInfo.getRoles());


					} catch (ServiceException | DaoException e) {
						e.printStackTrace();
						clear(session);
					}
				} else {
					clear(session);
				}
			}
		}

		chain.doFilter(request, response);
	}

	private void clear(HttpSession session){
		SecurityContextHolder.getContext().setAuthentication(null);
		session.removeAttribute(Constants.SURE_LOGIN_INFO);
		session.removeAttribute(Constants.SURE_LOGIN_USER_NAME);
		session.removeAttribute(Constants.SURE_LOGIN_USER_ID);
		session.removeAttribute(Constants.SURE_LOGIN_USER);
		session.removeAttribute(Constants.SURE_LOGIN_USER_DETAIL);
		session.removeAttribute(Constants.SURE_LOGIN_USER_ROLES);
	}

}
