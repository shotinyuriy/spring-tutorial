package ru.shotin.spring.demo.etcd.connector;

public class EtcdConfig {
    public static final String ETCD_HOST = "http://"+System.getenv("ETCD_HOST")+":2379";
}
