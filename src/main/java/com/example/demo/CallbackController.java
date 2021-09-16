package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(path = "/callback")
public class CallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackController.class);

    private final Map<String, Map<String, Object>> dirtyCache = new ConcurrentHashMap<>();

    @PostMapping
    public ServerResponse consumeCallBack(@RequestBody Map<String, Object> inputData,
                                @RequestHeader("log-user-id") String logUserId,
                                @RequestHeader("log-process-context") String logContextId) {
        LOGGER.info("Callback received with payload {} with userId {} and processId {}",
                inputData, logUserId, logContextId);
        storeInCache(logContextId, inputData);
        return ServerResponse.accepted().build();
    }

    @GetMapping
    public Map exposeLogs() {
        return dirtyCache;
    }

    @DeleteMapping
    public ServerResponse deleteCache() {
        this.dirtyCache.clear();
        return ServerResponse.accepted().build();
    }

    private void storeInCache(String logContextId, Map<String, Object> payload) {
        if (logContextId != null && payload != null) {
            dirtyCache.put(logContextId, payload);
        }
    }

}
