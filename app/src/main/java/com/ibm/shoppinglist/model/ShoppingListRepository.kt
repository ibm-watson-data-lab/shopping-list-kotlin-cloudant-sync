package com.ibm.shoppinglist.model

import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import com.cloudant.sync.replication.ReplicatorBuilder
import java.net.URI
import com.cloudant.sync.replication.Replicator
import com.cloudant.sync.event.Subscribe
import com.cloudant.sync.event.notifications.ReplicationErrored
import com.cloudant.sync.event.notifications.ReplicationCompleted
import com.ibm.shoppinglist.SyncManager
import java.text.SimpleDateFormat
import java.util.*


class ShoppingListRepository(private val ds: DocumentStore) {

    //val remoteDb = "http://admin:pass@192.168.1.70:35984/shopping-list"
    val remoteDb = "http://admin:pass@9.24.7.248:35984/shopping-list"

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
        val uri = URI(remoteDb)
        val replicator = ReplicatorBuilder.push().from(this.ds).to(uri).build()
        replicator.eventBus.register(Listener(this, replicator, false))
        replicator.start()
    }

    private fun pull() {
        val uri = URI(remoteDb)
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
            // TODO: should treat incoming shoppingList as immutable, i.e. clone
            val body = shoppingList.body.asMap()
            body["createdAt"] = this.getDateISOString(Date())
            body["updatedAt"] = this.getDateISOString(Date())
            shoppingList.body = DocumentBodyFactory.create(body)
            this.ds.database().create(shoppingList)
        }
        else {
            // TODO: should treat incoming shoppingList as immutable, i.e. clone
            val body = shoppingList.body.asMap()
            body["updatedAt"] = Date()
            shoppingList.body = DocumentBodyFactory.create(body)
            this.ds.database().update(shoppingList)
        }
        this.sync()
        return rev
    }

    fun putItem(shoppingListItem: DocumentRevision) : DocumentRevision {
        val rev = if (shoppingListItem.revision == null) {
            // TODO: should treat incoming shoppingList as immutable, i.e. clone
            val body = shoppingListItem.body.asMap()
            body["createdAt"] = this.getDateISOString(Date())
            body["updatedAt"] = this.getDateISOString(Date())
            shoppingListItem.body = DocumentBodyFactory.create(body)
            this.ds.database().create(shoppingListItem)
        }
        else {
            // TODO: should treat incoming shoppingList as immutable, i.e. clone
            val body = shoppingListItem.body.asMap()
            body["updatedAt"] = Date()
            shoppingListItem.body = DocumentBodyFactory.create(body)
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

    private fun getDateISOString(date: Date) : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        return dateFormat.format(date)
    }

}
