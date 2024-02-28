package tpvinh.config.security;


import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource("classpath:auth.properties")
@ConfigurationProperties(prefix = "app")
@Data
public class AuthProperties {

    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final Cookie cookie = new Cookie();

    @Data
    public static class Auth {
    	private String tokenHeader;
    	private String loginPageUrl;
    	private String loginApiUrl;
        private String logoutUrl;

        private String tokenSecret;
        private long tokenExpirationMsec;
    }
    @Data
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }
    @Data
    public static class Cookie {
    	private String rememberMeKey;
        private String cookieSecret;
    }
}
