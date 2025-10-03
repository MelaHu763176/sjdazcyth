package priv.muller.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Knife4jConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("数据档案一体化 API")
                        .version("1.0")
                        .description("AI网盘系统")
                        .termsOfService("http://192.168.19.152")
                        .license(new License().name("Apache 2.0").url("http://192.168.19.152"))
                        // 添加作者信息
                        .contact(new Contact()
                                .name("muller") // 替换为作者的名字
                                .email("972736870@qq.com") // 替换为作者的电子邮件
                                .url("http://192.168.19.152") // 替换为作者的网站或个人资料链接
                        )
                ) ;
    }

}