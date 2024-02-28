package tpvinh;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import tpvinh.config.security.AuthProperties;

@SpringBootApplication
@EntityScan(basePackageClasses = { PixelFindLandApplication.class, })
@EnableConfigurationProperties(AuthProperties.class)
@EnableScheduling
@ComponentScan
public class PixelFindLandApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixelFindLandApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
