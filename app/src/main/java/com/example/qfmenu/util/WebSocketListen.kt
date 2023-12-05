package com.example.qfmenu.util

import android.util.Log
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListen(private val handleMsg: (msg: String) -> Unit) : WebSocketListener() {

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        super.onOpen(webSocket, response)
        webSocket.send("{\n" +
                "     \"event\": \"pusher:subscribe\",\n" +
                "    \"data\": {\n" +
                "        \"channel\": \"channel-order\"\n" +
                "    }\n" +
                "}")
    }

    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        super.onMessage(webSocket, text)
        outPut("Receiving : $text")
        handleMsg(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        outPut("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        super.onFailure(webSocket, t, response)
        outPut("Error : " + t.message)
    }

    private fun outPut(s: String) {
        Log.d("webSocket", s)
    }

    companion object{
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}