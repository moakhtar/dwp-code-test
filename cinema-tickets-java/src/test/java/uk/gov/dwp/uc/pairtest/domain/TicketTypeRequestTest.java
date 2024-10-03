package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;

class TicketTypeRequestTest {
    @Test
    public void testNumOfTicketsAsNegative() {

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> {
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);
        });

        assertEquals("Number of tickets cannot be negative", exception.getMessage());
    }

    @Test
    public void testNumOfTicketsAsValidValue() {
        TicketTypeRequest typeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertEquals(typeRequest.getNoOfTickets(), 1);
        assertEquals(typeRequest.getTicketType(), TicketTypeRequest.Type.ADULT);
    }

}