package com.example.passwordvault.util

import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.Executors

private const val port = 50005
private val TAG = VoiceStreamer::class.java.simpleName

class VoiceStreamer {
//    private val sampleRate = 16000 // 44100 for music
//
//    //    private val channelConfig: Int = android.media.AudioFormat.CHANNEL_CONFIGURATION_MONO
//    private val channelConfig: Int = android.media.AudioFormat.CHANNEL_IN_MONO
//    private val audioFormat: Int = android.media.AudioFormat.ENCODING_PCM_16BIT
//    var minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
//    private var status = true
//    private lateinit var recorder: AudioRecord
//    private lateinit var client: MqttAndroidClient
//
//    fun connect(context: Context) {
//        val clientId = MqttClient.generateClientId()
//        client = MqttAndroidClient(
//            context.applicationContext,
//            "mtqq:tcp//192.168.43.37:11883",
//            clientId
//        )
//        Log.e(TAG, clientId)
//        try {
//            val token = client.connect()
//            token.actionCallback = object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    Log.i("Connection", "success ")
//                    //connectionStatus = true
//                    // Give your callback on connection established here
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    //connectionStatus = false
//                    Log.i("Connection", "failure")
//                    // Give your callback on connection failure here
//                    exception.printStackTrace()
//                }
//            }
//        } catch (e: MqttException) {
//            // Give your callback on connection failure here
//            e.printStackTrace()
//        }
//    }
//
//    fun subscribe(topic: String) {
//        val qos = 2 // Mention your qos value
//        try {
//            client.subscribe(topic, qos, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    // Give your callback on Subscription here
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    // Give your subscription failure callback here
//                }
//            })
//        } catch (e: MqttException) {
//            // Give your subscription failure callback here
//        }
//    }
//
//    fun publish(topic: String, data: String) {
//        val encodedPayload: ByteArray
//        try {
//            encodedPayload = data.toByteArray(charset("UTF-8"))
//            val message = MqttMessage(encodedPayload)
//            message.qos = 2
//            message.isRetained = false
//            client.publish(topic, message)
//        } catch (e: Exception) {
//            // Give Callback on error here
//        } catch (e: MqttException) {
//            // Give Callback on error here
//        }
//    }
//
//    fun disconnect() {
//        try {
//            val disconToken = client.disconnect()
//            disconToken.actionCallback = object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    //connectionStatus = false
//                    // Give Callback on disconnection here
//                }
//
//                override fun onFailure(
//                    asyncActionToken: IMqttToken,
//                    exception: Throwable
//                ) {
//                    // Give Callback on error here
//                }
//            }
//        } catch (e: MqttException) {
//            // Give Callback on error here
//        }
//    }
//
//    fun startStreamingOverNetwork() {
//        val executors = Executors.newFixedThreadPool(6)
//        executors.execute {
//            try {
//                Log.e(TAG, "Started...")
//                streamVoice()
//            } catch (e: UnknownHostException) {
//                Log.e(TAG, "UnknownHostException");
//            } catch (e: IOException) {
//                e.printStackTrace();
//                Log.e(TAG, "IOException");
//            }
//        }
//    }
//
//    fun stopStreamingVoice() {
//        status = false
//        recorder?.run {
//            recorder.stop()
//        }
//    }
//
//    private fun streamVoice() {
//        val datagramSocket = DatagramSocket()
//        val buffer = ByteArray(minBufSize)
//        var packet: DatagramPacket
//        val address = InetAddress.getByName("127.0.0.1")
//        Log.e(TAG, "address -> $address")
//        val recorder = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            sampleRate,
//            channelConfig,
//            audioFormat,
//            minBufSize * 10
//        )
//        recorder.startRecording()
//        Log.e(TAG, "address -> ${recorder.state}")
//        while (status) {
//            minBufSize = recorder.read(buffer, 0, buffer.size)
//            packet = DatagramPacket(buffer, buffer.size, address, port)
//            datagramSocket.send(packet)
//            Log.e(TAG, "Minimum buffer size -> $minBufSize")
//        }
//    }
//}
////        public void startStreaming()
////        {
////
////
////            Thread streamThread = new Thread(new Runnable () {
////
////                @Override
////                public void run() {
////                    try {
////
////                        DatagramSocket socket = new DatagramSocket();
////                        Log.d("VS", "Socket Created");
////
////                        byte[] buffer = new byte[minBufSize];
////
////                        Log.d("VS", "Buffer created of size " + minBufSize);
////                        DatagramPacket packet;
////
////                        final InetAddress destination = InetAddress.getByName("192.168.1.5");
////                        Log.d("VS", "Address retrieved");
////
////                        recorder =
////                            new AudioRecord (MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize*10);
////                        Log.d("VS", "Recorder initialized");
////
////                        recorder.startRecording();
////
////                        while (status == true) {
////
////                            //reading data from MIC into buffer
////                            minBufSize = recorder.read(buffer, 0, buffer.length);
////
////                            //putting buffer in the packet
////                            packet = new DatagramPacket (buffer, buffer.length, destination, port);
////
////                            socket.send(packet);
////                            System.out.println("MinBufferSize: " + minBufSize);
////
////
////                        }
////
////
////                    } catch (UnknownHostException e) {
////                        Log.e("VS", "UnknownHostException");
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                        Log.e("VS", "IOException");
////                    }
////                }
////
////            });
////            streamThread.start();
////        }
////    }
}