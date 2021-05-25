package nl.bioinf.minorapplicationdesign.ontpillen.model.web_interaction;


/**
 *
 * @author Naomi Hindriks
 */
public class User {
    private int age;
    private Gender gender;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = Gender.valueOf(gender);
    }
}
