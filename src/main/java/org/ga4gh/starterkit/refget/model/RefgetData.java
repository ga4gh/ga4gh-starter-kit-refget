package org.ga4gh.starterkit.refget.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.ga4gh.starterkit.common.hibernate.HibernateEntity;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "refget_data")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonFilter("refgetDataFilter")
public class RefgetData implements HibernateEntity<String> {

    @Id
    @Column(name = "id",updatable = false, nullable = false)
    @NonNull
    private String id;//md5 column name

    @Column(name = "md5")
    private String md5;

    @Column(name = "trunc512")
    private String trunc512;

    @Column(name = "length")
    private Integer length;

    @Column(name = "sequence")
    private String sequence;

    @Column(name = "iscircular")
    private int iscircular;

    @OneToMany(mappedBy = "refgetData",
            fetch = FetchType.EAGER,
            cascade = javax.persistence.CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
//    @JsonIgnore
    private List<Aliases> aliases;

    /* Constructors */
    public RefgetData(@NonNull String id, String md5, String trunc512, Integer length, String sequence, int iscircular ) {
        this.id = id;
        this.md5 = md5;
        this.trunc512 = trunc512;
        this.length = length;
        this.sequence = sequence;
//        this.aliases = aliases;
        this.iscircular = iscircular;
    }

    /* Default Constructor */

    public RefgetData() {
        aliases = new ArrayList<>();
    }

    /**
     * Fetch relational data that is not loaded automatically (lazy load)
     */
    public void loadRelations() {
        Hibernate.initialize(getAliases());
    }

    /* Getters and Setters */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMd5() { return md5;}

    public void setMd5(String md5) { this.md5 = md5;}

    public String getTrunc512() {
        return trunc512;
    }

    public void setTrunc512(String trunc512) {
        this.trunc512 = trunc512;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public List<Aliases> getAliases() {
        return aliases;
    }

    public void setAliases(List<Aliases> aliases) {
        this.aliases = aliases;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean getIscircular() {
        if (iscircular == 0){
            return false;
        }
        else {
            return true;
        }
    }

    public void setIscircular(boolean iscircular) {
        if (iscircular == true) {
            this.iscircular = 1;
        } else {
            this.iscircular = 0;
        }
    }
}
