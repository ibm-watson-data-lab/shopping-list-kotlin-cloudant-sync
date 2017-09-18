package com.ibm.shoppinglist.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.R
import android.R.attr.data



class ShoppingListsRecyclerViewAdapter(private var shoppingLists: List<DocumentRevision>) : RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(card: View) : RecyclerView.ViewHolder(card) {
        val textView: TextView = card.findViewById(R.id.shopping_lists_card_text_view)
    }

    fun updateShoppingLists(shoppingLists: List<DocumentRevision>) {
        this.shoppingLists = shoppingLists
        this.notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ShoppingListsRecyclerViewAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.shopping_lists_card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.textView?.text = shoppingLists[position].body.asMap()["title"] as String
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}