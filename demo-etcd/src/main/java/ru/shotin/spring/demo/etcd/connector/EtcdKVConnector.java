package ru.shotin.spring.demo.etcd.connector;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class EtcdKVConnector {
    private String etcdEndpoints = EtcdConfig.ETCD_HOST;
    private String certBasePath = "E:\\git\\spring-tutorial\\demo-etcd\\src\\main\\resources\\cert\\";
    private long etcdRequestTimeout = 10000;

    private Client client = null;

    private String sharedKey = "si-1680-consumer-expected-source";

    @PostConstruct
    public void tarantoolFlag() {
        etcdClient().ifPresent(etcdClient -> {
            try {
                KV kvClient = etcdClient.getKVClient();
                ByteSequence key = ByteSequence.from(sharedKey.getBytes());
                ByteSequence value = ByteSequence.from("primary".getBytes());
                CompletableFuture<PutResponse> putResponseFuture = kvClient.put(key, value);
                PutResponse putResponse = putResponseFuture.get(etcdRequestTimeout, TimeUnit.MILLISECONDS); // TimeOut settings are required to avoid hanging
                log.info("putResponse={}", putResponse);
                CompletableFuture<GetResponse> getResponseFuture = kvClient.get(key);
                GetResponse getResponse = getResponseFuture.get(etcdRequestTimeout, TimeUnit.MILLISECONDS); // TimeOut settings are required to avoid hanging
                log.info("getResponse={}", getResponse);

            } catch (InterruptedException ex) {
                log.error("workWithEtcdKvClient() Failed working with etcdClient", ex);
                Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException ex) {
                log.error("workWithEtcdKvClient() Failed working asynchronously with etcdClient", ex);
            } catch (RuntimeException ex) {
                log.error("workWithEtcdKvClient() Failed working with etcdClient", ex);
            }
        });
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void workWithEtcdKvClient() {
        etcdClient().ifPresent(etcdClient -> {
            try {
                KV kvClient = etcdClient.getKVClient();
                ByteSequence key = ByteSequence.from(sharedKey.getBytes());

                CompletableFuture<GetResponse> getResponseFuture = kvClient.get(key);
                GetResponse getResponse = getResponseFuture.get(etcdRequestTimeout, TimeUnit.MILLISECONDS); // TimeOut settings are required to avoid hanging
                log.debug("getResponse={}", getResponse);
                if (getResponse.getCount() > 0) {
                    String value = new String(getResponse.getKvs().get(0).getValue().getBytes());
                    String newValue;
                    if ("primary".equals(value)) {
                        newValue = "stand-in";
                    } else {
                        newValue = "primary";
                    }
                    log.info("newValue={}", newValue);
                    ByteSequence newValueBytes = ByteSequence.from(value.getBytes());
                    CompletableFuture<PutResponse> putResponseFuture = kvClient.put(key, newValueBytes);
                    PutResponse putResponse = putResponseFuture.get(etcdRequestTimeout, TimeUnit.MILLISECONDS); // TimeOut settings are required to avoid hanging
                    log.debug("putResponse={}", putResponse);
                }
            } catch (InterruptedException ex) {
                log.error("workWithEtcdKvClient() Failed working with etcdClient", ex);
                Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException ex) {
                log.error("workWithEtcdKvClient() Failed working asynchronously with etcdClient", ex);
            } catch (RuntimeException ex) {
                log.error("workWithEtcdKvClient() Failed working with etcdClient", ex);
            }
        });
    }

//    @Scheduled(initialDelay = 6000, fixedDelay = 10000)
    public void workWithEtcdKvDelete() {
        etcdClient().ifPresent(etcdClient -> {
            try {
                KV kvClient = etcdClient.getKVClient();
                ByteSequence key = ByteSequence.from(sharedKey.getBytes());
                CompletableFuture<DeleteResponse> deleteResponseFuture = kvClient.delete(key);
                DeleteResponse deleteResponse = deleteResponseFuture.get(etcdRequestTimeout, TimeUnit.MILLISECONDS); // TimeOut settings are required to avoid hanging
                log.debug("deleteResponse={}", deleteResponse);
            } catch (InterruptedException ex) {
                log.error("workWithEtcdKvDelete() Failed working with etcdClient", ex);
                Thread.currentThread().interrupt();
            } catch (ExecutionException | TimeoutException ex) {
                log.error("workWithEtcdKvDelete() Failed working asynchronously with etcdClient", ex);
            } catch (RuntimeException ex) {
                log.error("workWithEtcdKvDelete() Failed working with etcdClient", ex);
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
