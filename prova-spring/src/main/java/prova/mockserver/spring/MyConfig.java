package prova.mockserver.spring;

import org.apache.http.impl.client.*;
import org.apache.http.ssl.*;
import org.mockserver.logging.*;
import org.mockserver.socket.tls.*;
import org.springframework.context.annotation.*;
import org.springframework.http.client.*;
import org.springframework.web.client.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.*;

@Configuration
public class MyConfig {

  @Bean
  public RestTemplate restTemplate() throws Exception {
    System.setProperty("https.protocols", "TLSv1.2");

    HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());

    URL url = MyConfig.class.getClassLoader().getResource("cert.p12");
    Objects.requireNonNull(url);
    URI uri = url.toURI();
    SSLContext sslcontext = SSLContexts.custom()
      .loadKeyMaterial(
        new File(uri),
        "aaa".toCharArray(),
        "aaa".toCharArray(),
        null
      )
      .build();

    return new RestTemplate(new HttpComponentsClientHttpRequestFactory(
      HttpClients.custom()
        .useSystemProperties()
        .setSSLContext(sslcontext)
        .build()
    ));
  }

}
