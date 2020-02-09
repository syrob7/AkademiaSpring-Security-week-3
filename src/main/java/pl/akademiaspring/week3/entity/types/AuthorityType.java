package pl.akademiaspring.week3.entity.types;

public enum  AuthorityType {
    ROLE_ADMIN,
    ROLE_USER;

    public String getName() {
        return this.name();
    }
}
