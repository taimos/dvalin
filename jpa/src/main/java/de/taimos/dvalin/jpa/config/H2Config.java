package de.taimos.dvalin.jpa.config;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "H2")
public class H2Config {
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseFactoryBean factoryBean = new EmbeddedDatabaseFactoryBean();
		// randomize database name to get a new one each time
		factoryBean.setDatabaseName(UUID.randomUUID().toString());
		factoryBean.setDatabaseType(EmbeddedDatabaseType.H2);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
}
