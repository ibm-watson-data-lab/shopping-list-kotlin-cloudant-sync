package com.ibm.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.R
import com.ibm.shoppinglist.StateManager

class ShoppingListRecyclerViewAdapter(private var shoppingListItems: List<DocumentRevision>) : RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder>() {

    var newItemText: String = ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.shopping_list_card_check_box)
        val textView: TextView = view.findViewById(R.id.shopping_list_card_text_view)
        val editText: EditText = view.findViewById(R.id.shopping_list_card_edit_text)
        val deleteButton: ImageButton = view.findViewById(R.id.shopping_list_card_delete_button)
    }

    private fun addItem(title: String) {
        StateManager.datastore.addItem(title)
        this.updateShoppingItemList()
    }

    private fun updateItemChecked(itemIndex: Int, checked: Boolean) {
        val newItem = StateManager.datastore.updateItemChecked(this.shoppingListItems[itemIndex], checked)
        val newItems = ArrayList<DocumentRevision>()
        this.shoppingListItems.indices.mapTo(newItems) { if (it == itemIndex) newItem else this.shoppingListItems[it] }
        this.shoppingListItems = newItems
        this.notifyItemChanged(itemIndex)
        this.notifyItemRangeChanged(itemIndex, 1)
    }

    private fun deleteItem(itemIndex: Int) {
        StateManager.datastore.deleteItem(this.shoppingListItems[itemIndex])
        val newItems = ArrayList<DocumentRevision>()
        this.shoppingListItems.indices.mapNotNullTo(newItems) { if (it == itemIndex) null else this.shoppingListItems[it] }
        this.shoppingListItems = newItems
        this.notifyItemRemoved(itemIndex)
        this.notifyItemRangeChanged(itemIndex, this.shoppingListItems.size)
    }

    fun updateShoppingItemList() {
        this.shoppingListItems = StateManager.datastore.loadItems(StateManager.activeShoppingList!!)
        this.notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val card = inflater.inflate(R.layout.shopping_list_card_view, parent, false)
        return ViewHolder(card)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        // disable all events first
        // this is particularly important for checkbox
        // as setting isChecked will trigger
        holder?.checkbox?.setOnCheckedChangeListener(null)
        holder?.editText?.setOnKeyListener(null)
        holder?.deleteButton?.setOnClickListener(null)
        if (position < this.shoppingListItems.size) {
            holder?.checkbox?.isEnabled = true
            holder?.checkbox?.isChecked = shoppingListItems[position].body.asMap()["checked"] as Boolean
            holder?.checkbox?.setOnCheckedChangeListener { _, checked ->
                this.updateItemChecked(position, checked)
            }
            holder?.textView?.visibility = View.VISIBLE
            holder?.textView?.text = shoppingListItems[position].body.asMap()["title"] as String
            holder?.editText?.visibility = View.GONE
            holder?.deleteButton?.visibility = View.VISIBLE
            holder?.deleteButton?.setOnClickListener {
                this.deleteItem(position)
            }
        }
        else {
            holder?.checkbox?.isEnabled = false
            holder?.checkbox?.isChecked = false
            holder?.textView?.visibility = View.GONE
            holder?.editText?.visibility = View.VISIBLE
            holder?.editText?.setText(this.newItemText)
            holder?.editText?.setOnKeyListener { _, keyCode, event ->
                if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (holder.editText.text.isNotEmpty()) {
                        addItem(holder.editText.text.toString())
                        holder.editText.text.clear()
                        this.newItemText = ""
                    }
                    true
                }
                else {
                    this.newItemText = holder.editText.text.toString()
                }
                false
            }
            holder?.deleteButton?.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return shoppingListItems.size + 1
    }
}