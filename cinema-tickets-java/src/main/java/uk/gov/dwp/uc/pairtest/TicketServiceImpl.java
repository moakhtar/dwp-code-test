package uk.gov.dwp.uc.pairtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import properties.TicketProperties;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

@Service
public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;
    private final TicketProperties ticketProperties;

    @Autowired
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService, TicketProperties ticketProperties) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.ticketProperties = ticketProperties;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID");
        }

        int totalTickets = calculateTotalTickets(ticketTypeRequests);
        int totalAmountToPay = calculateTotalAmountToPay(ticketTypeRequests);
        int totalSeatsToReserve = calculateTotalSeatsToReserve(ticketTypeRequests);
        int numAdultTickets = countAdultTickets(ticketTypeRequests);
        int numChildTickets = countChildTickets(ticketTypeRequests);
        int numInfantTickets = countInfantTickets(ticketTypeRequests);

        if (totalTickets > ticketProperties.getMaxTickets()) {
            throw new InvalidPurchaseException("Cannot purchase more than 25 tickets");
        }

        if (numAdultTickets == 0 && (numChildTickets > 0 || numInfantTickets > 0)) {
            throw new InvalidPurchaseException("Child and Infant tickets cannot be purchased without an Adult ticket");
        }

        /* I would add try catch for the payment and reservation service
        but instructions have stated that "The code in the thirdparty.* packages CANNOT be modified."
        */
        paymentService.makePayment(accountId, totalAmountToPay);
        System.out.println("Payment successful for account: " + accountId); // Can be replaced with LOG4J

        reservationService.reserveSeat(accountId, totalSeatsToReserve);
        System.out.println("Reservation successful for account: " + accountId); // Can be replaced with LOG4J
    }

    private int calculateTotalAmountToPay(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .mapToInt(request -> {
                    int numOfTickets = request.getNoOfTickets();
                    switch (request.getTicketType()) {
                        case CHILD:
                            return numOfTickets * ticketProperties.getChildTicketPrice();
                        case ADULT:
                            return numOfTickets * ticketProperties.getAdultTicketPrice();
                        default:
                            return 0;
                    }
                })
                .sum();
    }

    private int calculateTotalSeatsToReserve(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() != Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int calculateTotalTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int countAdultTickets(TicketTypeRequest[] ticketTypeRequests) {

        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() == Type.ADULT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int countChildTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() == Type.CHILD)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int countInfantTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() == Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

}
