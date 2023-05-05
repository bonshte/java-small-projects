package bg.sofia.uni.fmi.mjt.cocktail.server.response;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;

public record Response(String status, String errorMessage, Cocktail[] cocktails) {
}
