package com.telnyx.webhookverification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebhookControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private final String payloadSignature = "E7Z/uNnEWOnLDk1SUF75P35JL345szFKIxTlTyWui+nF9Bum9BAUnOcISjszPc7v9AF2swipFxcTpGwMIAECCw==";
    private final String payloadTimestamp = "1623051511";
    private HttpHeaders headers;
    private String url;

    @BeforeEach
    public void setup() {
        url = "http://localhost:" + port + "/webhook";

        headers = new HttpHeaders();
        headers.add("telnyx-signature-ed25519", payloadSignature);
        headers.add("telnyx-timestamp", payloadTimestamp);
    }

    @Test
    public void webhook_whenPayloadMatchesSignatureAndTimestamp_returns200() {
        HttpEntity<String> request =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void webhook_whenPayloadDoesNotMatchSignatureAndTimestamp_returns401() {
        HttpEntity<String> request =
                new HttpEntity<>("unexpected-payload", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private String payload = "{\n" +
            "  \"data\": {\n" +
            "    \"event_type\": \"message.received\",\n" +
            "    \"id\": \"e7f70185-c248-43e3-a8e5-b83c5092ccfc\",\n" +
            "    \"occurred_at\": \"2021-06-07T07:38:31.372+00:00\",\n" +
            "    \"payload\": {\n" +
            "      \"cc\": [],\n" +
            "      \"completed_at\": null,\n" +
            "      \"cost\": null,\n" +
            "      \"direction\": \"inbound\",\n" +
            "      \"encoding\": \"GSM-7\",\n" +
            "      \"errors\": [],\n" +
            "      \"from\": {\n" +
            "        \"carrier\": \"\",\n" +
            "        \"line_type\": \"VoWiFi\",\n" +
            "        \"phone_number\": \"+13125790236\"\n" +
            "      },\n" +
            "      \"id\": \"a2c5a2e5-65a1-4b10-b863-ab9a4de1a3ae\",\n" +
            "      \"media\": [],\n" +
            "      \"messaging_profile_id\": \"400174d8-9abf-4cd0-af88-8f5715f5f78e\",\n" +
            "      \"organization_id\": \"20fc8b33-1664-4da4-8bfb-b6f5d5a69243\",\n" +
            "      \"parts\": 1,\n" +
            "      \"received_at\": \"2021-06-07T07:38:31.347+00:00\",\n" +
            "      \"record_type\": \"message\",\n" +
            "      \"sent_at\": null,\n" +
            "      \"tags\": [],\n" +
            "      \"text\": \"Hello, World!\",\n" +
            "      \"to\": [\n" +
            "        {\n" +
            "          \"carrier\": \"Telnyx\",\n" +
            "          \"line_type\": \"Wireless\",\n" +
            "          \"phone_number\": \"+13125790236\",\n" +
            "          \"status\": \"webhook_delivered\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"SMS\",\n" +
            "      \"valid_until\": null,\n" +
            "      \"webhook_failover_url\": \"https://8c9cd2040c0f.ngrok.io/webhook\",\n" +
            "      \"webhook_url\": \"https://8c9cd2040c0f.ngrok.io/webhook\"\n" +
            "    },\n" +
            "    \"record_type\": \"event\"\n" +
            "  },\n" +
            "  \"meta\": {\n" +
            "    \"attempt\": 1,\n" +
            "    \"delivered_to\": \"https://8c9cd2040c0f.ngrok.io/webhook\"\n" +
            "  }\n" +
            "}";
}