package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import nl.bioinf.minorapplicationdesign.ontpillen.model.webInteraction.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @GetMapping("/list")
    public String showResult(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        return "result";
    }
}
