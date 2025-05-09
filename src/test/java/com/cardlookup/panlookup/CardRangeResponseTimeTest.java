package com.cardlookup.panlookup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardRangeResponseTimeTest {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String URL_TEMPLATE = "http://localhost:8080/api/card-range/";
    private static final Random random = new Random();

    private static final BigInteger START_PAN = new BigInteger("4000022040000000");
    private static final BigInteger END_PAN = new BigInteger("4000022049999999");

    public static void main(String[] args){
        testAverageResponseTimeWithFixedRangePANs();
    }
    private static String generatePanInRange() {
        BigInteger range = END_PAN.subtract(START_PAN);
        BigInteger randomOffset = new BigInteger(range.bitLength(), random).mod(range.add(BigInteger.ONE));
        BigInteger pan = START_PAN.add(randomOffset);
        return pan.toString();
    }

    public static void testAverageResponseTimeWithFixedRangePANs() {
        int iterations = 100;
        List<Long> times = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            String pan = generatePanInRange();
            String url = URL_TEMPLATE + pan;

            Instant start = Instant.now();
            try {
                restTemplate.getForObject(url, String.class);
            } catch (Exception e) {
                // Ignore not-found cases for timing
            }
            Instant end = Instant.now();
            times.add(Duration.between(start, end).toMillis());
        }

        double average = times.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.println("Average response time over " + iterations + " PANs in range: " + average + " ms");
    }
}
