package com.zgamelogic.helpers;

import com.zgamelogic.data.APIMonitor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class APIInterfacer {

    public static boolean pingAPI(APIMonitor apiMonitor){
        final String url = apiMonitor.getUrl()  + ":" + apiMonitor.getPort() + "/" + apiMonitor.getHealthCheckUrl();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            apiMonitor.setStatus(response.toLowerCase().contains("health"));
            return true;
        } catch (URISyntaxException | HttpClientErrorException e) {
            apiMonitor.setStatus(false);
            return false;
        }
    }

}
