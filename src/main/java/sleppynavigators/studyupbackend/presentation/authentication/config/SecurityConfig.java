package sleppynavigators.studyupbackend.presentation.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import sleppynavigators.studyupbackend.exception.BaseException;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.exception.network.ForbiddenException;
import sleppynavigators.studyupbackend.exception.network.UnAuthorizedException;
import sleppynavigators.studyupbackend.presentation.authentication.filter.AccessTokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final CorsConfigurationSource corsConfigurationSource;
    private final AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we only target mobile platforms, so we use self-contained bearer tokens
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // for development environments only
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // disable unnecessary security features
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // authorize requests
                .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/docs/**", "/auth/**", "/chat/**", "/ws/**").permitAll()
                                .anyRequest().authenticated())

                // handle exceptions
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler()));
        return httpSecurity.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            BaseException exception = new UnAuthorizedException();
            response.setStatus(exception.getStatus());
            objectMapper.writeValue(response.getWriter(),
                    new ErrorResponse(exception.getCode(), exception.getMessage(), request.getRequestURI()));
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json");
            BaseException exception = new ForbiddenException("Need more permissions");
            response.setStatus(exception.getStatus());
            objectMapper.writeValue(response.getWriter(),
                    new ErrorResponse(exception.getCode(), exception.getMessage(), request.getRequestURI()));
        };
    }
}
