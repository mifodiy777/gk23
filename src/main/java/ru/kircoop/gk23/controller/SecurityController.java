package ru.kircoop.gk23.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created with IntelliJ IDEA.
 * User: Kuzmin.K.A
 * Date: 07.11.2021
 */
@Controller
public class SecurityController {

    @GetMapping(value = "/login")
    public String toLoginPage() {
        return "login";
    }
}
