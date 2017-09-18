package com.ibm.shoppinglist.model

import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore

class ShoppingListRepository(private val ds: DocumentStore) {

    fun find(query: HashMap<String, Any>? = null) : List<DocumentRevision> {
        val shoppingLists = ArrayList<DocumentRevision>()
        var q = query
        if (q == null) {
            q = HashMap()
            q.put("type", "list")
        }
        val result = this.ds.query().find(q)
        shoppingLists += result
        return shoppingLists
    }

    fun findItems(query: HashMap<String, Any>? = null) : List<DocumentRevision> {
        val shoppingListItems = ArrayList<DocumentRevision>()
        var q = query
        if (q == null) {
            q = HashMap()
            q.put("type", "item")
        }
        val result = this.ds.query().find(q)
        shoppingListItems += result
        return shoppingListItems
    }

    fun put(shoppingList: DocumentRevision) : DocumentRevision {
        return if (shoppingList.revision == null) {
            this.ds.database().create(shoppingList)
        }
        else {
            this.ds.database().update(shoppingList)
        }
    }

    fun delete(shoppingList: DocumentRevision) : DocumentRevision {
        return this.ds.database().delete(shoppingList)
    }

}
