package com.yh.siemen.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class HtmlApi {

    @GetMapping("/hml")
    @ResponseBody
    public String index() throws IOException {
        String name = "/static/index.html";
        InputStream inputStream = getClass().getResourceAsStream(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
        }
        inputStream.close();
        reader.close();
        String s = stringBuilder.toString();
        return s.replaceAll("%contextPath%", "/api");
    }
}
