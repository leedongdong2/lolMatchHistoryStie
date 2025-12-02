package com.reactpf.reactpf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/react")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> getMethodName() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "철수");
        data.put("age", 20);
        data.put("isMember", true);
        return data;
    }

    @GetMapping("/link")
    public Map<String,Object> getMethodNamed(@RequestParam("name") String param) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", param);
        return data;
    }

    @GetMapping("/test")
    public Map<String,Object> getMethodName(@RequestParam("count") String param) {
        Map<String, Object> data = new HashMap<>();
        data.put("count", param);
        return data;
    }
    
    


    
}
