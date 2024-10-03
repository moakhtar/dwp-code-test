package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import properties.TicketProperties;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketServiceTest {

    private TicketPaymentService paymentService;
    private SeatReservationService reservationService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        reservationService = mock(SeatReservationService.class);

        // Set and test dummy values
        TicketProperties ticketProperties = new TicketProperties();
        ticketProperties.setMaxTickets(25);
        ticketProperties.setChildTicketPrice(15);
        ticketProperties.setAdultTicketPrice(25);

        ticketService = new TicketServiceImpl(paymentService, reservationService, ticketProperties);
    }

    @Test
    public void testPurchaseTicketsExceedsLimit() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, adultRequest));

        assertEquals("Cannot purchase more than 25 tickets", exception.getMessage());
    }

    @Test
    public void testPurchaseChildTicketsWithoutAdult() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, childRequest));

        assertEquals("Child and Infant tickets cannot be purchased without an Adult ticket", exception.getMessage());
    }

    @Test
    public void testPurchaseInfantTicketsWithoutAdult() {
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, infantRequest));

        assertEquals("Child and Infant tickets cannot be purchased without an Adult ticket", exception.getMessage());
    }

    @Test
    public void testPurchaseWithInvalidAccount() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, adultRequest));

        assertEquals("Invalid account ID", exception.getMessage());
    }

    @Test
    public void testPurchaseValidTickets() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(paymentService, times(1)).makePayment(1L, 95); // 2 * 25 + 3 * 15 + 0 = 95
        verify(reservationService, times(1)).reserveSeat(1L, 5); // 2 adults + 3 children + 1 infant = 5 seats
    }
}