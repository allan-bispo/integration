package com.example.odm.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
public class CustomerDocument {
    @Id
    private String id;
    private String name;
    private String email;
    private String status;
    private String sourceSystem;
}
