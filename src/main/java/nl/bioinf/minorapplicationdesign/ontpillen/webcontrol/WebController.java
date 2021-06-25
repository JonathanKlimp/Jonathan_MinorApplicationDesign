package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import nl.bioinf.minorapplicationdesign.ontpillen.OntpillenApplication;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.User;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger LOGGER = LoggerFactory.getLogger(WebController.class);


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
        LOGGER.info("Request for homepage");
        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        model.addAttribute("user", new User());
        LOGGER.info("Serving homepage");
        return "index";
    }

    @PostMapping({"/", "/zoekresultaten/{searchQuery}", "/medicijn/{drugName}", "/lijst", "/privacy", "/disclaimer"})
    public RedirectView changeFrontEnd(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserType newUserType = UserType.valueOf(request.getParameter("user-type"));

        LOGGER.info("Post request: changing usertype to " + newUserType);
        session.setAttribute("userType", newUserType);

        String requestOrigin = request.getRequestURI();
        LOGGER.debug("Redirecting to " + requestOrigin);
        return new RedirectView(requestOrigin);
    }

    @GetMapping("/zoekresultaten/{searchQuery}")
    public String showSearchResults(Model model, @PathVariable String searchQuery, HttpServletRequest request) {
        LOGGER.info("Request search result page");
        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        LOGGER.info("Serving search results");
        return "search_result";
    }

    @GetMapping("/lijst")
    public String showResult(Model model, @ModelAttribute User user, HttpServletRequest request) {
        LOGGER.info("Request for list page");
        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        model.addAttribute("user", user);
        LOGGER.info("Serving list page");
        return "list_page";
    }

    @GetMapping("/medicijn/{drugName}")
    public String showDrugPage(Model model, @PathVariable String drugName, HttpServletRequest request) {
        LOGGER.info("Request for " + drugName + " drug page");
        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        LOGGER.info("Serving " + drugName + " drug page");
        return "drug_page";
    }

    @PostMapping("/zoeken")
    public RedirectView zoeken(Model model, HttpServletRequest request) {
        String searchQuery = request.getParameter("search-query");
        LOGGER.info("Request searching for " + searchQuery);

        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        LOGGER.debug("Redirecting to search result page");
        return new RedirectView(("zoekresultaten/" + searchQuery));
    }

    @GetMapping("/disclaimer")
    public String showDisclaimer(Model model, HttpServletRequest request) {
        LOGGER.info("Request for disclaimer page");

        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        LOGGER.info("Serving disclaimer page");
        return "disclaimer";
    }

    @GetMapping("/privacy")
    public String showPrivacy(Model model, HttpServletRequest request) {
        LOGGER.info("Request for privacy page");

        HttpSession session = request.getSession();

        // If there is no userType set in the session, set t he userType to "gebruiker"
        if (session.getAttribute("userType") == null) {
            LOGGER.debug("Setting usertype to PATIENT");
            session.setAttribute("userType", UserType.valueOf("PATIENT"));
        }

        LOGGER.info("Serving privacy page");
        return "privacy";
    }
}