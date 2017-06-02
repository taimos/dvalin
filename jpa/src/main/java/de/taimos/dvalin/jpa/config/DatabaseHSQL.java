package de.taimos.dvalin.jpa.config;

/*-
 * #%L
 * JPA support for dvalin using Hibernate
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import java.util.UUID;


@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "HSQL")
public class DatabaseHSQL {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseFactoryBean factoryBean = new EmbeddedDatabaseFactoryBean();
        // randomize database name to get a new one each time
        factoryBean.setDatabaseName(UUID.randomUUID().toString());
        factoryBean.setDatabaseType(EmbeddedDatabaseType.HSQL);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
