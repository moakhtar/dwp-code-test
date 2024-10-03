package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import properties.TicketProperties;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ApplicationConfig {
    @Bean
    public TicketService ticketService(TicketPaymentService paymentService, SeatReservationService reservationService, TicketProperties ticketProperties) {
        return new TicketServiceImpl(paymentService, reservationService, ticketProperties);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("application.properties"));
        return configurer;
    }

    @Bean
    public TicketProperties ticketProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("application.properties").getInputStream());

        TicketProperties ticketProperties = new TicketProperties();
        ticketProperties.setMaxTickets(Integer.parseInt(properties.getProperty("max.tickets")));
        ticketProperties.setChildTicketPrice(Integer.parseInt(properties.getProperty("child.ticket.price")));
        ticketProperties.setAdultTicketPrice(Integer.parseInt(properties.getProperty("adult.ticket.price")));
        return ticketProperties;
    }
 }
