package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;

import java.util.Objects;

public abstract class AbstractStorableItem implements Storable {
    private final String name;
    private final StorableType type;
    private final LocalDate expiration;

    public AbstractStorableItem(String name, StorableType type, LocalDate expiration) {
        this.name = name;
        this.type = type;
        this.expiration = expiration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getExpiration() {
        return expiration;
    }

    @Override
    public StorableType getType() {
        return type;
    }

    @Override
    public boolean isExpired() {
        return expiration.isAfter(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractStorableItem that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
