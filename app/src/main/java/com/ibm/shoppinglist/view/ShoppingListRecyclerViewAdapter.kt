package com.ibm.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.R

class ShoppingListRecyclerViewAdapter(private var shoppingListItems: List<DocumentRevision>) : RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.shopping_list_card_check_box)
        val textView: TextView = view.findViewById(R.id.shopping_list_card_text_view)
    }

    fun updateShoppingItemList() {
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
        holder?.checkbox?.isChecked = shoppingListItems[position].body.asMap()["checked"] as Boolean
        holder?.textView?.text = shoppingListItems[position].body.asMap()["title"] as String
    }

    override fun getItemCount(): Int {
        return shoppingListItems.size
    }
}