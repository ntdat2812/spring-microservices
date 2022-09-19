package com.datnguyen.clients.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private Integer toCustomerId;
    private String toCustomerEmail;
    private String message;
}
