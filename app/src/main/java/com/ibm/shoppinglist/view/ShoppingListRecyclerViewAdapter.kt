package com.ibm.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.R
import com.ibm.shoppinglist.StateManager
import com.ibm.shoppinglist.model.ShoppingListFactory

class ShoppingListRecyclerViewAdapter(private var shoppingListItems: List<DocumentRevision>) : RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.shopping_list_card_check_box)
        val textView: TextView = view.findViewById(R.id.shopping_list_card_text_view)
        val editText: EditText = view.findViewById(R.id.shopping_list_card_edit_text)
    }

    private fun addItem(title: String) {
        val item = ShoppingListFactory.newShoppingListItem(title, StateManager.activeShoppingList!!)
        StateManager.shoppingListRepository.putItem(item)
        this.updateShoppingItemList()
    }

    private fun updateItemChecked(itemIndex: Int, checked: Boolean) {
        var item = this.shoppingListItems[itemIndex]
        var body = item.body.asMap()
        body["checked"] = checked
        item.body = DocumentBodyFactory.create(body)
        val newItem = StateManager.shoppingListRepository.putItem(item)
        val newItems = ArrayList<DocumentRevision>()
        this.shoppingListItems.indices.mapTo(newItems) { if (it == itemIndex) newItem else this.shoppingListItems[it] }
        this.shoppingListItems = newItems
    }

    fun updateShoppingItemList() {
        this.shoppingListItems = StateManager.shoppingListRepository.findItems(hashMapOf("type" to "item", "list" to StateManager.activeShoppingList!!.id))
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
        if (position < this.shoppingListItems.size) {
            holder?.checkbox?.isEnabled = true
            holder?.checkbox?.isChecked = shoppingListItems[position].body.asMap()["checked"] as Boolean
            holder?.checkbox?.setOnCheckedChangeListener { _, checked ->
                this.updateItemChecked(position, checked)
            }
            holder?.textView?.visibility = View.VISIBLE
            holder?.textView?.text = shoppingListItems[position].body.asMap()["title"] as String
            holder?.editText?.visibility = View.GONE
            holder?.editText?.setOnKeyListener(null)
        }
        else {
            holder?.checkbox?.isEnabled = false
            holder?.checkbox?.isChecked = false
            holder?.checkbox?.setOnCheckedChangeListener(null)
            holder?.textView?.visibility = View.GONE
            holder?.editText?.visibility = View.VISIBLE
            holder?.editText?.setOnKeyListener { _, keyCode, event ->
                if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (holder.editText.text.isNotEmpty()) {
                        addItem(holder.editText.text.toString())
                        holder.editText.text.clear()
                    }
                    true
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return shoppingListItems.size + 1
    }
}