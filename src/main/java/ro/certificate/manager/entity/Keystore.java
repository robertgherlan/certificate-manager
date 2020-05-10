package ro.certificate.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table
public class Keystore implements Serializable {

    private static final long serialVersionUID = 8856907110610641L;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column
    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    @JsonIgnore
    @NotEmpty
    private String keyStorePassword;

    @JsonIgnore
    @NotEmpty
    private String privateKeyPassword;

    @NotEmpty
    private String certificateSubject;

    @NotNull
    private Date creationDate;

    @JsonIgnore
    private Date expirationDate;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

}
