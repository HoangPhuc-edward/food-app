package com.example.foodapp.Domain;

public class FoodCart {
    private Foods Food;
    private int Quantity;



    public FoodCart(){

    }
    public FoodCart(Foods food, int quantity) {
        Food = food;
        Quantity = quantity;
    }

    public Foods getFood() {
        return Food;
    }

    public void setFood(Foods food) {
        Food = food;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
