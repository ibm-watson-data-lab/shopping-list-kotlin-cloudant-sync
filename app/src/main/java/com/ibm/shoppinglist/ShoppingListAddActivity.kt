package com.ibm.shoppinglist

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
//import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import com.ibm.shoppinglist.model.ShoppingListFactory

class ShoppingListAddActivity : AppCompatActivity() {

    lateinit var parentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_add)
        parentLayout = findViewById(android.R.id.content)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val editText = findViewById(R.id.shopping_list_add_edit_text) as EditText
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ -> save(editText.text.toString()) }
    }

    private fun save(title: String) {
//        val snackbar = Snackbar.make(this.parentLayout, "Adding shopping list...", Snackbar.LENGTH_LONG)
//        snackbar.show()
        val shoppingList = ShoppingListFactory.newShoppingList(title)
        StateManager.shoppingListRepository.put(shoppingList)
        this.finish()
    }

}
