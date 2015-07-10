package net.ivanov.accountservice.client;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class AccountServiceClient {
    private static final String PROPERTIES_LINK = "client.properties";
    private static int rCount;
    private static int wCount;
    private static volatile boolean running = true;
    private static List<Integer> ids = new ArrayList<>();
    private static WebTarget service;


    public static void main(String[] args) throws Exception {
        try {
            Properties properties = new Properties();
            InputStream propertiesStream = AccountServiceClient.class
                    .getClassLoader()
                    .getResourceAsStream(PROPERTIES_LINK);

            properties.load(propertiesStream);
            propertiesStream.close();
            
            String rCountString = properties.getProperty("rCount");
            String wCountString = properties.getProperty("wCount");
            String serviceUrl = properties.getProperty("publish.address");
            String ids = properties.getProperty("run.ids");

            rCount = Integer.parseInt(rCountString);
            wCount = Integer.parseInt(wCountString);

            ClientConfig config = new ClientConfig();
            
            Client client = ClientBuilder.newClient(config);
            URI targetUri = getBaseURI(serviceUrl);
            
            service = client.target(targetUri);

            generateIds(ids);
            runReaders(rCount);
            runWriters(wCount);
        } catch (Exception e) {
            System.err.println("Error: " + e);
            throw e;
        }

    }

    private static void runReaders(int count) {
        if (count <= 0) {
            System.out.println("No readers run");
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        System.out.println("Run readers: " + count);
        while (count > 0) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        String id = String.valueOf(getRandomId());

                        Response response = service.path("account")
                                .path(id)
                                .request()
                                .get();
                        
                        String result = "Status: " + response.getStatus()
                                + " balance: " 
                                + response.readEntity(Long.class);
                        
                        System.out.println(result);
                    }
                }
            });
            count--;
        }
    }

    private static void runWriters(int count) {
        if (count <= 0) {
            System.out.println("No writers run");
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        System.out.println("Run writers: " + count);
        while (count > 0) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        boolean positive = new Random().nextBoolean();
                        int balance = new Random().nextInt(100000);
                        balance = positive ? balance : -balance;

                        String id = String.valueOf(getRandomId());
                        String value = String.valueOf(balance);
                        Form form = new Form();

                        form.param("value", value);
                        Entity<Form> entity = Entity.entity(form, 
                                MediaType.APPLICATION_FORM_URLENCODED);

                        Response response = service.path("account")
                                .path(id)
                                .request()
                                .post(entity);
                        
                        String result = "Status: " + response.getStatus()
                                + " response: " 
                                + response.readEntity(String.class);
                        
                        System.out.println(result);
                    }
                }
            });
            count--;
        }
    }

    private static void generateIds(String s) {
        String[] a = s.split(",");
        
        for (String range : a) {
            parseRange(range);
        }
    }

    private static void parseRange(String range) {
        int idx = range.indexOf("-");

        String s1 = range.substring(0, idx);
        String s2 = range.substring(idx + 1);
        
        int start = Integer.parseInt(s1);
        int end = Integer.parseInt(s2);
        
        if (end < start) {
            throw new IllegalArgumentException("" + start + " < " + end);
        }
        for (int i = start; i <= end; i++) {
            ids.add(i);
        }
    }

    public static int getRandomId() {
        int idsListSize = ids.size();
        
        int randomListId = new Random().nextInt(idsListSize);
        
        return ids.get(randomListId);
    }

    private static URI getBaseURI(String uri) {
        return UriBuilder.fromUri(uri).build();
    }
}
