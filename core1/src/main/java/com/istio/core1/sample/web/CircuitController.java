package com.istio.core1.sample.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import com.istio.core1.sample.exception.TestException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * CircuitController
 */
@RestController
@RequestMapping("/circuits")
@Slf4j
public class CircuitController {
    @Value("${custom.hostname}")
    private String hostname;

    /**
     * 하나의 API 로 Circuit 분기 circuitType: 어떤 팟에 해당하는가? 전체. hostname. b: 실패 확률 c: 실패코드
     * d:
     */
    @GetMapping("circuit01")
    public Map<String, String> circuit01(@RequestParam(defaultValue = "all") String circuitType,
            @RequestParam(defaultValue = "0") int failRate, @RequestParam(defaultValue = "200") int responseCode,
            @RequestParam(defaultValue = "0") long delay, HttpServletResponse response) throws Exception {
                
        TimeUnit.MILLISECONDS.sleep(delay);

        Map<String, String> result = new HashMap<>();

        result.put("hostname",hostname);

        if ("all".equals(circuitType) || hostname.contains(circuitType)) {
            Random r = new Random();
            int num = r.nextInt(100);
            log.error("" + num);
            if (num < failRate + 1) {
                response.setStatus(responseCode);
                result.put("result", "FAILED");
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                result.put("result", "SC_OK");
            }

            if (responseCode > 1000) {
                // unknown Issue
                throw new TestException("unknown responseCode");
            }
        }
        return result;
    }

}