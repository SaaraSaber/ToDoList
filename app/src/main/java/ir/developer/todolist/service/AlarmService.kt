package ir.developer.todolist.service
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import ir.developer.todolist.R

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


//        val message = intent?.getStringExtra("EXTRA_MESSAGE")
//        val channelId = "alarm_id"
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        this.let { ctx ->
//            val notificationManager =
//                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val builder = NotificationCompat.Builder(ctx, channelId)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("To Do List")
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//            notificationManager.notify(1, builder.build())
//        }


        // پخش صدای آلارم
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_alarm)
        mediaPlayer.setOnCompletionListener {
            stopSelf()
        }
        mediaPlayer.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}