package com.lithan.merrymeals.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lithan.merrymeals.entity.Donation;
import com.lithan.merrymeals.repository.PaypalRepository;
import com.lithan.merrymeals.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.exception.PayPalException;
import com.paypal.base.rest.PayPalRESTException;


@RestController
@RequestMapping("/paypal")
public class PaypalController {

    @Autowired
    PaypalService paypalService;
    @Autowired
    private PaypalRepository paymentRepository;

    public static final String SUCCESS_URL = "http://localhost:3000/success";
    public static final String CANCEL_URL = "http://localhost:3000/cancel";

  @PostMapping("/pay")
public ResponseEntity<String> payment(@RequestBody Donation donation) {
    try {
        Payment createPayment = paypalService.createPayment(donation.getPrice(), donation.getCurrency(), donation.getMethod(),
                donation.getIntent(), donation.getDescription(), CANCEL_URL, SUCCESS_URL);
        for (Links link : createPayment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return ResponseEntity.ok(link.getHref());
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create payment.");
    } catch (PayPalRESTException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment: " + e.getMessage());
    }
}

@GetMapping("/success")
public ResponseEntity<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
    try {
        Payment payment = paypalService.executePayment(paymentId, payerId);
        if (payment.getState().equals("approved")) {
            Donation donation = new Donation();
            // ... (populate donation object)
            Donation savedDonation = paymentRepository.save(donation);
            System.out.println("Payment saved with ID: " + savedDonation.getId());
            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/pay");
            
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment not approved.");
    } catch (PayPalRESTException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment: " + e.getMessage());
    }
}

@GetMapping("/cancel")
public ResponseEntity<String> cancelPay() {
    // Handle cancel logic if needed
    return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/pay"); // Redirect to /pay after payment cancellation
}
}