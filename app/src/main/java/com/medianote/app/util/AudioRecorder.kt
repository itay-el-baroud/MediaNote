package com.medianote.app.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: String = ""

    fun startRecording(): String {
        try {
            val fileName = "voice_${System.currentTimeMillis()}.m4a"
            val file = File(context.filesDir, fileName)
            outputFile = file.absolutePath

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
                prepare()
                start()
            }
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun pauseRecording() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumeRecording() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder?.resume()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRecording(): String {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        return outputFile
    }

    fun cancelRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            if (outputFile.isNotEmpty()) {
                File(outputFile).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        outputFile = ""
    }
}
