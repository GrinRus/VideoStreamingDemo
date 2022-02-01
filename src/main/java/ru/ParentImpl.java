package ru;

public class ParentImpl extends Parent {
    public ParentImpl(String some) {
        this.some = some;
    }

    public ParentImpl() {
    }

    public String getSome() {
        return some;
    }

    public void setSome(String some) {
        this.some = some;
    }

    private String some;

    @Override
    public String toString() {
        return "ParentImpl{" +
                "some='" + some + '\'' +
                '}';
    }
}
