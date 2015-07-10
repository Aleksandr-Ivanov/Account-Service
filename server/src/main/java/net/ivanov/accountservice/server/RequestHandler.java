package net.ivanov.accountservice.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.ivanov.accountservice.api.AccountService;

@Path("")
public class RequestHandler {
    private static volatile AccountService accountService;
    private static volatile StatsHandler statsHandler;

    static {
        try {
            accountService = new AccountServiceImpl();
            statsHandler = new StatsHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("account/{id}")
    public Response getAmount(@PathParam("id") Integer id) {

        long balance = accountService.getAmount(id);

        statsHandler.incrementGetStats();
        return Response.status(200).entity(balance).build();
    }

    @POST
    @Path("account/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addAmount(@PathParam("id") Integer id, 
            @FormParam("value") Long value) throws Exception {

        accountService.addAmount(id, value);
        statsHandler.incrementAddStats();
        String result = value + " has been added"  ;

        return Response.status(200).entity(result).build();
    }

    @GET
    @Path("stats")
    public Response getStats() {
        long getRequestsAmount = statsHandler.getQuantityOfGet();
        long addRequestsAmount = statsHandler.getQuantityOfAdd();

        long  getRate = statsHandler.getRequestRate(getRequestsAmount);
        long  addRate = statsHandler.getRequestRate(addRequestsAmount);
        
        StringBuilder statsBuilder = new StringBuilder();

        statsBuilder.append("\r\n <br> getAmount requests quantity: ");
        statsBuilder.append(getRequestsAmount);
        statsBuilder.append("\r\n <br> getAmount request rate per second: ");
        statsBuilder.append(getRate);
        statsBuilder.append("\r\n <br> addAmount requests quantity: ");
        statsBuilder.append(addRequestsAmount);
        statsBuilder.append("\r\n <br> addAmount request rate per second: ");
        statsBuilder.append(addRate);
        statsBuilder.append("\r\n");
        String stats = statsBuilder.toString();

        return Response.status(200).entity(stats).build();

    }

    @GET
    @Path("stats/reset")
    public Response resetStats() {
        statsHandler.reset();
        String stats = "Stats has been reset to 0.";

        return Response.status(200).entity(stats).build();
    }

}
