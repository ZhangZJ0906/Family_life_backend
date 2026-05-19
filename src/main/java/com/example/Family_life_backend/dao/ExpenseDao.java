package com.example.Family_life_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.Expense;

@Repository
public interface ExpenseDao extends JpaRepository<Expense, Integer> {

}
