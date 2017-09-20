package com.ibm.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.MainActivity
import com.ibm.shoppinglist.R
import com.ibm.shoppinglist.StateManager

class ShoppingListsRecyclerViewAdapter(private val parent: MainActivity) : RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ViewHolder>() {

    lateinit var shoppingLists: ArrayList<ShoppingListMeta>

    init {
        this.loadShoppingLists()
    }

    class ShoppingListMeta(
            val list: DocumentRevision,
            val itemCount: Int,
            val itemCheckedCount: Int)

    class ViewHolder(private val adapter: ShoppingListsRecyclerViewAdapter, private val card: View) : RecyclerView.ViewHolder(card) {

        val titleTextView: TextView = card.findViewById(R.id.shopping_lists_card_title_text_view)
        val itemsTextView: TextView = card.findViewById(R.id.shopping_lists_card_items_text_view)
        val deleteButton: ImageButton = card.findViewById(R.id.shopping_lists_card_delete_button)

        init {
            this.card.setOnClickListener {
                this.adapter.parent.manageShoppingList(this.adapter.shoppingLists[this.adapterPosition].list)
            }
        }
    }

    fun loadShoppingLists() {
        this.shoppingLists = ArrayList()
        val lists = StateManager.shoppingListRepository.find()
        lists.mapTo(this.shoppingLists) {
            val items = StateManager.shoppingListRepository.findItems(it)
            val itemCount = items.size
            val itemCheckedCount = items.count { it.body.asMap()["checked"] as Boolean }
            ShoppingListMeta(it, itemCount, itemCheckedCount)
        }
    }

    fun updateShoppingLists() {
        this.loadShoppingLists()
        this.notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val card = inflater.inflate(R.layout.shopping_lists_card_view, parent, false)
        return ViewHolder(this, card)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.titleTextView?.text = shoppingLists[position].list.body.asMap()["title"].toString()
        holder?.itemsTextView?.text = "%d of %d item(s) checked.".format(shoppingLists[position].itemCheckedCount, shoppingLists[position].itemCount)
        holder?.deleteButton?.setOnClickListener {
            this.parent.deleteShoppingList(this.shoppingLists[position].list)
        }
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}