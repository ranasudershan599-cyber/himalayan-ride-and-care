package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="payment_records")
public class PaymentRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="booking_id",nullable=false) private RentalBooking booking;
    @NotNull @Positive @Column(nullable=false,precision=10,scale=2) private BigDecimal amount;
    @NotBlank @Column(nullable=false,length=30) private String method;
    @NotBlank @Column(nullable=false,length=20) private String status="RECORDED";
    @Column(name="paid_at",nullable=false) private LocalDateTime paidAt=LocalDateTime.now();
    protected PaymentRecord() {}
    public PaymentRecord(RentalBooking booking,BigDecimal amount,String method,String status){this.booking=booking;this.amount=amount;this.method=method;this.status=status;}
    public Long getId(){return id;} public RentalBooking getBooking(){return booking;} public void setBooking(RentalBooking v){booking=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getMethod(){return method;} public void setMethod(String v){method=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public LocalDateTime getPaidAt(){return paidAt;}
}
