package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.*;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private String email;
    private String password;
    private Role role;
    // Customer fields
    private String firstName;
    private String lastName;
    private PaymentInformation paymentInformation;
    // ServiceProvider fields
    private String companyName;
    private String abn;
    private Set<Skill> skills;
    // Common fields
    private String phoneNumber;
    private String streetAddress;
    private Suburb suburb;
    private String postCode;
    private Membership membership;
}