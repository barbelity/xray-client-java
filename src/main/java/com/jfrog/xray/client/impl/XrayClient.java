package com.jfrog.xray.client.impl;

import com.jfrog.xray.client.Xray;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jfrog.client.http.HttpBuilderBase;
import org.jfrog.client.http.auth.PreemptiveAuthInterceptor;
import org.jfrog.client.http.model.ProxyConfig;
import org.jfrog.client.util.KeyStoreProvider;

/**
 * Created by romang on 2/2/17.
 */
public class XrayClient {

    private static final int DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = 300 * 1000;
    private static final String DEFAULT_USER_AGENT = "jfrog-xray-client/" + XrayClient.class.getPackage().getImplementationVersion();

    public static Xray create(CloseableHttpClient preConfiguredClient, String url) {
        return new XrayImpl(preConfiguredClient, url);
    }

    public static ClientBuilder newBuilder() {
        return new ClientBuilder();
    }

    public static class ClientBuilder {
        private String url;
        private String username;
        private String password;
        private String userAgent;
        private int timeout;
        private boolean noHostVerification;
        private KeyStoreProvider keyStoreProvider;
        private ProxyConfig proxyConfig;

        private ClientBuilder() {
            this.timeout = DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS;
            this.userAgent = DEFAULT_USER_AGENT;
        }

        public Xray build() {
            HttpBuilderBase configurator = new HttpBuilderBase(){};
            configurator.hostFromUrl(url)
                    .authentication(username, password, true)
                    .connectionTimeout(timeout)
                    .socketTimeout(timeout)
                    .userAgent(userAgent)
                    .proxy(proxyConfig)
                    .noHostVerification(noHostVerification)
                    .keyStoreProvider(keyStoreProvider)
                    .trustSelfSignCert(true)
                    .addRequestInterceptor(new PreemptiveAuthInterceptor());
            return create(configurator.build(), url);
        }

        public ClientBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public ClientBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ClientBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public ClientBuilder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public ClientBuilder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public ClientBuilder setNoHostVerification(boolean noHostVerification) {
            this.noHostVerification = noHostVerification;
            return this;
        }

        public ClientBuilder setKeyStoreProvider(KeyStoreProvider keyStoreProvider) {
            this.keyStoreProvider = keyStoreProvider;
            return this;
        }

        public ClientBuilder setProxyConfig(ProxyConfig proxyConfig) {
            this.proxyConfig = proxyConfig;
            return this;
        }
    }
}