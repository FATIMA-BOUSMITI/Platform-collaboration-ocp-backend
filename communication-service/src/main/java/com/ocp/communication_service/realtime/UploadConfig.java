package com.ocp.communication_service.realtime;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;
@Configuration public class UploadConfig {
 @Bean MultipartConfigElement multipartConfigElement(){var factory=new MultipartConfigFactory();factory.setMaxFileSize(DataSize.ofMegabytes(10));factory.setMaxRequestSize(DataSize.ofMegabytes(12));return factory.createMultipartConfig();}
}
