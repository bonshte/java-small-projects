package bg.sofia.uni.fmi.mjt.cocktail.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.response.Response;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.InvalidIngredientException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandExecutor {
    private static final String ERROR_STATUS = "ERROR";
    private static final String OK_STATUS = "OK";

    private static final String CREATED_STATUS = "CREATED";
    private static final String GET = "get";
    private static final String GET_ALL = "all";
    private static final String GET_BY_NAME = "by-name";
    private static final String GET_BY_INGREDIENT = "by-ingredient";
    private static final String CREATE = "create";
    private CocktailStorage storage;


    public CommandExecutor(CocktailStorage storage) {
        this.storage = storage;
    }

    public Response handleCommand(Command command) {
        if (command.name() == null) {
            return new Response(ERROR_STATUS, "Unknown command", null);
        }
        List<String> arguments  = command.arguments();
        if (command.name().equals(GET)) {

            if (arguments == null || arguments.isEmpty()) {
                return new Response(ERROR_STATUS, "get command needs parameters", null);
            }
            String getMethod = arguments.get(0);
            if (getMethod.equals(GET_ALL)) {
                return new Response(OK_STATUS, null , storage.getCocktails().toArray(new Cocktail[0]));
            } else if (getMethod.equals(GET_BY_NAME)) {
                if (arguments.size() < 2) {
                    return new Response(ERROR_STATUS, "argument not passed to get by name", null);
                }
                try {
                    Cocktail[] cocktail = { storage.getCocktail(arguments.get(1))};
                    return new Response(OK_STATUS, null, cocktail );
                } catch (CocktailNotFoundException e) {
                    return new Response(ERROR_STATUS, "no such cocktail found", null);
                }
            } else if (getMethod.equals(GET_BY_INGREDIENT)) {
                if (arguments.size() < 2) {
                    return new Response(ERROR_STATUS, "argument not passed to get by ingredient", null);
                }
                String ingredient = arguments.get(1);
                Collection<Cocktail> cocktails = storage.getCocktailsWithIngredient(ingredient);
                return new Response(OK_STATUS, null , cocktails.toArray(cocktails.toArray(new Cocktail[0])));
            }

        } else if (command.name().equals(CREATE)) {
            if (arguments == null || arguments.isEmpty()) {
                return new Response(ERROR_STATUS, "create command needs parameters", null);
            }
            String cocktailName = arguments.get(0);
            if (arguments.size() < 2) {
                return new Response(ERROR_STATUS, "creating a cocktail requires ingredients", null);
            }
            Set<Ingredient> ingredients = new HashSet<>();
            try {
                for (int i = 1 ; i < arguments.size(); ++i) {
                    ingredients.add(Ingredient.of(arguments.get(i)));
                }
                Cocktail newCocktail = new Cocktail(cocktailName, ingredients);
                storage.createCocktail(newCocktail);
                return new Response(CREATED_STATUS, null, null);
            } catch (InvalidIngredientException e) {
                return new Response(ERROR_STATUS, "invalid ingredient", null);
            } catch (CocktailAlreadyExistsException x) {
                return new Response(ERROR_STATUS, "already exists", null);
            }
        }
        return new Response(ERROR_STATUS, "Unknown command", null);
    }
}
