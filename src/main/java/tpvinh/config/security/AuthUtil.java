package tpvinh.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import tpvinh.mybatis.model.AccountVO;

public class AuthUtil {

    static Authentication authentication;

    static {
        authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    public static AccountVO crrAcc() {
        if(!isAuthenticated()) {
            return null;
        }
        AccountPrincipalImpl principal = (AccountPrincipalImpl) authentication.getPrincipal();
        return principal.getAccount();
    }
}
