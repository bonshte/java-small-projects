package bg.sofia.uni.fmi.mjt.client.dto;

import java.util.Objects;

public class Source {
    private String id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Source source)) return false;
        return Objects.equals(id, source.id) && Objects.equals(name, source.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
