package com.hoyar.nooshafocus

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private val Purple = Color(0xFF59458D)
private val Violet = Color(0xFF9578E8)
private val Blush = Color(0xFFFFD5E6)
private val Mint = Color(0xFFBDEFE0)
private val Pale = Color(0xFFF8F3FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FocusTimerApp() }
    }
}

@Composable
private fun FocusTimerApp() {
    var focusMin by remember { mutableIntStateOf(15) }
    var breakMin by remember { mutableIntStateOf(5) }
    var remaining by remember { mutableIntStateOf(15 * 60) }
    var running by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }
    var stars by remember { mutableIntStateOf(0) }
    var musicOn by remember { mutableStateOf(false) }
    val music = remember { SoftMelody() }
    DisposableEffect(Unit) { onDispose { music.stop() } }

    LaunchedEffect(running, remaining) {
        if (running && remaining > 0) {
            delay(1000)
            remaining--
        } else if (running && remaining == 0) {
            running = false
            if (!isBreak) {
                stars++
                isBreak = true
                remaining = breakMin * 60
            } else {
                isBreak = false
                remaining = focusMin * 60
            }
        }
    }

    val total = (if (isBreak) breakMin else focusMin) * 60
    val progress by animateFloatAsState(if (total == 0) 1f else remaining.toFloat() / total, tween(450), label = "timer")
    val clock = "%02d:%02d".format(remaining / 60, remaining % 60)

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFF0E9FF), Color(0xFFFFEFF6), Color(0xFFFFFBF3))))) {
            Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("سلام قهرمان کوچولو! 🌷", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
                        Text("قدم‌های کوچیک، ستاره‌های بزرگ", fontSize = 14.sp, color = Purple.copy(alpha = .75f))
                    }
                    Box(Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = .85f)), contentAlignment = Alignment.Center) { Text("🦄", fontSize = 32.sp) }
                }
                Spacer(Modifier.height(18.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .94f)), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(color = if (isBreak) Mint else Blush, shape = RoundedCornerShape(50)) {
                            Text(if (isBreak) "🌈 وقت استراحه" else "✨ وقت تمرکز", Modifier.padding(horizontal = 18.dp, vertical = 8.dp), color = Purple, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.size(210.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize()) {
                                drawArc(Color(0xFFECE6F7), -90f, 360f, false, style = Stroke(18.dp.toPx()))
                                drawArc(if (isBreak) Color(0xFF61C6A8) else Violet, -90f, 360f * progress, false, style = Stroke(18.dp.toPx(), cap = StrokeCap.Round))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(clock, fontSize = 43.sp, fontWeight = FontWeight.Bold, color = Purple)
                                Text(if (isBreak) "آب بخور و کش‌وقوس بیا" else "فقط همین یک کار!", fontSize = 14.sp, color = Purple.copy(alpha = .75f))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = { running = !running }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet)) {
                                Icon(if (running) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(5.dp)); Text(if (running) "مکث" else "شروع", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(onClick = { running = false; isBreak = false; remaining = focusMin * 60 }, modifier = Modifier.size(60.dp, 52.dp), shape = RoundedCornerShape(18.dp)) { Icon(Icons.Default.Refresh, contentDescription = "شروع دوباره", tint = Purple) }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .85f))) {
                    Column(Modifier.padding(14.dp)) {
                        Text("⏳ زمان دلخواه من", fontWeight = FontWeight.Bold, color = Purple, fontSize = 17.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("تمرکز", Modifier.weight(1f), color = Purple)
                            listOf(10, 15, 20).forEach { m -> FilterChip(selected = focusMin == m, onClick = { focusMin = m; if (!isBreak) { running = false; remaining = m * 60 } }, label = { Text("$m دقیقه") }) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("استراحت", Modifier.weight(1f), color = Purple)
                            listOf(3, 5, 7).forEach { m -> FilterChip(selected = breakMin == m, onClick = { breakMin = m; if (isBreak) { running = false; remaining = m * 60 } }, label = { Text("$m دقیقه") }) }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .86f))) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 26.sp); Text("$stars ستاره", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Purple); Text("جلسه‌های کامل", fontSize = 12.sp, color = Purple.copy(alpha = .7f))
                        }
                    }
                    Card(Modifier.weight(1f), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .86f))) {
                        Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎵", fontSize = 26.sp)
                            TextButton(onClick = { musicOn = !musicOn; if (musicOn) music.start() else music.stop() }) { Text(if (musicOn) "خاموش کردن" else "آهنگ آرامش", color = Purple, fontWeight = FontWeight.Bold) }
                            Text("ملودی آرام و بی‌کلام", fontSize = 11.sp, color = Purple.copy(alpha = .7f))
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("لازم نیست همه‌چیز کامل باشه؛ فقط شروع کن 💜", fontSize = 13.sp, color = Purple.copy(alpha = .8f), textAlign = TextAlign.Center)
            }
        }
    }
}

/** Locally generated, quiet ambient melody; no internet or audio files needed. */
private class SoftMelody {
    @Volatile private var playing = false
    @Volatile private var track: AudioTrack? = null
    private var worker: Thread? = null
    fun start() {
        if (playing) return
        playing = true
        worker = Thread {
            val rate = 22050
            val samples = ShortArray(2048)
            val min = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT).coerceAtLeast(samples.size * 2)
            val audio = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAudioFormat(AudioFormat.Builder().setSampleRate(rate).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(min).setTransferMode(AudioTrack.MODE_STREAM).build()
            track = audio
            audio.setVolume(0.12f); audio.play()
            var index = 0L
            val notes = doubleArrayOf(261.63, 329.63, 392.0, 329.63, 293.66, 349.23, 440.0, 349.23)
            while (playing) {
                for (i in samples.indices) {
                    val t = index.toDouble() / rate
                    val f = notes[((index / (rate * 2L)) % notes.size).toInt()]
                    val wave = sin(2.0 * PI * f * t) * 0.38 + sin(2.0 * PI * f * 0.5 * t) * 0.10
                    samples[i] = (wave * Short.MAX_VALUE * 0.18).toInt().toShort()
                    index++
                }
                if (playing) audio.write(samples, 0, samples.size)
            }
            try { audio.pause(); audio.flush(); audio.release() } catch (_: Exception) { }
            track = null
        }.apply { isDaemon = true; start() }
    }
    fun stop() { playing = false; try { track?.pause() } catch (_: Exception) { } }
}
