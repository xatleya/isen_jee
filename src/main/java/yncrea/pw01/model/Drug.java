package yncrea.pw01.model;

public class Drug {
    private String name;
    private String lab;

    public Drug() {

    }

    public Drug(String name, String lab) {
        this.name = name;
        this.lab = lab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drug drug = (Drug) o;

        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return lab != null ? lab.equals(drug.lab) : drug.lab == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lab != null ? lab.hashCode() : 0);
        return result;
    }
}
