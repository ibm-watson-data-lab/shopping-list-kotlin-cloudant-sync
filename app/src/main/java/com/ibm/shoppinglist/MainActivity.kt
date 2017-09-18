package com.ibm.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.ibm.shoppinglist.model.ShoppingListsRecyclerViewAdapter
import com.cloudant.sync.documentstore.DocumentStoreException
import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var shoppingListsAdapter: ShoppingListsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // Initialize the database
        val path = applicationContext.getDir("documentstores", Context.MODE_PRIVATE)
        try {
            StateManager.ds = DocumentStore.getInstance(File(path, "shopping-list"))
        } catch (dse: DocumentStoreException) {
            // TODO:???
            System.err.println("Problem opening or accessing DocumentStore: " + dse)
        }

        // Load the shopping lists
        val recyclerView = findViewById(R.id.shopping_lists_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        this.shoppingListsAdapter = ShoppingListsRecyclerViewAdapter(this.loadShoppingLists())
        recyclerView.adapter = this.shoppingListsAdapter
        recyclerView.layoutManager = layoutManager

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ -> startActivity(Intent(this, ShoppingListAddActivity::class.java)) }


    }

    private fun loadShoppingLists() : List<DocumentRevision> {
        val shoppingLists = ArrayList<DocumentRevision>()
        try {
            val query = HashMap<String, Any>()
            query.put("type", "list")
            val result = StateManager.ds.query().find(query)
            shoppingLists += result
        } catch (dse: DocumentStoreException) {
            // TODO:???
            System.err.println("Problem opening or accessing DocumentStore: " + dse)
        }
        return shoppingLists
    }

    public override fun onResume() {  // After a pause OR at startup
        super.onResume()
        this.shoppingListsAdapter.updateShoppingLists(this.loadShoppingLists())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }
}
