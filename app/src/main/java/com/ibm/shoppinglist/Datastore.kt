package com.ibm.shoppinglist

import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.model.ShoppingListDetails
import com.ibm.shoppinglist.model.ShoppingListFactory
import com.ibm.shoppinglist.model.ShoppingListRepository

class Datastore(val shoppingListRepository: ShoppingListRepository) {

    fun loadLists() : List<ShoppingListDetails> {
        val lists = ArrayList<ShoppingListDetails>()
        val allLists = this.shoppingListRepository.find()
        allLists.mapTo(lists) {
            val items = this.shoppingListRepository.findItems(it)
            val itemCount = items.size
            val itemCheckedCount = items.count { it.body.asMap()["checked"] as Boolean }
            ShoppingListDetails(it, itemCount, itemCheckedCount)
        }
        return lists.toList()
    }

    fun loadList(listId: String) : DocumentRevision? {
        val shoppingLists = this.shoppingListRepository.find(hashMapOf("_id" to listId))
        return if (shoppingLists.isNotEmpty()) {
            shoppingLists[0]
        }
        else {
            null
        }
    }

    fun addList(title: String) : DocumentRevision {
        val shoppingList = ShoppingListFactory.newShoppingList(title)
        return this.shoppingListRepository.put(shoppingList)
    }

    fun deleteList(list: DocumentRevision) : DocumentRevision {
        return this.shoppingListRepository.deleteItem(list)
    }

    fun loadItems(list: DocumentRevision) : List<DocumentRevision> {
        return this.shoppingListRepository.findItems(hashMapOf<String,Any>("type" to "item", "list" to list.id))
    }

    fun addItem(title: String) : DocumentRevision {
        val item = ShoppingListFactory.newShoppingListItem(title, StateManager.activeShoppingList!!)
        return this.shoppingListRepository.putItem(item)
    }

    fun updateItemChecked(item: DocumentRevision, checked: Boolean) : DocumentRevision {
        // create a copy of the item
        val body = item.body.asMap()
        body["checked"] = checked
        val itemCopy = DocumentRevision(item.id, item.revision, DocumentBodyFactory.create(body))
        return this.shoppingListRepository.putItem(itemCopy)
    }

    fun deleteItem(item: DocumentRevision) : DocumentRevision {
        return this.shoppingListRepository.deleteItem(item)
    }
}