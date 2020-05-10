package ro.certificate.manager.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Table
@Entity
public class Signature implements Serializable {

    private static final long serialVersionUID = 9011301425697541713L;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column
    @Id
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String path;

    @NotNull
    private Date creationDate;

    @OneToOne
    private Document document;

    @OneToOne
    private Keystore keystore;
}
