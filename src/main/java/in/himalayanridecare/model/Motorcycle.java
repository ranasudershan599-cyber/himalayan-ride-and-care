package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity @Table(name="motorcycles")
public class Motorcycle {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=20) @Column(nullable=false,unique=true,length=20) private String registrationNumber;
    @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String model;
    @NotNull @PositiveOrZero @Column(nullable=false,precision=10,scale=2) private BigDecimal dailyRate;
    @NotNull @PositiveOrZero @Column(nullable=false) private Integer odometerKm=0;
    @NotBlank @Column(nullable=false,length=20) private String status="AVAILABLE";
    protected Motorcycle() {}
    public Motorcycle(String registrationNumber,String model,BigDecimal dailyRate,Integer odometerKm,String status){this.registrationNumber=registrationNumber;this.model=model;this.dailyRate=dailyRate;this.odometerKm=odometerKm;this.status=status;}
    public Long getId(){return id;} public String getRegistrationNumber(){return registrationNumber;} public void setRegistrationNumber(String v){registrationNumber=v;}
    public String getModel(){return model;} public void setModel(String v){model=v;} public BigDecimal getDailyRate(){return dailyRate;} public void setDailyRate(BigDecimal v){dailyRate=v;}
    public Integer getOdometerKm(){return odometerKm;} public void setOdometerKm(Integer v){odometerKm=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
