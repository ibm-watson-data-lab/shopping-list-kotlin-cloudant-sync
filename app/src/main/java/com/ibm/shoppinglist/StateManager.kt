package com.ibm.shoppinglist

import com.cloudant.sync.documentstore.DocumentRevision
import com.ibm.shoppinglist.model.ShoppingListRepository

class StateManager {

    companion object{

        lateinit var shoppingListRepository: ShoppingListRepository
        var activeShoppingList: DocumentRevision? = null
    }
}