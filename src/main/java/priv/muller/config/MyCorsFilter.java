package priv.muller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MyCorsFilter {
    @Bean
    public CorsFilter corsFilter(){
        // 创建CORS配置对象
        CorsConfiguration config =new CorsConfiguration();
        //允许访问的域名
        config.addAllowedOriginPattern("*");
        //允许访问的方法
        config.setAllowCredentials(true);
        //允许访问的方法
        config.addAllowedMethod("*");
        //允许访问的头信息
        config.addAllowedHeader("*");
        //暴露哪些头部信息，因为跨域访问默认不能获取全部头部信息
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource corsConfigurationSource =new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",config);
        return new CorsFilter(corsConfigurationSource);

    }
}
