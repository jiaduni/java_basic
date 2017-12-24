package spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * describe :
 * Created by jiadu on 2017/10/22 0022.
 */
@Configuration
@EnableAspectJAutoProxy //用于开启自动aop,xml的话需要单独配置
@ComponentScan
public class JavaConfig {}
