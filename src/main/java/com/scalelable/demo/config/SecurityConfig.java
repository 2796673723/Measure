package com.scalelable.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.sql.DataSource;
import java.util.Collection;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence charSequence) {
            return charSequence.toString();
        }

        @Override
        public boolean matches(CharSequence charSequence, String s) {
            return s.equals(charSequence.toString());
        }
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String userSQL = "select username,password,valid from t_user where username = ?";
        String authoritySQL = "select u.username,a.authority from t_user u,t_authority a," +
                "t_user_authority ua where ua.user_id=u.id " +
                "and ua.authority_id=a.id and u.username =?";

        auth.jdbcAuthentication()
                .passwordEncoder(passwordEncoder)
                .dataSource(dataSource)
                .usersByUsernameQuery(userSQL)
                .authoritiesByUsernameQuery(authoritySQL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/userLogin**").permitAll()
                .antMatchers("/lib/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/client/**").permitAll()
                .antMatchers("/root/**").hasRole("root")
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/manager/**").hasRole("manager")
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable();
        http.formLogin()
                .loginPage("/userLogin").permitAll()
                .usernameParameter("username").passwordParameter("password")
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                    SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    boolean isRoot = authorities.contains(new SimpleGrantedAuthority("ROLE_root"));
                    if (isRoot) {
                        httpServletResponse.sendRedirect("./root");
                    } else {
                        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_admin"));
                        if (isAdmin) {
                            httpServletResponse.sendRedirect("./admin");
                        } else {
                            httpServletResponse.sendRedirect("./manager");
                        }
                    }

                })
                .failureHandler((httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendRedirect("./userLogin?error&msg=" + e.getMessage()));

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/");
    }
}
