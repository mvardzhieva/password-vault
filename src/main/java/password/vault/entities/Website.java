package password.vault.entities;

import java.util.Objects;

public record Website(String name, User user) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website = (Website) o;
        return name.equals(website.name) &&
                user.equals(website.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }
}
