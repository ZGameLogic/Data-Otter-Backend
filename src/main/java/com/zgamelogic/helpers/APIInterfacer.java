package com.zgamelogic.helpers;

import com.zgamelogic.data.APIMonitor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

public abstract class APIInterfacer {

    public static boolean pingAPI(APIMonitor apiMonitor){
        apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis());
        final String url = apiMonitor.getUrl()  + ":" + apiMonitor.getPort() + "/" + apiMonitor.getHealthCheckUrl();
        RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(2)).build();
        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            apiMonitor.setStatus(response.toLowerCase().contains("health"));
            apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - apiMonitor.getCompletedInMilliseconds());
            return true;
        } catch (Exception e) {
            apiMonitor.setStatus(false);
            apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - apiMonitor.getCompletedInMilliseconds());
            return false;
        }
    }

}
