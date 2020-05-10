package ro.certificate.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import ro.certificate.manager.annotation.UniqueEmail;
import ro.certificate.manager.annotation.UniqueUsername;
import ro.certificate.manager.utils.ErrorMessageBundle;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table
public class User implements Serializable {

    private static final long serialVersionUID = 9093304836112847216L;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column
    @Id
    private String id;

    @UniqueUsername(message = ErrorMessageBundle.USERNAME_ALREADY_EXIST)
    @NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
    private String username;

    @UniqueEmail(message = ErrorMessageBundle.EMAIL_ALREADY_EXIST)
    @Email(message = ErrorMessageBundle.INVALID_FIELD)
    @NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
    private String email;

    @NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
    private String firstname;

    @NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
    private String lastname;

    @JsonIgnore
    @NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
    @Size(min = 8, max = 256, message = "Password must have between 8 and 256 characters.")
    private String password;

    @JsonIgnore
    @Column(unique = true)
    private String registerToken;

    @JsonIgnore
    @Column(unique = true)
    private String recoverPasswordToken;

    @JsonIgnore
    private Date creationDate;

    @JsonIgnore
    private Date expiredDate;

    @JsonIgnore
    private boolean expired = false;

    @JsonIgnore
    private boolean enabled = false;

    @JsonIgnore
    private boolean validEmail = false;

    @JsonIgnore
    private String registerIpAddress = null;

    @ManyToMany(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinTable
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Keystore> keyStores;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> documents;

}
