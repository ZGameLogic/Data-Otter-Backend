package com.zgamelogic.helpers;

import com.zgamelogic.data.WebMonitor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

public abstract class WebInterfacer {

    public static boolean pingWeb(WebMonitor webMonitor){
        webMonitor.setCompletedInMilliseconds(System.currentTimeMillis());
        final String url = webMonitor.getUrl()  + ":" + webMonitor.getPort();
        RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(2)).setReadTimeout(Duration.ofSeconds(2)).build();
        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            webMonitor.setStatus(response.contains(webMonitor.getRegex()));
            webMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - webMonitor.getCompletedInMilliseconds());
            return true;
        } catch (Exception e) {
            webMonitor.setStatus(false);
            webMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - webMonitor.getCompletedInMilliseconds());
            return false;
        }
    }
}
