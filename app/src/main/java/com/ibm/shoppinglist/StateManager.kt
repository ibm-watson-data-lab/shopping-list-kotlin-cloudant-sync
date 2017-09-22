package com.ibm.shoppinglist

import com.cloudant.sync.documentstore.DocumentRevision

class StateManager {

    companion object{

        lateinit var datastore: Datastore
        var activeShoppingList: DocumentRevision? = null
    }
}