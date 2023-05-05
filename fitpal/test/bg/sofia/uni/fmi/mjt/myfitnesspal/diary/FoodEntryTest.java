package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class FoodEntryTest {

    @Test
    void testFoodEntryNullString() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodEntry(null,10, new NutritionInfo(60,20,20)),
                "food entry with null string constructed");
    }
    @Test
    void testFoodEntryBlankString() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodEntry("  ",10, new NutritionInfo(60, 20 ,20)),
                "food entry with blank name constructed");
    }
    @Test
    void testFoodEntryEmptyString() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodEntry("",10, new NutritionInfo(60, 20 ,20)),
                "food entry with empty name constructed");
    }

    @Test
    void testFoodEntryWithNegativeServing() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodEntry("pasta",-5, new NutritionInfo(50, 30 ,20)),
                "food entry with empty name constructed");
    }

    @Test
    void testFoodEntryWithZeroServing() {
        assertThrows(IllegalArgumentException.class,
                () -> new FoodEntry("pasta",0, new NutritionInfo(50, 30 ,20)),
                "food entry with empty name constructed");
    }

    @Test
    void testFoodEntryNullNutritionInfo() {
        assertThrows(IllegalArgumentException.class,() -> new FoodEntry("cake", 10, null),
                "food with null NutritionInfo constructed");

    }

    @Test
    void testFoodEntryCorrectArguments() {
        assertDoesNotThrow(() -> new FoodEntry("Tiramisu",
                0.5, new NutritionInfo(40,40,20)),
                "food entry with correct arguments throws exception");
    }

    @Test void testFoodEntryGetters() {
        FoodEntry threeCakes = new FoodEntry("cake",3, new NutritionInfo(40,40,20));

        assertEquals("cake", threeCakes.food(), "food name getter does not work");
        assertEquals(3, threeCakes.servingSize(), "serving size getter does not match");
    }
}