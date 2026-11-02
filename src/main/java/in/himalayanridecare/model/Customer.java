package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="customers")
public class Customer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String name;
    @NotBlank @Size(max=20) @Column(nullable=false,length=20) private String phone;
    @Email @Size(max=120) @Column(length=120,unique=true) private String email;
    protected Customer() {}
    public Customer(String name,String phone,String email){this.name=name;this.phone=phone;this.email=email;}
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
}
