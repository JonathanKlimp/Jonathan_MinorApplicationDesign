package nl.bioinf.minorapplicationdesign.ontpillen.webcontrol;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.webInteraction.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {
    DrugDao drugDao;

    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @GetMapping("/list")
    public String showResult(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("drugDao", this.drugDao);
        return "result";
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }
}
