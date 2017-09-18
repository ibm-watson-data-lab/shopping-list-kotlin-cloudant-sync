package com.ibm.shoppinglist

import com.cloudant.sync.documentstore.DocumentStore

class StateManager {

    companion object{

        lateinit var ds: DocumentStore
    }
}