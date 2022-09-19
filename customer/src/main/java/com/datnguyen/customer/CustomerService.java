package com.datnguyen.customer;

import com.datnguyen.amqp.RabbitMQMessageProducer;
import com.datnguyen.clients.fraud.FraudCheckResponse;
import com.datnguyen.clients.fraud.FraudClient;
import com.datnguyen.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer producer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.getIsFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        // send notification
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to Amigscode...", customer.getFirstName())
        );

        producer.publish(notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key");
    }
}
