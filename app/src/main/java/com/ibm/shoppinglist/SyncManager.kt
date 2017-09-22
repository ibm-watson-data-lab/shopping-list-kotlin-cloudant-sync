package com.ibm.shoppinglist

import kotlin.concurrent.thread

class SyncManager {

    companion object {

        var syncListener: SyncListener? = null
        var running = false

        fun startSync() {
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

    }
}

