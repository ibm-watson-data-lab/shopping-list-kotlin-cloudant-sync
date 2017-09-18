package com.ibm.shoppinglist.view

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.R
import com.ibm.shoppinglist.ShoppingListManageActivity
import com.ibm.shoppinglist.StateManager

class ShoppingListsRecyclerViewAdapter(private val activity: Activity, private var shoppingLists: List<DocumentRevision>) : RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(card: View) : RecyclerView.ViewHolder(card) {

        val toolbar: Toolbar = card.findViewById(R.id.shopping_lists_card_toolbar)

        init {
            // inflate your menu
            toolbar.inflateMenu(R.menu.menu_shopping_list_card)
        }
    }

    fun updateShoppingLists() {
        this.shoppingLists = StateManager.shoppingListRepository.find()
        this.notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val card = inflater.inflate(R.layout.shopping_lists_card_view, parent, false)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.toolbar?.title = shoppingLists[position].body.asMap()["title"] as String
        holder?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_manage -> this.activity.startActivity(Intent(this.activity, ShoppingListManageActivity::class.java))
                R.id.action_delete -> {
                    StateManager.shoppingListRepository.delete(this.shoppingLists[position])
                    this.updateShoppingLists()
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}