package prova.mockserver.spring;

import org.mockserver.client.*;
import org.mockserver.configuration.*;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.*;
import org.mockserver.netty.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.util.concurrent.*;

@Component
public class Runner implements ApplicationRunner {
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private MyConfig config;

  @Override
  public void run(ApplicationArguments args) {
    ConfigurationProperties.logLevel("DEBUG");

//    ClientAndServer c = new ClientAndServer(8081);

    MockServer server = new MockServer(8081);
    MockServerClient c = new MockServerClient(CompletableFuture.completedFuture(8081));

    try {
      HttpRequest req1 = new HttpRequest()
        .withPath("/prova")
        .withMethod("GET");
      c.when(req1).respond(new HttpResponse().withBody("Maccarons"));

      HttpRequest req2 = new HttpRequest()
        .withPath("/prova2")
        .withMethod("GET");
      c.when(req2).respond(new HttpResponse().withBody("Maccarons2"));

      ConfigurationProperties.tlsMutualAuthenticationRequired(true);
      ConfigurationProperties.tlsMutualAuthenticationCertificateChain("./cert.pem");

      RestTemplate restTemplate = new RestTemplate(
//        new HttpComponentsClientHttpRequestFactory()
      );
//      String resp = executeGet(restTemplate, "https://localhost:8081/prova");
//      System.out.println(resp);
//
//      String resp2 = executeGet(restTemplate, "https://localhost:8081/prova2");
//      System.out.println(resp2);

      String resp3 = executeGet(restTemplate, "https://prod.idrix.eu/secure/");
      System.out.println(resp3);

      ConfigurationProperties.tlsMutualAuthenticationRequired(false);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      c.stop();
      server.stop();
    }
    System.exit(0);
  }

  private String executeGet(RestTemplate restTemplate, String uri) {
    return restTemplate.execute(
      uri,
      HttpMethod.GET,
      __ -> {
      },
      r -> new String(r.getBody().readAllBytes())
    );
  }
}
