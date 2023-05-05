package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyFoodDiaryTest {

    @Mock
    private NutritionInfoAPI database;

    @InjectMocks
    private DailyFoodDiary diary;


    @Test
     void testDiaryConstructedWithNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new DailyFoodDiary(null),
                "Daily diary with null nutritionInfoAPi created");
    }

    @Test
    void testAddFoodNullFood()  {
        assertThrows(IllegalArgumentException.class,
                () -> diary.addFood(null,"cake",10),
                "should throw IllegalArgumentException when null meal passed to addFood");
    }

    @Test
    void testAddFoodWithNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> diary.addFood(Meal.DINNER,null,10),
                "should throw IllegalArgumentException when null name passed to addFood");
    }

    @Test
    void testAddFoodWithEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> diary.addFood(Meal.LUNCH,"",10),
                "should throw IllegalArgumentException when empty string passed to addFood");
    }

    @Test
    void testAddFoodWithBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> diary.addFood(Meal.LUNCH,"  ",10),
                "should throw IllegalArgumentException when blank string passed to addFood");
    }

    @Test
    void testAddFoodWithNegativeServing() {
        assertThrows(IllegalArgumentException.class,
                () -> diary.addFood(Meal.LUNCH,"pizza",-2),
                "should throw IllegalArgumentException when negative serving passed to addFood");

    }

    @Test
    void testAddFoodCorrect() throws UnknownFoodException{
        when(database.getNutritionInfo("pizza")).thenReturn(new NutritionInfo(55, 25,20));
        assertDoesNotThrow(
                () -> diary.addFood(Meal.LUNCH,"pizza",2),
                "should not throw exception when food added has name and positive serving");

    }

    @Test
    void testAddFoodCorrectFoodEntryCreation() throws UnknownFoodException{
        when(database.getNutritionInfo("cake")).thenReturn(new NutritionInfo(40, 50, 10));
        FoodEntry expectedResult = new FoodEntry("cake",1,new NutritionInfo(40,50,10));
        assertEquals(expectedResult,diary.addFood(Meal.SNACKS, "cake",1),
                "add food creates wrong foodEntry");

    }



    @Test
    void testGetAllFoodEntries() throws UnknownFoodException {
        when(database.getNutritionInfo("cake")).thenReturn(new NutritionInfo(40, 50, 10));
        when(database.getNutritionInfo("pasta")).thenReturn(new NutritionInfo(30, 60, 10));
        when(database.getNutritionInfo("pizza")).thenReturn(new NutritionInfo(70, 10, 20));
        List<FoodEntry> allExpectedEntries = new LinkedList<>();
        FoodEntry cake = new FoodEntry("cake",1,new NutritionInfo(40,50,10));
        FoodEntry pasta = new FoodEntry("pasta",1,new NutritionInfo(30,60,10));
        FoodEntry pizza = new FoodEntry("pizza",2,new NutritionInfo(70,10,20));
        diary.addFood(Meal.SNACKS,"cake",1);
        diary.addFood(Meal.LUNCH,"pizza",2);
        diary.addFood(Meal.DINNER,"pasta",1);
        allExpectedEntries.add(cake);
        allExpectedEntries.add(pasta);
        allExpectedEntries.add(pizza);

        Collection<FoodEntry> foodEaten = diary.getAllFoodEntries();
        assertTrue(foodEaten.size() == allExpectedEntries.size() && allExpectedEntries.containsAll(foodEaten),"food stored stored inside does not match expected");
    }

    @Test
    void testGetAllFoodEntriesEmpty() {
        Collection<FoodEntry> foodEaten = diary.getAllFoodEntries();
        assertEquals(foodEaten.size(), 0,0.001,"no foods were stored inside but list of foods returned");
    }

    @Test
    void testGetAllFoodEntriesByProteinContent() throws UnknownFoodException{
        when(database.getNutritionInfo("cake")).thenReturn(new NutritionInfo(50, 50, 0));
        when(database.getNutritionInfo("pasta")).thenReturn(new NutritionInfo(30, 60, 10));
        when(database.getNutritionInfo("pizza")).thenReturn(new NutritionInfo(70, 10, 20));
        List<FoodEntry> allExpectedEntriesSorted = new LinkedList<>();
        FoodEntry cake = new FoodEntry("cake",1,new NutritionInfo(50,50,0));
        FoodEntry pasta = new FoodEntry("pasta",1,new NutritionInfo(30,60,10));
        FoodEntry pizza = new FoodEntry("pizza",1,new NutritionInfo(70,10,20));
        diary.addFood(Meal.SNACKS,"cake",1);
        diary.addFood(Meal.LUNCH,"pizza",1);
        diary.addFood(Meal.DINNER,"pasta",1);
        allExpectedEntriesSorted.add(cake);
        allExpectedEntriesSorted.add(pasta);
        allExpectedEntriesSorted.add(pizza);
        allExpectedEntriesSorted.sort(new FoodEntryProteinContentComparator());
        List<FoodEntry> foodEatenSortedByProtein = diary.getAllFoodEntriesByProteinContent();

        assertIterableEquals(allExpectedEntriesSorted,foodEatenSortedByProtein,"protein sorting does not work");

    }

    @Test
    void testGetDailyCaloriesIntakeNothingEaten() {
        assertEquals(0,diary.getDailyCaloriesIntake(),0.001,
                "calories intake should be 0 when nothing was eaten");
    }

    @Test
    void testGetDailyCaloriesIntakeByMealNullMeal() {
        assertThrows(IllegalArgumentException.class, () -> diary.getDailyCaloriesIntakePerMeal(null),
                "expected illegal argument exception when null meal passed");
    }

    @Test
    void getGetDailyCaloriesIntakeByMealNoSuchMealFound() {
        assertEquals(0, diary.getDailyCaloriesIntakePerMeal(Meal.BREAKFAST), 0.0001,
                "calories intake for meal should be zero when no such meal was eaten");
    }

    @Test
    void getDailyCaloriesIntakeByMealCorrect() throws UnknownFoodException{
        when(database.getNutritionInfo("cake")).thenReturn(new NutritionInfo(50, 50, 0));
        when(database.getNutritionInfo("pasta")).thenReturn(new NutritionInfo(30, 60, 10));
        when(database.getNutritionInfo("pizza")).thenReturn(new NutritionInfo(70, 10, 20));
        FoodEntry cake = new FoodEntry("cake",1,new NutritionInfo(50,50,0));
        FoodEntry pasta = new FoodEntry("pasta",2,new NutritionInfo(30,60,10));
        FoodEntry pizza = new FoodEntry("pizza",1,new NutritionInfo(70,10,20));
        diary.addFood(Meal.SNACKS,"cake",1);
        diary.addFood(Meal.LUNCH,"pizza",1);
        diary.addFood(Meal.LUNCH,"pasta",2);

        double caloriesFromSnacks = cake.nutritionInfo().calories() * cake.servingSize();
        double caloriesFromLunch = pizza.nutritionInfo().calories() * pizza.servingSize() +
                pasta.nutritionInfo().calories() * pasta.servingSize();


        assertEquals(caloriesFromSnacks,diary.getDailyCaloriesIntakePerMeal(Meal.SNACKS),0.001,"calories from snack not calculated correctly");
        assertEquals(caloriesFromLunch,diary.getDailyCaloriesIntakePerMeal(Meal.LUNCH),0.001,"calories from lunch not calculated correctly");
    }

    @Test
    void getGetDailyCaloriesIntakeCorrectAmount() throws UnknownFoodException{
        when(database.getNutritionInfo("cake")).thenReturn(new NutritionInfo(50, 50, 0));
        when(database.getNutritionInfo("pasta")).thenReturn(new NutritionInfo(30, 60, 10));
        when(database.getNutritionInfo("pizza")).thenReturn(new NutritionInfo(70, 10, 20));
        when(database.getNutritionInfo("soup")).thenReturn(new NutritionInfo(40,50,10));
        FoodEntry cake = new FoodEntry("cake",1,new NutritionInfo(50,50,0));
        FoodEntry pasta = new FoodEntry("pasta",2,new NutritionInfo(30,60,10));
        FoodEntry pizza = new FoodEntry("pizza",1,new NutritionInfo(70,10,20));
        FoodEntry soup = new FoodEntry("soup",2,new NutritionInfo(40,50,10));
        diary.addFood(Meal.SNACKS,"cake",1);
        diary.addFood(Meal.LUNCH,"pizza",1);
        diary.addFood(Meal.LUNCH,"pasta",2);
        diary.addFood(Meal.DINNER,"soup",2);
        double expectedTotalCaloriesForTheDay = 0;
        expectedTotalCaloriesForTheDay += cake.servingSize() * cake.nutritionInfo().calories();
        expectedTotalCaloriesForTheDay += pasta.servingSize() * pasta.nutritionInfo().calories();
        expectedTotalCaloriesForTheDay += pizza.servingSize() * pizza.nutritionInfo().calories();
        expectedTotalCaloriesForTheDay += soup.servingSize() * soup.nutritionInfo().calories();

        assertEquals(diary.getDailyCaloriesIntake(),expectedTotalCaloriesForTheDay,0.001, "calories for the day not calculated correctly");

    }




}