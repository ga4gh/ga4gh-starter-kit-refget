package org.ga4gh.starterkit.refget.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public class RefgetInputSequence {

    @NotNull
    private String sequence;

    @NotNull
    private List<Aliases> aliases;

    @NotNull
    private int iscircular;

    public RefgetInputSequence(String sequence, List<Aliases> aliases, int iscircular) {
        this.sequence = sequence;
        this.aliases = aliases;
        this.iscircular = iscircular;
    }

    public RefgetInputSequence() {
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<Aliases> getAliases() {
        return aliases;
    }

    public void setAliases(List<Aliases> aliases) {
        this.aliases = aliases;
    }

    public int getIscircular() {
        return iscircular;
    }

    public void setIscircular(int iscircular) {
        this.iscircular = iscircular;
    }

    @Override
    public String toString() {
        return "RefgetInputSequence{" +
                "sequence='" + sequence + '\'' +
                ", aliases=" + aliases +
                ", iscircular=" + iscircular +
                '}';
    }
}
