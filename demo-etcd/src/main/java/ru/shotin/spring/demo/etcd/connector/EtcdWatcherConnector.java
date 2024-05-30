package ru.shotin.spring.demo.etcd.connector;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.Watch;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Optional;

@Component
@Slf4j
public class EtcdWatcherConnector {
    private String etcdEndpoints = EtcdConfig.ETCD_HOST;
    private String certBasePath = "E:\\git\\spring-tutorial\\demo-etcd\\src\\main\\resources\\cert\\";
    private long etcdRequestTimeout = 10000;

    private Client client = null;

    private String sharedKey = "test_key";

    @PostConstruct
    public void workWithEtcdWatchClient() {
        etcdClient().ifPresent(etcdClient -> {
            try {
                Watch watchClient = etcdClient.getWatchClient();
                ByteSequence key = ByteSequence.from(sharedKey.getBytes());
                watchClient.watch(key,
                        watchResponse -> {
                            log.info("watchResponse={}", watchResponse);
                        },
                        throwable -> {
                            log.error("watchError=", throwable.toString());
                        });
            } catch (RuntimeException ex) {
                log.error("workWithEtcdWatchClient() Failed working with etcdClient", ex);
            }
        });
    }

    public synchronized Optional<Client> etcdClient() {

        try {
            if (this.client == null) {
                ClientBuilder clientBuilder = Client.builder()
                        .endpoints(etcdEndpoints);
                if (etcdEndpoints.startsWith("https://")) {
                    File trustCertStoreCollectionFile = new File(certBasePath + "intermediate_ca_chain_cert.pem");
                    File keyCertChainFile = new File(certBasePath + "client_cert_chain.pem");
                    File keyFile = new File(certBasePath + "client_key.pem");
                    SslContext sslContext = GrpcSslContexts.forClient()
                            .trustManager(trustCertStoreCollectionFile)
                            .keyManager(keyCertChainFile, keyFile)
                            .build();
                    clientBuilder = clientBuilder.sslContext(sslContext);
                }
                this.client = clientBuilder.build();
            }
            return Optional.of(this.client);
        } catch (RuntimeException | SSLException ex) {
            log.error("Failed to create ETCD client", ex);
            return Optional.empty();
        }
    }
}
