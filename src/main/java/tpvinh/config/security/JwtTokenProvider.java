package tpvinh.config.security;

import io.jsonwebtoken.*;
import tpvinh.config.util.CookieUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String tokenHeader;
    private String tokenSecret;
    private long tokenExpirationMsec;;

    private String rememberMeKey;
    private String cookieSecret;

    public JwtTokenProvider(AuthProperties authProperties) {
    	this.tokenHeader = authProperties.getAuth().getTokenHeader();
        this.tokenSecret = authProperties.getAuth().getTokenSecret();
        this.tokenExpirationMsec = authProperties.getAuth().getTokenExpirationMsec();
        this.rememberMeKey = authProperties.getCookie().getRememberMeKey();
        this.cookieSecret = authProperties.getCookie().getCookieSecret();
    }

    /**
     * Jwt with limited time default
     *
     * @param authentication
     * @return
     */
    public String generateTokenDefault(Authentication authentication) {

        AccountPrincipalImpl accountPrincipalImpl = (AccountPrincipalImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        return Jwts.builder()
                .setSubject(String.valueOf(accountPrincipalImpl.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    /**
     * Jwt with limited time use for Cookie
     *
     * @param authentication
     * @return
     */
    public String generateTokenExp(Authentication authentication, int numberOfDays) {

        LocalDate localDate = LocalDate.now().plusDays(numberOfDays);

        AccountPrincipalImpl accountPrincipalImpl = (AccountPrincipalImpl) authentication.getPrincipal();
        Date expiryDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(String.valueOf(accountPrincipalImpl.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    /**
     * Jwt with unlimited time use for HtppSession
     *
     * @param authentication
     * @return
     */
    public String generateTokenPrivate(Authentication authentication) {

        AccountPrincipalImpl accountPrincipalImpl = (AccountPrincipalImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(String.valueOf(accountPrincipalImpl.getId()))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    /**
     * Private jwt with payload
     *
     * @param authentication
     * @param contextPath
     * @return
     */
    public String generateTokenPrivate(Authentication authentication, String payload) {

        AccountPrincipalImpl accountPrincipalImpl = (AccountPrincipalImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(String.valueOf(accountPrincipalImpl.getId()))
                .claim("payload", payload)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    public Long getSubjectFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public String getPayloadFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("payload");
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getJwtFromRequest(HttpServletRequest request, HttpServletResponse response) {
        // get jwt from header
        String bearerToken = request.getHeader(tokenHeader);

        if (StringUtils.isEmpty(bearerToken)) {
            // get jwt from session
            HttpSession httpSession = request.getSession(true);
            bearerToken = (httpSession != null && httpSession.getAttribute(tokenHeader) != null) ? (String) httpSession.getAttribute(tokenHeader) : null;
        }
        if (StringUtils.isEmpty(bearerToken)) {
            // get jwt from cookie
            String rememberMeCoookie = CookieUtil.getValue(request, rememberMeKey);
            bearerToken = StringUtils.isNotEmpty(rememberMeCoookie) ? AES.decrypt(rememberMeCoookie, cookieSecret) : null;
        }
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        if (StringUtils.isNotEmpty(bearerToken)) {
            return StringUtils.join("Bearer ",bearerToken);
        }
        return StringUtils.EMPTY;
    }
}