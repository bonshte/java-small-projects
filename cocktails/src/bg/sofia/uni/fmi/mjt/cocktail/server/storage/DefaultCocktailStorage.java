package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultCocktailStorage implements CocktailStorage {
    private Set<Cocktail> cocktails;

    public DefaultCocktailStorage() {
        this.cocktails = new HashSet<>();
    }

    @Override
    public synchronized void createCocktail(Cocktail cocktail) throws CocktailAlreadyExistsException {
        if (cocktails.contains(cocktail)) {
            throw new CocktailAlreadyExistsException("recipe for this cocktail already exists");
        }
        cocktails.add(cocktail);
    }

    @Override
    public synchronized Set<Cocktail> getCocktails() {
        return cocktails;
    }

    @Override
    public synchronized Collection<Cocktail> getCocktailsWithIngredient(String ingredientName) {
        return cocktails.stream()
                .filter(x -> x.ingredients().stream().anyMatch(i -> i.name().equals(ingredientName)))
                .toList();
    }

    @Override
    public synchronized Cocktail getCocktail(String name) throws CocktailNotFoundException {
        Optional<Cocktail> desiredCocktail = cocktails.stream().filter(x -> x.name().equals(name)).findFirst();
        if (desiredCocktail.isEmpty()) {
            throw new CocktailNotFoundException("no such cocktail found");
        }
        return desiredCocktail.get();
    }
}
