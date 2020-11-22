package me.jaejoon.demo.Main;

import me.jaejoon.demo.account.CurrentUser;
import me.jaejoon.demo.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainHome(Model model , @CurrentUser Account account){
        if(account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("login")
    public String login(){
        return "login-page";
    }
}
