package com.ibm.shoppinglist

import com.ibm.shoppinglist.model.ShoppingListRepository

class StateManager {

    companion object{

        lateinit var shoppingListRepository: ShoppingListRepository
    }
}