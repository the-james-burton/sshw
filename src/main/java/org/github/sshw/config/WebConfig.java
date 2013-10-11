package org.github.sshw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.config.EnableWebSocket;
import org.springframework.web.socket.server.config.WebSocketConfigurer;
import org.springframework.web.socket.server.config.WebSocketHandlerRegistry;

import com.github.sshw.websocket.SSHSessionImpl;
import com.github.sshw.websocket.SSHWebSocketHandler;

@Configuration
@EnableWebMvc
@EnableWebSocket
@ComponentScan(basePackages={"com.github.sshw.rest"})
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer  {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(sshWebSocketHandler(), "/ssh");

    }

    @Bean
    public WebSocketHandler sshWebSocketHandler() {
        return new SSHWebSocketHandler(sshService());
    }


    @Bean
    public SSHSessionImpl sshService() {
        SSHSessionImpl service = new SSHSessionImpl();
        return service;
    }

    // Allow serving HTML files through the default Servlet

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * Since we don't have any controller logic, simpler to just
     * define controller for page using View Controller. Note:
     * had to extend WebMvcConfigurerAdapter to get this functionality
     * @param registry
     */
    //@Override
    //public void addViewControllers(ViewControllerRegistry registry) {
    //    registry.addViewController("/").setViewName("home");
    //}

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        //resolver.setPrefix("/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");

    }

}
