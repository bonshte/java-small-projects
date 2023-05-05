package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.ordering.ExpirationDateComparator;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SmartFridge implements SmartFridgeAPI {
    private final int totalCapacity;
    private int currentCapacity;
    private Map<String, List<Storable>> itemsContained;
    //List can be replaced with Queue, the queue will be priority
    private void validateItemName(String itemName) {
        if (itemName == null || itemName.isEmpty() || itemName.isBlank()) {
            throw new IllegalArgumentException("itemName passed is not valid");
        }
    }

    public SmartFridge(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        itemsContained = new HashMap<>();
    }

    @Override
    public <E extends Storable> void store(E item, int quantity) throws FridgeCapacityExceededException {
        if (item == null || quantity < 1) {
            throw new IllegalArgumentException("null item or negative capacity");
        }

        if (currentCapacity + quantity > totalCapacity) {
            throw new FridgeCapacityExceededException("no space left");
        }


        if (itemsContained.containsKey(item.getName())) {
            for (int i = 0; i < quantity; ++i) {
                itemsContained.get(item.getName()).add(item);
            }
        } else {
            List<Storable> itemsMatching = new LinkedList<>();
            for (int i = 0; i < quantity; ++i) {
                itemsMatching.add(item);
            }
            itemsContained.put(item.getName(), itemsMatching);
        }
        currentCapacity += quantity;

    }

    @Override
    public int getQuantityOfItem(String itemName) {
        validateItemName(itemName);

        if (itemsContained.containsKey(itemName)) {
            return itemsContained.get(itemName).size();
        }
        return 0;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName, int quantity) throws InsufficientQuantityException {
        validateItemName(itemName);

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity requested must be positive");
        }

        if (getQuantityOfItem(itemName) < quantity) {
            throw new InsufficientQuantityException("not enough quantity of the requested item");
        }

        itemsContained.get(itemName).sort(new ExpirationDateComparator());
        List<Storable> requested = new LinkedList<>();
        for (int i = 0; i < quantity; ++i) {
            requested.add(itemsContained.get(itemName).remove(0));
        }
        currentCapacity -= quantity;
        return requested;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName) {
        validateItemName(itemName);

        int quantity = getQuantityOfItem(itemName);
        if (quantity == 0) {
            return new ArrayList<>(0);
        }
        try {
            return retrieve(itemName, quantity);
        } catch ( InsufficientQuantityException e) {
            throw new ConcurrentModificationException("concurrent access to items in fridge");
        }
    }
    private List<? extends Storable> extractExpired(String itemName) {
        List<Storable> result = new LinkedList<>();
        for ( var it = itemsContained.get(itemName).iterator(); it.hasNext();) {
            var item = it.next();
            if (item.isExpired()) {
                result.add(item);
                it.remove();
                currentCapacity--;
            }
        }
        return result;
    }

    @Override
    public List<? extends Storable> removeExpired() {
        List<Storable> result = new LinkedList<>();
        Set<String> listOfGoodsNames  = itemsContained.keySet();
        for (String itemName : listOfGoodsNames) {
            result.addAll(extractExpired(itemName));
        }
        return result;

    }

    //last 2 are not working
    private List<Ingredient<? extends Storable>> getInsufficientAndMissing(Ingredient<? extends Storable> ingredient) {
        int countNeeded = ingredient.quantity();
        List<Ingredient<? extends Storable>> insufficientOrMissing = new LinkedList<>();
        if (itemsContained.containsKey(ingredient.item().getName())) {
            for (var good : itemsContained.get(ingredient.item().getName())) {
                if (good.isExpired()) {
                    insufficientOrMissing.add(new DefaultIngredient<>(good, 1));
                } else {
                    countNeeded--;
                }
            }
        }

        if (countNeeded > 0) {
            insufficientOrMissing.add(new DefaultIngredient<>(ingredient.item(), countNeeded));
        }
        return insufficientOrMissing;

    }

    @Override
    public Iterator<Ingredient<? extends Storable>> getMissingIngredientsFromRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("recipe is null");
        }

        List<Ingredient<? extends Storable>>  insufficientIngredients = new LinkedList<>();

        for (var ingredient : recipe.getIngredients()) {

            insufficientIngredients.addAll(getInsufficientAndMissing(ingredient));
        }

        return insufficientIngredients.iterator();
    }
}
