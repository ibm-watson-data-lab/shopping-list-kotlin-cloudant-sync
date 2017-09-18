package com.ibm.shoppinglist.model

import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentRevision
import java.util.*

class ShoppingListFactory {

    companion object {

        fun newShoppingList(title: String) : DocumentRevision {
            val revision = DocumentRevision("list:%s".format(UUID.randomUUID().toString()))
            val body = HashMap<String, Any>()
            body.put("type", "list")
            body.put("title", title)
            revision.body = DocumentBodyFactory.create(body)
            return revision
        }

        fun newShoppingListItem(title: String, shoppingList: DocumentRevision) : DocumentRevision {
            val revision = DocumentRevision("item:%s".format(UUID.randomUUID().toString()))
            val body = HashMap<String, Any>()
            body.put("type", "item")
            body.put("title", title)
            body.put("checked", false)
            body.put("list", shoppingList.id)
            revision.body = DocumentBodyFactory.create(body)
            return revision
        }
    }
}
