package CSIT3214.GroupProject.TestUtils;

import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Membership;
import CSIT3214.GroupProject.Model.MembershipType;
import com.github.javafaker.Faker;

import java.util.Locale;

public class TestDataGenerator {
    private final Faker faker;

    public TestDataGenerator() {
        faker = new Faker(new Locale("en-AU"));
    }

    public Customer createRandomCustomer() {
        Customer customer = new Customer();

        customer.setFirstName(faker.name().firstName());
        customer.setLastName(faker.name().lastName());
        customer.setEmail(faker.internet().emailAddress());
        customer.setPassword(faker.internet().password());
        customer.setPhoneNumber(faker.phoneNumber().phoneNumber());
        customer.setStreetAddress(faker.address().streetAddress());
        //customer.setCity(faker.address().city());
        //customer.setState(faker.address().stateAbbr());
        customer.setPostCode(faker.address().zipCode());

        Membership membership = new Membership();
        membership.setMembershipType(randomCustomerMembershipType());
        membership.setPrice(faker.number().randomDouble(2, 10, 100));
        membership.setDescription(faker.lorem().sentence());

        customer.setMembership(membership);

        return customer;
    }

    private MembershipType randomCustomerMembershipType() {
        return Math.random() < 0.5 ? MembershipType.CLIENT_SUBSCRIPTION : MembershipType.PAY_ON_DEMAND;
    }
}