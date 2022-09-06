package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.websocket.server.PathParam;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String login(@PathParam("originUrl") String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
