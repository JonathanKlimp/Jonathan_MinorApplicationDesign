package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;

public class FarmacoWebScraper extends AbstractWebScraper {

    FarmacoWebScraper(DrugDao drugDao) {
        super(drugDao);
    }


    @Override
    public void parseHtml() {
        String drugs = getDrugs(drugDao);
        information.add(drugs);
    }


    private String getDrugs(DrugDao informationStorage) {
//        Drug drug = new DrugGroup();
//        drugDao.addDrugSubstance(null);
        return null;
    }
}
