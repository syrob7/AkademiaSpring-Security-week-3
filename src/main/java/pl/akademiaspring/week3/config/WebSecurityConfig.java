package pl.akademiaspring.week3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private UserDetailsService userDetailsService;

    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/helloAdmin").hasRole("ADMIN")
                .antMatchers("/helloUser").hasAnyRole( "ADMIN", "USER")
                .antMatchers("/helloAnonymous").hasAnyRole("ANONYMOUS", "ADMIN", "USER")
                .antMatchers("/forAll").hasAnyRole("ANONYMOUS", "ADMIN", "USER")
                .antMatchers("/singup").permitAll()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/helloUser").failureUrl("/login-error").permitAll()
                .and()
                .logout().logoutSuccessUrl("/forAll")
                .and()
                .rememberMe().tokenValiditySeconds(86400).rememberMeCookieName("rehresh").rememberMeParameter("remember");
    }
}
