package bg.sofia.uni.fmi.mjt.cocktail.server;

import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.InvalidIngredientException;

public record Ingredient(String name, String amount) {
    public static Ingredient of(String input) throws InvalidIngredientException {
        String[] words = input.split("=");
        if (words.length != 2) {
            throw new InvalidIngredientException("invalid format for ingredient");
        }
        return new Ingredient(words[0], words[1]);
    }


}
