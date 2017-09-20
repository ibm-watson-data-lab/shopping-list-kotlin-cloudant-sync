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
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.view.ShoppingListsRecyclerViewAdapter
import com.cloudant.sync.documentstore.DocumentStore
import com.ibm.shoppinglist.model.ShoppingListRepository
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), SyncListener {

    private lateinit var shoppingListsAdapter: ShoppingListsRecyclerViewAdapter
    var runSync = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // Initialize the database
        val documentStorePath = applicationContext.getDir("documentstores", Context.MODE_PRIVATE)
        val documentStore = DocumentStore.getInstance(File(documentStorePath, "shopping-list"))
        StateManager.shoppingListRepository = ShoppingListRepository(documentStore)

        // Load the shopping lists
        this.shoppingListsAdapter = ShoppingListsRecyclerViewAdapter(this)

        // Initialize RecyclerView
        val recyclerView = findViewById(R.id.shopping_lists_recycler_view) as RecyclerView
        recyclerView.adapter = this.shoppingListsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ -> startActivity(Intent(this, ShoppingListAddActivity::class.java)) }
    }

    override fun onSyncComplete() {
        this.runOnUiThread {
            this.shoppingListsAdapter.updateShoppingLists()
        }
    }

    fun manageShoppingList(shoppingList: DocumentRevision) {
        StateManager.activeShoppingList = shoppingList
        this.startActivity(Intent(this, ShoppingListManageActivity::class.java))
    }

    fun deleteShoppingList(shoppingList: DocumentRevision) {
        StateManager.shoppingListRepository.delete(shoppingList)
        this.shoppingListsAdapter.updateShoppingLists()
    }

    public override fun onResume() {  // After a pause OR at startup
        super.onResume()
        StateManager.activeShoppingList = null
        this.shoppingListsAdapter.updateShoppingLists()
        SyncManager.syncListener = this
        SyncManager.startSync()
    }

    public override fun onDestroy() {
        super.onDestroy()
        SyncManager.stopSync()
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
