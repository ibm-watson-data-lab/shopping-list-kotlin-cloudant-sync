package com.ibm.shoppinglist.model

import com.cloudant.sync.documentstore.DocumentRevision

class ShoppingListDetails(val list: DocumentRevision, val itemCount: Int, val itemCheckedCount: Int)
