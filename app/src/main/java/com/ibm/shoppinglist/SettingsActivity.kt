package com.ibm.shoppinglist

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val editText = findViewById(R.id.settings_sync_url_edit_text) as EditText
        editText.setText(SyncManager.activeSyncUrl)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { _ -> save(editText.text.toString()) }
    }

    private fun save(syncUrl: String) {
        SyncManager.updateSyncUrl(syncUrl)
        this.finish()
    }

}
