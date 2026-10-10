package in.kajalCoder.ContactSphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import in.kajalCoder.ContactSphere.security.AuthFailureHandler;
import in.kajalCoder.ContactSphere.security.AuthLogoutSuccessHandler;
import in.kajalCoder.ContactSphere.security.AuthSuccessHandler;
import in.kajalCoder.ContactSphere.security.CustomUserDetailsService;
import in.kajalCoder.ContactSphere.security.OAuth2LoginSuccessHandler;

@Configuration
public class SecurityConfig {

    private AuthFailureHandler authFailureHandler;

    private AuthSuccessHandler authSuccessHandler;

    private AuthLogoutSuccessHandler authLogoutSuccessHandler;

    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(
            AuthFailureHandler authFailureHandler,
            AuthLogoutSuccessHandler authLogoutSuccessHandler,
            AuthSuccessHandler authSuccessHandler,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {

        this.authFailureHandler = authFailureHandler;
        this.authLogoutSuccessHandler = authLogoutSuccessHandler;
        this.authSuccessHandler = authSuccessHandler;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    // Authentication Provider

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;

    }

    // SecurityFilter Chain

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity.authorizeHttpRequests(auth -> auth
                // Logged-in users only
                .requestMatchers("/users/**").authenticated()
                .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/login")
                        // .defaultSuccessUrl("/users", true)
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)
                        .permitAll())

                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(oAuth2LoginSuccessHandler)

                        // .successHandler((request, response, authentication) -> {
                        // response.sendRedirect("/users");
                        // })
                        .failureHandler((request, response, exception) -> {

                            System.out.println("===== GOOGLE OAUTH2 LOGIN FAILED =====");
                            System.out.println("Exception: " + exception);
                            exception.printStackTrace();

                            response.sendRedirect("/login?oauth2Error=true");
                        }))

                .logout(logout -> logout
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .permitAll());

        return httpSecurity.build();

    }

}
