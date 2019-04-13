package com.example.exploreyourcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.exploreyourcity.adapters.CategoryAdapter;
import com.example.exploreyourcity.models.Category;

import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryListener {

    private static final String TAG = "CategoryListActivity";
    
    private ArrayList<Category> categories = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        categories.add(new Category(1, "Academia"));
        categories.add(new Category(2, "Food & Drink"));
        categories.add(new Category(3, "Recreation"));
        categories.add(new Category(4, "Health"));
        categories.add(new Category(5, "Shopping"));
        categories.add(new Category(6, "Religion"));
        initRecyclerView();

    }

    private void initRecyclerView() {
        // Add list elements to RecyclerView
        recyclerView = findViewById(R.id.category_list_recycler_view);
        categoryAdapter = new CategoryAdapter(categories, this);
        recyclerView.setAdapter(categoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onCategoryClick(int position) {
        Category category = categories.get(position);

        Log.d(TAG, "Clicked on category " + category.toString());

        Intent missionListIntent = new Intent(getApplicationContext(), MissionListActivity.class);
        missionListIntent.putExtra("CATEGORY_ID", category.getId());
        missionListIntent.putExtra("MODE", "AVAILABLE");
        startActivity(missionListIntent);
    }
}
