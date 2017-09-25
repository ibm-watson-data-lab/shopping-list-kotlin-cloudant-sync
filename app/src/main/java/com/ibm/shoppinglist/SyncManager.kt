package com.ibm.shoppinglist

import com.cloudant.sync.documentstore.DocumentBodyFactory
import com.cloudant.sync.documentstore.DocumentNotFoundException
import com.cloudant.sync.documentstore.DocumentRevision
import com.cloudant.sync.documentstore.DocumentStore
import kotlin.concurrent.thread

class SyncManager {

    companion object {

        var settingsDB: DocumentStore? = null
        var settingsDoc: DocumentRevision? = null
        var syncListener: SyncListener? = null
        var activeSyncUrl: String = ""
        var running = false

        fun start(settingsDB: DocumentStore) {
            this.settingsDB = settingsDB
            try {
                this.settingsDoc = this.settingsDB!!.database().read("settings")
                if (this.settingsDoc != null) {
                    this.applySyncUrl(this.settingsDoc!!.body.asMap()["syncUrl"] as String, false)
                }
            }
            catch (ex: DocumentNotFoundException) {
                // ignore
            }
        }

        private fun startSync() {
            if (StateManager.datastore.shoppingListRepository.syncUrl.isEmpty()) {
                this.running = false
                return
            }
            this.running = true
            thread {
                while(this.running) {
                    StateManager.datastore.shoppingListRepository.sync()
                    Thread.sleep(2000)
                }
            }
        }

        fun stopSync() {
            this.running = false
        }

        fun onSyncComplete() {
            this.syncListener?.onSyncComplete()
        }

        fun updateSyncUrl(syncUrl: String) {
            this.applySyncUrl(syncUrl, true)
        }

        private fun applySyncUrl(syncUrl: String, updateDB: Boolean = true) {
            if (syncUrl != this.activeSyncUrl) {
                if (this.settingsDoc == null) {
                    this.settingsDoc = DocumentRevision("settings")
                    this.settingsDoc!!.body = DocumentBodyFactory.create(hashMapOf("syncUrl" to syncUrl))
                    this.settingsDoc = this.settingsDB!!.database().create(this.settingsDoc)
                }
                else {
                    this.settingsDoc!!.body = DocumentBodyFactory.create(hashMapOf("syncUrl" to syncUrl))
                    this.settingsDoc = this.settingsDB!!.database().update(this.settingsDoc)
                }
                this.activeSyncUrl = syncUrl
                StateManager.datastore.shoppingListRepository.syncUrl = syncUrl
                if (! this.running) {
                    this.startSync()
                }
            }
        }

    }
}

