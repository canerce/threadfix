////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.cli;

import com.denimgroup.threadfix.CollectionUtils;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.orm.hibernate3.support.IdTransferringMergeEventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mcollins on 6/24/15.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = false)
public class SpringConfigurationJDBCProperties {

    private static AnnotationConfigApplicationContext context = null;

    public static AnnotationConfigApplicationContext getContext() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext();
            context.register(SpringConfigurationJDBCProperties.class);
            context.scan("com.denimgroup.threadfix");
            context.setClassLoader(SpringConfigurationJDBCProperties.class.getClassLoader());
            context.refresh();
        }
        return context;
    }

    public static <T> T getSpringBean(Class<T> targetClass) {
        return getContext().getBean(targetClass);
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();

        Properties properties = getJDBCProperties();

        ds.setUrl(properties.getProperty("jdbc.url"));
        ds.setUsername(properties.getProperty("jdbc.username"));
        ds.setPassword(properties.getProperty("jdbc.password"));
        String driverClassName = properties.getProperty("jdbc.driverClassName");
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    private Properties getJDBCProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("jdbc.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't find jdbc.properties. Make sure it's available in the same directory you run java -jar from.");
        }

        return properties;
    }

    @Bean
    public PropertiesFactoryBean propertiesFactoryBean() {
        final PropertiesFactoryBean bean = new PropertiesFactoryBean();

        bean.setProperties(getJDBCProperties());

        return bean;
    }

    @Bean
    public AnnotationSessionFactoryBean annotationSessionFactoryBean() {
        AnnotationSessionFactoryBean bean = new AnnotationSessionFactoryBean();

        bean.setDataSource(dataSource());
        bean.setPackagesToScan("com.denimgroup.threadfix.data.entities");
        bean.setHibernateProperties(getJDBCProperties());
        Map<String, Object> merge = CollectionUtils.map("merge", (Object) new IdTransferringMergeEventListener());
        bean.setEventListeners(merge);

        return bean;
    }

    @Bean
    public HibernateTransactionManager getHibernateTransactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(annotationSessionFactoryBean().getObject());
        return hibernateTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor getPersistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}

