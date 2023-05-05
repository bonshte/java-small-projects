package bg.sofia.uni.fmi.mjt.cocktail.command;

import java.util.List;

public record Command(String name, List<String> arguments) {
}
