package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.User;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * @author Noami Hindriks
 */ //TODO add javaodc
@Controller
public class WebController {
    DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("drugDao", this.drugDao);
    }

    @GetMapping("/")
    public String showIndex(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

//        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping({"/", "/zoekresultaten/{searchQuery}", "/medicijn/{drugName}", "/lijst", "/privacy", "/disclaimer"})
    public RedirectView changeFrontEnd(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserType newUserType = UserType.valueOf(request.getParameter("user-type"));
        session.setAttribute("userType", newUserType);

        String requestOrigin = request.getRequestURI();
        return new RedirectView(requestOrigin);
    }

    @GetMapping("/zoekresultaten/{searchQuery}")
    public String showSearchResults(Model model, @PathVariable String searchQuery, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        return "search_result";
    }

    @GetMapping("/lijst")
    public String showResult(Model model, @ModelAttribute User user, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        model.addAttribute("user", user);
        return "list_page";
    }

    @GetMapping("/medicijn/{drugName}")
    public String showDrugPage(Model model, @PathVariable String drugName, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }
        return "drug_page";
    }

    @PostMapping("/zoeken")
    public RedirectView zoeken(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        String searchQuery = request.getParameter("search-query");
        return new RedirectView(("zoekresultaten/" + searchQuery));
    }

    @GetMapping("/disclaimer")
    public String showDisclaimer(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }
        return "disclaimer";
    }

    @GetMapping("/privacy")
    public String showPrivacy(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //        If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }
        return "privacy";
    }
}