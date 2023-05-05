package bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionInfoTest {
    @Mock
    private NutritionInfoAPI dataBase;

    @Test
    void testNutritionInfoNegativeArgument() {
        assertThrows(IllegalArgumentException.class , () -> new NutritionInfo(200,-3,-197), "nutritionInfo cannot contain negative values");
    }

    @Test
    void testNutritionInfoDoesNotAddToHundredGrams() {
        assertThrows(IllegalArgumentException.class, () -> new NutritionInfo(20,50,20),"nutritionInfo should contain information for 100 grams");
    }

    @Test
    void testNutritionInfoNotConstructed() {
        assertDoesNotThrow(()-> new NutritionInfo(65,20,15),"Nutrition info does not construct correctly with correct arguments");
    }

    @Test
    void testCaloriesCorrectCalculation() throws UnknownFoodException {
        when(dataBase.getNutritionInfo("chocolate cake")).thenReturn(new NutritionInfo(40, 50, 10));
        NutritionInfo cakeInfo = dataBase.getNutritionInfo("chocolate cake");

        assertEquals(cakeInfo.calories(),
                40 * MacroNutrient.CARBOHYDRATE.calories + 50 * MacroNutrient.FAT.calories + 10 * MacroNutrient.PROTEIN.calories,
                0.001
                ,"wrong calories calculation");
    }

    @Test
    void testNutritionInfoGetters() {
        NutritionInfo cola = new NutritionInfo(80,19,1);
        assertEquals(cola.carbohydrates(),80, 0.001, "getter for calories returns invalid value");
        assertEquals(cola.fats(),19, 0.001, "getter for fats returns invalid value");
        assertEquals(cola.proteins(),1, 0.001, "getter for proteins returns invalid value");
    }

}