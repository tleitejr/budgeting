package dio.budgeting.infra.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApiSecurityConfiguration {

  @Bean
  @ConditionalOnExpression("'${spring.security.oauth2.resourceserver.jwt.issuer-uri:}' != ''")
  SecurityFilterChain authenticatedApi(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
      .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
      .build();
  }

  @Bean
  @ConditionalOnExpression("'${spring.security.oauth2.resourceserver.jwt.issuer-uri:}' == ''")
  SecurityFilterChain denyRequestsWithoutIssuer(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize.anyRequest().denyAll())
      .build();
  }
}