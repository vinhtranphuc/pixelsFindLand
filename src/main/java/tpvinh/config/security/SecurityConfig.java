package tpvinh.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;
    @Autowired
    private LogoutHandlerImpl logoutHandlerImpl;
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandlerImpl;
    @Autowired
    private AuthProperties authProperties;

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // cors session
        httpSecurity.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1);

        // disable csrf
        httpSecurity.csrf().disable();

        // httpBasic
        httpSecurity.httpBasic().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler);

        httpSecurity
                .authorizeRequests()
                .antMatchers("/assets/**", "/store/**")
                    .permitAll()
                .antMatchers(authProperties.getAuth().getLoginApiUrl())
                    .permitAll()
                .anyRequest()
                    .hasAnyRole("SUPER_ADMIN", "ADMIN");

        httpSecurity.formLogin().loginPage(authProperties.getAuth().getLoginPageUrl()).permitAll();

        httpSecurity.logout(
                logout -> logout.logoutUrl(authProperties.getAuth().getLogoutUrl()).addLogoutHandler(logoutHandlerImpl)
                        .invalidateHttpSession(true).logoutSuccessHandler(logoutSuccessHandlerImpl));

        // Add our custom JWT security filter
        httpSecurity.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        super.configure(webSecurity);
        webSecurity.ignoring().antMatchers(
                "/resources/**",
                "/v2/api-docs",
                "/webjars/springfox-swagger-ui/**",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/csrf",
                "/swagger-ui.html");
        webSecurity.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

}