package com.ineric;

import java.util.List;
import java.util.function.Consumer;

public class PassageOptions {
    private Integer depth;
    private String mask;
    private Consumer<List<String>> results;

    public PassageOptions(){

    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setResults(Consumer<List<String>> results) {
        this.results = results;
    }


    public Consumer<List<String>> getResults() {
        return results;
    }


    public Integer getDepth() {
        return depth;
    }


    public String getMask() {
        return mask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassageOptions)) return false;

        PassageOptions that = (PassageOptions) o;

        if (getDepth() != that.getDepth()) return false;
        return getMask() != null ? getMask().equals(that.getMask()) : that.getMask() == null;
    }

    @Override
    public int hashCode() {
        int result = getDepth();
        result = 31 * result + (getMask() != null ? getMask().hashCode() : 0);
        return result;
    }
}
