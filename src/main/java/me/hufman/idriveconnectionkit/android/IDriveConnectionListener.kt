package me.hufman.idriveconnectionkit.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class IDriveConnectionListener : BroadcastReceiver() {
	private val TAG = "IDriveConnectionListen"

	/**
	 * The current connection status is available as static members
	 * isConnected indicates whether the car is currently connected
	 * brand is which type of car is connected: bmw bmwi mini
	 * host is what host to use to reach the Etch service, usually 127.0.0.1
	 * port is which port on that host to reach the Etch service
	 * instanceId is an internal identifier
	 * callback is called after the connection state changes
	 */
	companion object {
		const val INTENT_ATTACHED = "com.bmwgroup.connected.accessory.ACTION_CAR_ACCESSORY_ATTACHED"
		const val INTENT_DETACHED = "com.bmwgroup.connected.accessory.ACTION_CAR_ACCESSORY_DETACHED"

		var isConnected: Boolean = false
			private set
		var brand: String? = null
			private set
		var host: String? = null
			private set
		var port: Int? = null
			private set
		var instanceId: Int? = null
			private set
		var callback: Runnable? = null

		fun reset() {
			isConnected = false
			callback?.run()
		}

		fun setConnection(brand: String, host: String, port: Int, instanceId: Int? = null) {
			if (isConnected) return
			isConnected = true
			this.brand = brand
			this.host = host
			this.port = port
			this.instanceId = instanceId
			callback?.run()
		}
	}

	/**
	 * Listen for status updates about whether the car is connected
	 */
	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent == null) return
		Log.i(TAG, "Received car announcement: " + intent.action)
		if (intent.action == INTENT_ATTACHED) {
			isConnected = true
			brand = intent.getStringExtra("EXTRA_BRAND")
			host = intent.getStringExtra("EXTRA_HOST")
			port = intent.getIntExtra("EXTRA_PORT", -1)
			instanceId = intent.getIntExtra("EXTRA_INSTANCE_ID", -1)
			if (callback != null) callback?.run()
		}
		if (intent.action == INTENT_DETACHED) {
			isConnected = false
			if (callback != null) callback?.run()
		}
	}

	fun subscribe(context: Context) {
		context.registerReceiver(this, IntentFilter(INTENT_ATTACHED))
		context.registerReceiver(this, IntentFilter(INTENT_DETACHED))
	}
	fun unsubscribe(context: Context) {
		context.unregisterReceiver(this)
	}
}
