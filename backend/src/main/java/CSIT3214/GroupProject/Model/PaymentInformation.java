package CSIT3214.GroupProject.Model;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@Embeddable
public class PaymentInformation {

    private String cardName;
    private String cardNumber;
    private String cardExpiry;
    private String cardCVV;

    public PaymentInformation(String cardName, String cardNumber, String cardExpiry, String cardCVV) {
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCVV = cardCVV;

    }
}
