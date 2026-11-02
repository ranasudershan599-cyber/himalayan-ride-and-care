package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity @Table(name="assistance_requests")
public class AssistanceRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="customer_id",nullable=false) private Customer customer;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="booking_id") private RentalBooking booking;
    @NotBlank @Column(nullable=false,length=50) private String category;
    @NotBlank @Column(nullable=false,length=20) private String priority="NORMAL";
    @NotBlank @Size(max=1000) @Column(nullable=false,length=1000) private String description;
    @NotBlank @Size(max=200) @Column(nullable=false,length=200) private String location;
    @Size(max=100) @Column(name="assigned_to",length=100) private String assignedTo;
    @NotBlank @Column(nullable=false,length=20) private String status="OPEN";
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    protected AssistanceRequest() {}
    public AssistanceRequest(Customer c,RentalBooking b,String category,String priority,String description,String location){customer=c;booking=b;this.category=category;this.priority=priority;this.description=description;this.location=location;}
    public Long getId(){return id;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;} public RentalBooking getBooking(){return booking;} public void setBooking(RentalBooking v){booking=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;} public String getPriority(){return priority;} public void setPriority(String v){priority=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;} public String getAssignedTo(){return assignedTo;} public void setAssignedTo(String v){assignedTo=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
