package com.rockapp.config;

import jakarta.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatOptionsBlocker() {
        return factory -> factory.addContextCustomizers(context -> {
            context.getPipeline().addValve(new ValveBase() {
                @Override
                public void invoke(Request request, Response response)
                        throws IOException, ServletException {

                    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                        // 强制重置连接（替代直接关闭Socket）
                        response.setHeader("Connection", "close");
                        response.sendError(500, "Connection forcibly closed");
                        response.flushBuffer();
                        throw new IOException("OPTIONS request blocked");
                    }
                    getNext().invoke(request, response);
                }
            });
        });
    }
}
