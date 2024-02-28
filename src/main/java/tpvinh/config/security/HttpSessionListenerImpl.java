package tpvinh.config.security;

import java.util.Date;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpSessionListenerImpl implements HttpSessionListener {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        logger.info("JSESSIONID {}: has created at : {}", sessionEvent.getSession().getId(), new Date());
        sessionEvent.getSession().setMaxInactiveInterval(3600);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        logger.info("JSESSIONID {}: has destroyed at : {}", httpSessionEvent.getSession().getId(), new Date());
    }
}
