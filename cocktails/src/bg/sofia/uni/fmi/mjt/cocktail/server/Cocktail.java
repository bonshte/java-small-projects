package bg.sofia.uni.fmi.mjt.cocktail.server;

import java.util.Objects;
import java.util.Set;

public record Cocktail(String name, Set<Ingredient> ingredients) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cocktail cocktail)) return false;
        return Objects.equals(name, cocktail.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}
