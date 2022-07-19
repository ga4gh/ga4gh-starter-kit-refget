package org.ga4gh.starterkit.refget.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.ga4gh.starterkit.common.hibernate.HibernateEntity;
import org.springframework.lang.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "aliases")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Aliases implements Serializable, HibernateEntity<Long> {

    public static final long serialVersionUID = 1L;
    /**
     * unique identifier
     */
    @Id
    @Column(name = "alias_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "alias")
    @NonNull
    private String alias;

    @Column(name = "naming_authority")
    private String naming_authority;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "sequence_id",  nullable = false, insertable = true, updatable = true)
//    @JsonBackReference
    @JsonIgnore
    private RefgetData refgetData;

    /* Constructor */

    public Aliases() {
    }

    public Aliases(Long id,String alias, String naming_authority) {
        this.id = id;
//        this.sequence_id = sequence_id;
        this.alias = alias;
        this.naming_authority = naming_authority;
    }

    /**
     * Fetch relational data that is not loaded automatically (lazy load)
     */
    public void loadRelations() {

    }

    @Override
    public String toString() {
        return "Aliases{" +
                "alias='" + alias + '\'' +
                ", naming_authority='" + naming_authority + '\'' +
                '}';
    }
    /* Getters and Setters */

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNaming_authority() {
        return naming_authority;
    }

    public void setNaming_authority(String naming_authority) {
        this.naming_authority = naming_authority;
    }

    public RefgetData getRefgetData() {
        return refgetData;
    }

    public void setRefgetData(RefgetData refgetData) {
        this.refgetData = refgetData;
    }

}
