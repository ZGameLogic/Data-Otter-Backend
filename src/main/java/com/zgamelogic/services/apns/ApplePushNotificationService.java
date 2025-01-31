package com.zgamelogic.services.apns;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@PropertySource("File:./APNS.properties")
public class ApplePushNotificationService {
    @Value("${kid}")    private String kid;
    @Value("${org_id}") private String orgId;
    @Value("${APN}")    private String apnEndpoint;

    public void sendNotification(String device, ApplePushNotification notification){
        String url = apnEndpoint + "/3/device/" + device;
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", "bearer " + authJWT());
        headers.add("apns-push-type", "alert");
        headers.add("apns-priority", "5");
        headers.add("apns-expiration", "0");
        headers.add("apns-topic", "zgamelogic.Monitors");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(notification, headers), String.class);
    }

    private String authJWT() {
        try {
            String privateKeyPEM = new String(Files.readAllBytes(new File("./AuthKey_" + kid + ".p8").toPath()));
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", ""); // Remove any whitespaces or newlines

            byte[] decodedKey = org.bouncycastle.util.encoders.Base64.decode(privateKeyPEM);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            return Jwts.builder()
                    .setIssuer(orgId)
                    .setIssuedAt(new Date())
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .setHeaderParam("kid", kid)
                    .compact();
        } catch (Exception e){
            log.error("Unable to create JWT", e);
            return "";
        }
    }
}
