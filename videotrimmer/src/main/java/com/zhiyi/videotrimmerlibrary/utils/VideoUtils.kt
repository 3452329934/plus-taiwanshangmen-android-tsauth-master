/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zhiyi.videotrimmerlibrary.utils

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.zhiyi.videotrimmerlibrary.callback.OnTrimVideoListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*

object VideoUtils {

    private val TAG = VideoUtils::class.java.simpleName

    @Throws(IOException::class)
    fun startTrim(src: File, dst: String, startMs: Long, endMs: Long, callback: OnTrimVideoListener) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "MP4_$timeStamp.mp4"
        val filePath = dst + fileName

        val file = File(filePath)
        file.parentFile.mkdirs()
        Log.d(TAG, "Generated file path $filePath")
        genVideoUsingMp4Parser(src, file, startMs, endMs, callback)
    }

    fun startTrim(src: String?, dst: String, startMs: Long, endMs: Long, callback: OnTrimVideoListener) {
        if (TextUtils.isEmpty(src)) {
            return
        }
        val fileDst = File(dst)
        val fileSrc = File(src)
        fileDst.parentFile.mkdirs()
        Log.d(TAG, "Generated file path $dst")
        genVideoUsingMp4Parser(fileSrc, fileDst, startMs, endMs, callback)
    }

    @Throws(IOException::class)
    private fun genVideoUsingMp4Parser(src: File, dst: File, startMs: Long, endMs: Long, callback: OnTrimVideoListener) {

        val movie = MovieCreator.build(FileDataSourceImpl(src.absolutePath))

        val tracks = movie.tracks
        movie.tracks = LinkedList()

        var startTime1 = (startMs / 1000).toDouble()
        var endTime1 = (endMs / 1000).toDouble()

        var timeCorrected = false

        for (track in tracks) {
            if (track.syncSamples != null && track.syncSamples.size > 0) {
                if (timeCorrected) {
                    /*  This exception here could be a false positive in case we have multiple tracks
                     with sync samples at exactly the same positions. E.g. a single movie containing
                     multiple qualities of the same video (Microsoft Smooth Streaming file)*/

                    throw RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.")
                }
                startTime1 = correctTimeToSyncSample(track, startTime1, false)
                endTime1 = correctTimeToSyncSample(track, endTime1, true)
                timeCorrected = true
            }
        }

        for (track in tracks) {
            var currentSample: Long = 0
            var currentTime = 0.0
            var lastTime = -1.0
            var startSample1: Long = -1
            var endSample1: Long = -1

            for (i in 0 until track.sampleDurations.size) {
                val delta = track.sampleDurations[i]


                if (currentTime > lastTime && currentTime <= startTime1) {
                    /*current sample is still before the new starttime*/
                    startSample1 = currentSample
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    /* current sample is after the new start time and still before the new endtime*/
                    endSample1 = currentSample
                }
                lastTime = currentTime
                currentTime += delta.toDouble() / track.trackMetaData.timescale.toDouble()
                currentSample++
            }
            movie.addTrack(AppendTrack(CroppedTrack(track, startSample1, endSample1)))
        }

        dst.parentFile.mkdirs()

        if (!dst.exists()) {
            dst.createNewFile()
        }

        val out = DefaultMp4Builder().build(movie)

        val fos = FileOutputStream(dst)
        val fc = fos.channel
        out.writeContainer(fc)

        fc.close()
        fos.close()
        Log.d(TAG, "Generated file path $dst")
        callback.getResult(Uri.parse(dst.toString()))
    }

    private fun correctTimeToSyncSample(track: Track, cutHere: Double, next: Boolean): Double {
        val timeOfSyncSamples = DoubleArray(track.syncSamples.size)
        var currentSample: Long = 0
        var currentTime = 0.0
        for (i in 0 until track.sampleDurations.size) {
            val delta = track.sampleDurations[i]

            if (Arrays.binarySearch(track.syncSamples, currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.syncSamples, currentSample + 1)] = currentTime
            }
            currentTime += delta.toDouble() / track.trackMetaData.timescale.toDouble()
            currentSample++

        }
        var previous = 0.0
        for (timeOfSyncSample in timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                return if (next) {
                    timeOfSyncSample
                } else {
                    previous
                }
            }
            previous = timeOfSyncSample
        }
        return timeOfSyncSamples[timeOfSyncSamples.size - 1]
    }

    /**
     * 对Mp4文件集合进行追加合并(按照顺序一个一个拼接起来)
     *
     * @param mp4PathList [输入]Mp4文件路径的集合(支持m4a)(不支持wav)
     * @param outPutPath  [输出]结果文件全部名称包含后缀(比如.mp4)
     * @throws IOException 格式不支持等情况抛出异常
     */
    fun appendMp4List(mp4PathList: List<String>, outPutPath: String) {
        val mp4MovieList = ArrayList<Movie>()// Movie对象集合[输入]
        for (mp4Path in mp4PathList) {// 将每个文件路径都构建成一个Movie对象
            mp4MovieList.add(MovieCreator.build(mp4Path))
        }

        val audioTracks = LinkedList<Track>()// 音频通道集合
        val videoTracks = LinkedList<Track>()// 视频通道集合

        for (mp4Movie in mp4MovieList) {// 对Movie对象集合进行循环
            for (inMovieTrack in mp4Movie.tracks) {
                if ("soun" == inMovieTrack.handler) {// 从Movie对象中取出音频通道
                    audioTracks.add(inMovieTrack)
                }
                if ("vide" == inMovieTrack.handler) {// 从Movie对象中取出视频通道
                    videoTracks.add(inMovieTrack)
                }
            }
        }

        val resultMovie = Movie()// 结果Movie对象[输出]
        if (!audioTracks.isEmpty()) {// 将所有音频通道追加合并
            resultMovie.addTrack(AppendTrack(*audioTracks.toTypedArray()))
        }
        if (!videoTracks.isEmpty()) {// 将所有视频通道追加合并
            resultMovie.addTrack(AppendTrack(*videoTracks.toTypedArray()))
        }

        val outContainer = DefaultMp4Builder().build(resultMovie)// 将结果Movie对象封装进容器
        val fileChannel = RandomAccessFile(String.format(outPutPath), "rw").channel
        outContainer.writeContainer(fileChannel)// 将容器内容写入磁盘
        fileChannel.close()
    }
}
