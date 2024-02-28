package tpvinh.service;

import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tpvinh.config.util.CookieUtil;
import tpvinh.constant.RoleAdminEnum;
import tpvinh.mybatis.mapper.AuthMapper;
import tpvinh.mybatis.model.AccountVO;

@Service
public class AuthService {

    @Autowired
    private AuthMapper authMapper;

    public void clearAuthentication(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null)
            session.removeAttribute("Authorization");
        CookieUtil.clear(response, "JSESSIONID");
        CookieUtil.clear(response, "remember-me");
    }

    public AccountVO selectAccount(String id, String loginId) {
        return authMapper.selectAccount(id, loginId);
    }

    public boolean isAdminRole(String roleName) {
        return Arrays.stream(RoleAdminEnum.values()).anyMatch((t) -> t.name().equals(roleName));
    }

    public boolean isAccountlDisabled(String loginId) {
        AccountVO account = authMapper.selectAccount(null, loginId);
        return (Objects.nonNull(account) && !account.isDeleted() && !account.isEnabled());
    }
}
