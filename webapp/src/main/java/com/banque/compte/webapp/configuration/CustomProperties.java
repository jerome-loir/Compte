package com.banque.compte.webapp.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="com.banque.compte.webapp.configuration")
public class CustomProperties {

	private String apiUrl;
}
