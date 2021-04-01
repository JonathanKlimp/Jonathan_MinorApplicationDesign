package nl.bioinf.minorapplicationdesign.ontpillen;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan
public class MyApplicationContextConfiguration{

    @Bean
    @Scope("singleton")
    public DrugDao drugDao() {
        DrugDao myDrugDao = new InMemoryDrugDao();
        return myDrugDao;
    }

}