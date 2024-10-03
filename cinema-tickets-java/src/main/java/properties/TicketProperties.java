package properties;

public class TicketProperties {

    private int maxTickets;
    private int childTicketPrice;
    private int adultTicketPrice;

    public int getMaxTickets() {
        return maxTickets;
    }

    public void setMaxTickets(int maxTickets) {
        this.maxTickets = maxTickets;
    }

    public int getChildTicketPrice() {
        return childTicketPrice;
    }

    public void setChildTicketPrice(int childTicketPrice) {
        this.childTicketPrice = childTicketPrice;
    }

    public int getAdultTicketPrice() {
        return adultTicketPrice;
    }

    public void setAdultTicketPrice(int adultTicketPrice) {
        this.adultTicketPrice = adultTicketPrice;
    }
}
