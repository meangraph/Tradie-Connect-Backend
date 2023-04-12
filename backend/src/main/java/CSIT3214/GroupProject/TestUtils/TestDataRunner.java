package CSIT3214.GroupProject.TestUtils;

import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test-data")
public class TestDataRunner implements CommandLineRunner {

    @Autowired
    private CustomerService customerService;

    @Override
    public void run(String... args) {
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        System.out.println("Generating Data");

        // Adjust the number of customers to generate here
        int numberOfCustomersToGenerate = 100;

        for (int i = 0; i < numberOfCustomersToGenerate; i++) {
            Customer customer = testDataGenerator.createRandomCustomer();
            customerService.saveCustomer(customer);
        }

        System.out.println("Test data generation complete");
    }
}