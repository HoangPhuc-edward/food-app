package com.example.foodapp.Listener;

import com.example.foodapp.Domain.Foods;

public interface ListFoodListener {
    void navigateToDetailFood(Foods food);
    void startAddFoodAnimation(Foods foods, int x, int y);
}
