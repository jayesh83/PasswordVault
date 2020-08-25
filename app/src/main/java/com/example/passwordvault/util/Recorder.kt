package com.example.passwordvault.util

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

const val CALL_RECORDER = "CALL"
const val FIVE_MIN_RECORDER = "5MIN"
const val FIVE_MIN_DIR = ".FiveMinRecordings"
const val CALL_DIR = "CallRecordings"

object Recorder {

    lateinit var audioRecord: MediaRecorder
    private var baseDir: String? = null
    private lateinit var newFileName: String

    @RequiresApi(Build.VERSION_CODES.N)
    fun initialize(context: Context, type: String) {
        audioRecord = MediaRecorder()
        baseDir = context.externalCacheDir?.absolutePath
        newFileName = generateFileName(type)

        val outputDir = if (type == CALL_RECORDER) {
            "$baseDir/$CALL_DIR"
        } else {
            "$baseDir/$FIVE_MIN_DIR"
        }

        val file = File(outputDir)

        if (!file.exists())
            file.mkdirs()

        val outputFile = "$outputDir/$newFileName.amr"

        PreferenceUtil.writeToLatestRecord(outputFile, context)

        audioRecord.apply {

            if (type == CALL_RECORDER) {
                setAudioSource(MediaRecorder.AudioSource.UNPROCESSED)
            } else {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setMaxDuration(5000)
                createNoMediaFile()
            }

            setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
        }
    }

    private fun generateFileName(type: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateTime = LocalDateTime.now()
            dateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(
                    FormatStyle.LONG,
                    FormatStyle.SHORT
                )
            )
        } else {
            if (type == CALL_RECORDER)
                "callAudioTest"
            else
                "5minAudio"
        }
    }

    private fun createNoMediaFile() {
        val file = File("$baseDir/.nomedia")
        if (!file.exists())
            file.createNewFile()
    }

    fun increaseVolume(type: String, context: Context) {
        val audioManager: AudioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.mode == AudioManager.MODE_IN_CALL || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION)
            Log.e("Voice Call", "Voice call active")
        if (type == CALL_RECORDER)
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0
            )
        else
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0
            )
    }

    fun prepareRecorder(appContext: Context) {
        try {
            if (Permissions.audioPermission(appContext) == PackageManager.PERMISSION_GRANTED) {
                audioRecord.prepare()
                Thread.sleep(2000)
                audioRecord.start()
            } else {
                throw java.lang.IllegalStateException("Doesn't have permission to record voice")
            }
        } catch (ise: IllegalStateException) {
            Log.e("Service", "Exception -> $ise")
        } catch (ioe: IOException) {
            Log.e("Service", "Exception -> $ioe")
        } catch (exe: Exception) {
            Log.e("Service", "Exception -> $exe")
        }
    }

    fun stopRecorder() {
        audioRecord.stop()
        audioRecord.release()
    }
}