package com.example.integration.route;

import com.example.odm.model.CustomerDocument;
import com.example.odm.repository.CustomerMongoRepository;
import com.example.orm.model.CustomerEntity;
import com.example.orm.repository.CustomerRepository;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerIntegrationRoute extends RouteBuilder {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMongoRepository mongoRepository;

    @Override
    public void configure() throws Exception {
        from("jpa:com.example.orm.model.CustomerEntity?" +
                "namedQuery=findNewCustomers&" +
                "consumeDelete=false&" +
                "consumeLockEntity=false")
            .routeId("jpa-to-mongodb-route")
            .log("Processing customer: ${body}")
            .process(exchange -> {
                CustomerEntity entity = exchange.getIn().getBody(CustomerEntity.class);
                CustomerDocument doc = new CustomerDocument();
                doc.setName(entity.getName());
                doc.setEmail(entity.getEmail());
                doc.setStatus(entity.getStatus());
                doc.setSourceSystem("LEGACY_DB");
                exchange.getIn().setBody(doc);
            })
            .to("mongodb:mongoBean?database=integration&collection=customers&operation=insert")
            .log("Successfully saved to MongoDB: ${body}");

        rest("/api/customers")
            .get("/sync")
            .to("direct:trigger-sync");

        from("direct:trigger-sync")
            .routeId("manual-sync-route")
            .log("Triggering manual sync")
            .setBody(constant("SELECT c FROM CustomerEntity c WHERE c.status = 'NEW'"))
            .to("jpa:com.example.orm.model.CustomerEntity?query=#${body}")
            .split(body())
                .process(exchange -> {
                    CustomerEntity entity = exchange.getIn().getBody(CustomerEntity.class);
                    CustomerDocument doc = new CustomerDocument();
                    doc.setName(entity.getName());
                    doc.setEmail(entity.getEmail());
                    doc.setStatus(entity.getStatus());
                    doc.setSourceSystem("MANUAL_SYNC");
                    exchange.getIn().setBody(doc);
                })
                .to("mongodb:mongoBean?database=integration&collection=customers&operation=insert")
            .end()
            .log("Manual sync completed");
    }
}
