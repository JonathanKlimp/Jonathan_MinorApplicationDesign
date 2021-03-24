package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String showHome(){
        return "home";
    }
}
