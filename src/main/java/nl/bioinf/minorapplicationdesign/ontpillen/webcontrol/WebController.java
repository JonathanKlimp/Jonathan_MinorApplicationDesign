package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;


/**
 *
 * @author Noami Hindriks
 */
@Controller
public class WebController {
    DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @GetMapping("/")
    public String showIndex(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

//        If there is no userType set in the session, set t he userType to "gebruiker"
        if (Objects.isNull(session.getAttribute("userType"))) {
            session.setAttribute("userType", "gebruiker");
        }

        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/")
    public RedirectView changeFrontEnd(HttpServletRequest request) {
        String userType = request.getParameter("user-type");

        System.out.println(request.getRequestURL().toString() + "?" + request.getQueryString());

        HttpSession session = request.getSession();
//        Add the userType to the session
        session.setAttribute("userType", userType);
//        Redirect to showIndex
        return new RedirectView("/");
    }

    @GetMapping("/list")
    public String showResult(Model model, @ModelAttribute User user) {
        model.addAttribute("user", user);
        model.addAttribute("drugDao", this.drugDao);
        return "result_test";
    }

    @GetMapping("/medicijn")
    public String showDrugPage(Model model) {
        return "drugPage";
    }
}
