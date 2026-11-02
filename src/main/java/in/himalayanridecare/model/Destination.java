package in.himalayanridecare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity @Table(name="destinations")
public class Destination {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String name;
    @NotBlank @Size(max=500) @Column(nullable=false,length=500) private String description;
    protected Destination() {}
    public Destination(String name,String description){this.name=name;this.description=description;}
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
}
