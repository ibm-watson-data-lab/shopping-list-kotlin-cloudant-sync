package com.ibm.shoppinglist.model

import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import com.cloudant.sync.replication.ReplicatorBuilder
import java.net.URI
import com.cloudant.sync.replication.Replicator
import com.cloudant.sync.event.Subscribe
import com.cloudant.sync.event.notifications.ReplicationErrored
import com.cloudant.sync.event.notifications.ReplicationCompleted
import com.ibm.shoppinglist.SyncManager


class ShoppingListRepository(private val ds: DocumentStore) {

    class Listener(private var repository: ShoppingListRepository, private var replicator: Replicator, private var pull: Boolean = false) {

        @Subscribe
        fun complete(event: ReplicationCompleted) {
            this.replicator.eventBus.unregister(this)
            if (this.pull) {
                if (event.documentsReplicated > 0) {
                    SyncManager.onSyncComplete()
                }
            }
            else {
                this.repository.pull()
            }
        }

        @Subscribe
        fun error(event: ReplicationErrored) {
            this.replicator.eventBus.unregister(this)
        }
    }

    fun sync() {
        val uri = URI("http://admin:pass@192.168.1.70:35984/shopping-list-kotlin")
        val replicator = ReplicatorBuilder.push().from(this.ds).to(uri).build()
        replicator.eventBus.register(Listener(this, replicator, false))
        replicator.start()
    }

    private fun pull() {
        val uri = URI("http://admin:pass@192.168.1.70:35984/shopping-list-kotlin")
        val replicator = ReplicatorBuilder.pull().from(uri).to(this.ds).build()
        replicator.eventBus.register(Listener(this, replicator, true))
        replicator.start()
    }

    fun find(query: HashMap<String, Any>? = null) : List<DocumentRevision> {
        val shoppingLists = ArrayList<DocumentRevision>()
        var q = query
        if (q == null) {
            q = hashMapOf("type" to "list")
        }
        val result = this.ds.query().find(q)
        shoppingLists += result
        return shoppingLists
    }

    fun findItems(shoppingList: DocumentRevision) : List<DocumentRevision> {
        val shoppingListItems = ArrayList<DocumentRevision>()
        val q = hashMapOf<String,Any>("type" to "item","list" to shoppingList.id)
        val result = this.ds.query().find(q)
        shoppingListItems += result
        return shoppingListItems
    }

    fun findItems(query: HashMap<String, Any>? = null) : List<DocumentRevision> {
        val shoppingListItems = ArrayList<DocumentRevision>()
        var q = query
        if (q == null) {
            q = hashMapOf("type" to "item")
        }
        val result = this.ds.query().find(q)
        shoppingListItems += result
        return shoppingListItems
    }

    fun put(shoppingList: DocumentRevision) : DocumentRevision {
        val rev = if (shoppingList.revision == null) {
            this.ds.database().create(shoppingList)
        }
        else {
            this.ds.database().update(shoppingList)
        }
        this.sync()
        return rev
    }

    fun putItem(shoppingListItem: DocumentRevision) : DocumentRevision {
        val rev = if (shoppingListItem.revision == null) {
            this.ds.database().create(shoppingListItem)
        }
        else {
            this.ds.database().update(shoppingListItem)
        }
        this.sync()
        return rev
    }

    fun delete(shoppingList: DocumentRevision) : DocumentRevision {
        val rev = this.ds.database().delete(shoppingList)
        this.sync()
        return rev
    }

    fun deleteItem(shoppingListItem: DocumentRevision) : DocumentRevision {
        val rev = this.ds.database().delete(shoppingListItem)
        this.sync()
        return rev
    }

}
