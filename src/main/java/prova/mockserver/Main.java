package prova.mockserver;

import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.*;
import org.mockserver.configuration.*;
import org.mockserver.integration.*;
import org.mockserver.logging.*;
import org.mockserver.model.*;
import org.mockserver.socket.tls.*;

import javax.net.ssl.*;
import java.io.*;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        ConfigurationProperties.logLevel("DEBUG");
        ClientAndServer c = new ClientAndServer(8081);
        try
        {
            HttpRequest req1 = new HttpRequest()
                .withPath("/prova")
                .withMethod("GET");
            c.when(req1).respond(new HttpResponse().withBody("Maccarons"));

            HttpRequest req2 = new HttpRequest()
                .withPath("/prova2")
                .withMethod("GET");
            c.when(req2).respond(new HttpResponse().withBody("Maccarons 2"));

            ConfigurationProperties.tlsMutualAuthenticationRequired(true);
            ConfigurationProperties.tlsMutualAuthenticationCertificateChain(".\\cert.pem");

            HttpsURLConnection.setDefaultSSLSocketFactory(new KeyStoreFactory(new MockServerLogger()).sslContext().getSocketFactory());

            HttpClient client = getClient(true);
            doRequest(client, "https://localhost:8081/prova");

            HttpClient client2 = getClient(false);
            doRequest(client2, "https://localhost:8081/prova2");

            c.retrieveRecordedRequests(req1);
            c.retrieveRecordedRequests(req2);
        } finally
        {
            c.stop();
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void doRequest(HttpClient client, String uri) throws IOException
    {
        HttpGet get = new HttpGet(uri);
        org.apache.http.HttpResponse resp = client.execute(get);

        byte[] b = new byte[(int) resp.getEntity().getContentLength()];
        InputStream is = resp.getEntity().getContent();
        is.read(b);

        String s = new String(b);
        System.out.println(s);
    }

    private static HttpClient getClient(boolean secure) throws Exception
    {
        SSLContext sslcontext = SSLContexts.custom()
            .loadKeyMaterial(
                new File("C:\\Users\\Davide\\IdeaProjects\\ProvaMockServer\\src\\main\\resources\\cert.p12"),
                "aaa".toCharArray(),
                "aaa".toCharArray(),
                null
            )
            .build();

        HttpClientBuilder b = HttpClients.custom();
        if (secure)
            b.setSSLContext(sslcontext);
        return b
            .useSystemProperties()
            .build();
    }
}
