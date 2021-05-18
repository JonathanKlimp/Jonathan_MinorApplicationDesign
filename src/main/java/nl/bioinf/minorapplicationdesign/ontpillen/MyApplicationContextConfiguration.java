package nl.bioinf.minorapplicationdesign.ontpillen;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.InMemoryDrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.User;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction.UserType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Naomi Hindriks
 */
@Configuration
@ComponentScan
public class MyApplicationContextConfiguration{

    @Bean
    @Scope("singleton")
    public DrugDao drugDao() {
        DrugDao myDrugDao = new InMemoryDrugDao();
        return myDrugDao;
    }

    @Bean
    @Scope("singleton")
    public UserType userType() {
        UserType userType = new UserType();
        return userType;
    }
}
