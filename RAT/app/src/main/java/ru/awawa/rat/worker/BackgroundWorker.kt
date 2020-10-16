package ru.awawa.rat.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*


class BackgroundWorker(context: Context, workerParams: WorkerParameters):
        Worker(context, workerParams) {

    private var workerRunning = false
    private var workerLaunched = false

    override fun doWork(): Result {
        this.workerRunning = true
        while (!isStopped) {
            Log.d("BackgroundWorker", "Running...")
            Thread.sleep(1000)
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        this.workerRunning = false
    }
}