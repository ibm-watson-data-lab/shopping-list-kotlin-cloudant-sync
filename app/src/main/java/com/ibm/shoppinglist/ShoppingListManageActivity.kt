package com.ibm.shoppinglist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ibm.shoppinglist.view.ShoppingListRecyclerViewAdapter

class ShoppingListManageActivity : AppCompatActivity(), SyncListener {

    private lateinit var shoppingListAdapter : ShoppingListRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_manage)

        // Load the shopping lists
        this.shoppingListAdapter = ShoppingListRecyclerViewAdapter(StateManager.shoppingListRepository.findItems())

        // Initialize RecyclerView
        val recyclerView = findViewById(R.id.shopping_list_recycler_view) as RecyclerView
        recyclerView.adapter = this.shoppingListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    public override fun onResume() {
        super.onResume()
        this.shoppingListAdapter.updateShoppingItemList()
        SyncManager.syncListener = this
    }

    override fun onSyncComplete() {
        this.runOnUiThread {
            val shoppingLists = StateManager.shoppingListRepository.find(hashMapOf("_id" to StateManager.activeShoppingList!!.id))
            if (shoppingLists.isNotEmpty()) {
                StateManager.activeShoppingList = shoppingLists[0]
                this.shoppingListAdapter.updateShoppingItemList()
            }
        }
    }
}
