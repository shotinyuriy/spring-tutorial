package ru.shotin.spring.demo.project1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public enum TarantoolConfigEnum {
    TARANTOOL_ONE("tarantool-one"),
    TARANTOOL_TWO("tarantool-two");

    private final String propertyPrefix;
    private String host;
    private int port;
    private String username;
    private String password;

    private TarantoolConfigEnum(final String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    public String getPropertyPrefix() {
        return this.propertyPrefix;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Component
    private static class TarantoolPropertyInjector {
        final Environment environment;

        @Autowired
        public TarantoolPropertyInjector(final Environment environment) {
            this.environment = environment;
        }

        @PostConstruct
        public void postConstruct() {
            TarantoolConfigEnum[] var1 = TarantoolConfigEnum.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                TarantoolConfigEnum config = var1[var3];
                String propertyPrefix = config.propertyPrefix;
                config.host = this.environment.getProperty(String.format("%s.%s", propertyPrefix, "host"));
                config.port = Integer.valueOf(this.environment.getProperty(String.format("%s.%s", propertyPrefix, "port")));
                config.username = this.environment.getProperty(String.format("%s.%s", propertyPrefix, "username"));
                config.password = this.environment.getProperty(String.format("%s.%s", propertyPrefix, "password"));
            }

        }
    }
}

