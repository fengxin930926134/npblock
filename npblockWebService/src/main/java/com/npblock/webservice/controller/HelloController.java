package com.npblock.webservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.npblock.webservice.entity.Result;
import com.npblock.webservice.entity.Test;
import com.npblock.webservice.service.TestService;
import com.npblock.webservice.util.IpUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 测试控制器
 * @author fengxin
 */
@Controller
@RequiredArgsConstructor
public class HelloController {

    private final @NonNull TestService testService;

    @ResponseBody
    @RequestMapping("/hello")
    public HttpEntity<?> hello(HttpServletRequest request) {
        System.out.println(IpUtil.getIpAddress(request));
        System.out.println(IpUtil.getIpAddr(request));
        List<Test> tests = testService.findAll();
        return ResponseEntity.ok(new Result(JSONArray.toJSON(tests)));
    }

    @RequestMapping("/")
    public String index() {
        return "/index.html";
    }
}
