package com.hoyar.nooshafocus
import androidx.compose.ui.draw.rotate
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Purple = Color(0xFF59458D)
private val Violet = Color(0xFF9274E7)
private val Pink = Color(0xFFFFD5E7)
private val Mint = Color(0xFFBDEFE0)
private val Cream = Color(0xFFFFFBF5)
private val Lilac = Color(0xFFF0E9FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FocusTimerApp() }
    }
}

@Composable
private fun FocusTimerApp() {
    val context = LocalContext.current
    var focusMin by remember { mutableIntStateOf(15) }
    var breakMin by remember { mutableIntStateOf(5) }
    var remaining by remember { mutableIntStateOf(15 * 60) }
    var running by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }
    var stars by remember { mutableIntStateOf(0) }
    var musicOn by remember { mutableStateOf(false) }
    var loadingMusic by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Uri?>(null) }
    var selectedSongName by remember { mutableStateOf("آهنگ آرامش نوشا") }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    val songPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
            try { player?.release() } catch (_: Exception) { }
            player = null
            musicOn = false
            loadingMusic = false
            selectedSong = uri
            selectedSongName = uri.lastPathSegment?.substringAfterLast('/')?.takeLast(32)?.takeIf { it.isNotBlank() } ?: "آهنگ انتخاب‌شده"
        }
    }

    DisposableEffect(Unit) {
        onDispose { try { player?.release() } catch (_: Exception) { } }
    }

    LaunchedEffect(running, remaining) {
        if (running && remaining > 0) {
            delay(1000)
            remaining--
        } else if (running && remaining == 0) {
            running = false
            if (!isBreak) { stars++; isBreak = true; remaining = breakMin * 60 }
            else { isBreak = false; remaining = focusMin * 60 }
        }
    }

    val total = (if (isBreak) breakMin else focusMin) * 60
    val progress by animateFloatAsState(if (total <= 0) 1f else remaining.toFloat() / total, tween(450), label = "timer")
    val clock = "%02d:%02d".format(remaining / 60, remaining % 60)

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Lilac, Color(0xFFFFEFF6), Cream)))) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("سلام نوشا قهرمان! 🌷", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
                        Spacer(Modifier.height(5.dp))
                        Text("قدم‌های کوچیک، ستاره‌های بزرگ", fontSize = 15.sp, color = Purple.copy(alpha = .78f))
                    }
                    Box(Modifier.size(66.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text("🦄", fontSize = 37.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))

                Card(
                    Modifier.fillMaxWidth(), shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .97f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        LabelPill(if (isBreak) "🌈 وقت استراحت" else "✨ وقت تمرکز", if (isBreak) Mint else Pink)
                        Spacer(Modifier.height(14.dp))
                        Box(Modifier.size(210.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize()) {
                                drawArc(Color(0xFFECE6F7), -90f, 360f, false, style = Stroke(18.dp.toPx()))
                                drawArc(if (isBreak) Color(0xFF55BFA0) else Violet, -90f, 360f * progress, false, style = Stroke(18.dp.toPx(), cap = StrokeCap.Round))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(clock, fontSize = 43.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
                                Spacer(Modifier.height(5.dp))
                                Text(if (isBreak) "یه نفس عمیق بکش 🌿" else "فقط همین یک کار!", fontSize = 14.sp, color = Purple.copy(alpha = .75f))
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { running = !running }, modifier = Modifier.weight(1f).height(58.dp),
                                shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet)
                            ) {
                                Icon(if (running) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
                                Spacer(Modifier.width(8.dp)); Text(if (running) "مکث" else "شروع تمرکز", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { running = false; isBreak = false; remaining = focusMin * 60 },
                                modifier = Modifier.size(60.dp, 58.dp), shape = RoundedCornerShape(18.dp)
                            ) { Icon(Icons.Default.Refresh, contentDescription = "شروع دوباره", tint = Purple) }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                HomeworkWritingCard(isFocusing = running && !isBreak)

                Spacer(Modifier.height(16.dp))
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .94f))) {
                    Column(Modifier.padding(15.dp)) {
                        Text("⏳ زمان دلخواه من", fontWeight = FontWeight.ExtraBold, color = Purple, fontSize = 19.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("زمان تمرکز", color = Purple, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(7.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            listOf(10, 15, 20).forEach { m ->
                                ChoiceBox("$m دقیقه", focusMin == m, Modifier.weight(1f)) {
                                    focusMin = m
                                    if (!isBreak) { running = false; remaining = m * 60 }
                                }
                            }
                        }
                        Spacer(Modifier.height(13.dp))
                        Text("زمان استراحت", color = Purple, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(7.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            listOf(3, 5, 7).forEach { m ->
                                ChoiceBox("$m دقیقه", breakMin == m, Modifier.weight(1f)) {
                                    breakMin = m
                                    if (isBreak) { running = false; remaining = m * 60 }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    Card(Modifier.weight(.82f), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .96f))) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 32.sp)
                            Spacer(Modifier.height(5.dp))
                            Text("$stars ستاره", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
                            Text("قهرمان امروز", fontSize = 12.sp, color = Purple.copy(alpha = .75f))
                        }
                    }
                    Card(Modifier.weight(1.18f), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .96f))) {
                        Column(Modifier.fillMaxWidth().padding(11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎵 آهنگ تمرکز", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
                            Spacer(Modifier.height(8.dp))
                            Surface(color = Lilac, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(selectedSongName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp), fontSize = 11.sp, color = Purple, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                            }
                            Spacer(Modifier.height(7.dp))
                            OutlinedButton(
                                onClick = { songPicker.launch(arrayOf("audio/*")) }, modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(horizontal = 7.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp)); Text("انتخاب آهنگ", fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    val uri = selectedSong ?: Uri.parse("android.resource://${context.packageName}/${R.raw.focus_music}")
                                    try {
                                        val current = player
                                        if (current != null && current.isPlaying) {
                                            current.pause(); musicOn = false; loadingMusic = false
                                        } else if (current != null) {
                                            current.start(); musicOn = true; loadingMusic = false
                                        } else {
                                            loadingMusic = true
                                            val mp = MediaPlayer()
                                            player = mp
                                            mp.setDataSource(context, uri)
                                            mp.isLooping = true
                                            mp.setOnPreparedListener { prepared ->
                                                prepared.setVolume(0.35f, 0.35f)
                                                prepared.start(); musicOn = true; loadingMusic = false
                                            }
                                            mp.setOnErrorListener { _, _, _ -> musicOn = false; loadingMusic = false; player = null; true }
                                            mp.prepareAsync()
                                        }
                                    } catch (_: Exception) { musicOn = false; loadingMusic = false; try { player?.release() } catch (_: Exception) { }; player = null }
                                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Violet),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 9.dp), enabled = !loadingMusic
                            ) {
                                Icon(if (musicOn) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(19.dp))
                                Spacer(Modifier.width(4.dp)); Text(when { loadingMusic -> "در حال آماده‌سازی"; musicOn -> "مکث موسیقی"; else -> "پخش موسیقی" }, fontSize = 12.sp)
                            }
                            TextButton(onClick = {
                                try { player?.pause() } catch (_: Exception) { }
                                try { player?.seekTo(0) } catch (_: Exception) { }
                                musicOn = false; loadingMusic = false
                            }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(2.dp)) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp), tint = Purple)
                                Spacer(Modifier.width(3.dp)); Text("توقف", color = Purple, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Surface(color = Color.White.copy(alpha = .8f), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF55A98E), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("لازم نیست کامل باشی؛ فقط شروع کن 💜", fontSize = 13.sp, color = Purple, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun LabelPill(text: String, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(50)) {
        Text(text, Modifier.padding(horizontal = 20.dp, vertical = 10.dp), color = Purple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun ChoiceBox(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick, modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFE9DFFF) else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Violet else Color(0xFFD8D0E2))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 3.dp)) {
            Text(text, color = Purple, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}


@Composable
private fun HomeworkWritingCard(isFocusing: Boolean) {
    val transition = rememberInfiniteTransition(label = "homework-writing")
    val penProgress by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isFocusing) 1700 else 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pen-progress"
    )
    val sparkle by transition.animateFloat(
        initialValue = 0.65f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "sparkle"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9FE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✏️ وقت مشق نوشتن", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
            Spacer(Modifier.height(4.dp))
            Text(
                if (isFocusing) "آفرین! مداد کوچولو داره می‌نویسه 🌟" else "با قدم‌های کوچولو، مشق‌هامون رو می‌نویسیم",
                fontSize = 13.sp, color = Purple.copy(alpha = .8f), textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier.fillMaxWidth().height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFE8F8FF), Color(0xFFFFEAF3), Color(0xFFF1EAFF)))),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize().padding(12.dp)) {
                    val paperLeft = size.width * 0.16f
                    val paperTop = size.height * 0.08f
                    val paperWidth = size.width * 0.68f
                    val paperHeight = size.height * 0.82f
                    drawRoundRect(Color.White, Offset(paperLeft, paperTop), androidx.compose.ui.geometry.Size(paperWidth, paperHeight), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f))
                    // notebook margin and writing guides
                    drawLine(Color(0xFFFFB6C9), Offset(paperLeft + paperWidth * .17f, paperTop + 10f), Offset(paperLeft + paperWidth * .17f, paperTop + paperHeight - 8f), strokeWidth = 2.dp.toPx())
                    for (row in 1..4) {
                        val y = paperTop + paperHeight * (row * 0.18f)
                        drawLine(Color(0xFFD8DDF5), Offset(paperLeft + paperWidth * .10f, y), Offset(paperLeft + paperWidth * .92f, y), strokeWidth = 1.5.dp.toPx())
                    }
                    // progressively drawn handwriting-like strokes
                    val startX = paperLeft + paperWidth * .25f
                    val available = paperWidth * .62f
                    for (row in 0..2) {
                        val y = paperTop + paperHeight * (0.18f + row * 0.18f)
                        val fraction = if (row < 2) 1f else penProgress
                        val lineEnd = startX + available * fraction
                        var x = startX
                        var index = 0
                        while (x < lineEnd) {
                            val length = minOf(available * 0.075f, lineEnd - x)
                            val rise = if (index % 2 == 0) -5.dp.toPx() else 4.dp.toPx()
                            drawLine(Color(0xFF8D79D9), Offset(x, y + rise), Offset(x + length, y), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                            x += available * 0.105f
                            index++
                        }
                    }
                    // pencil moves as the writing progresses
                    val px = startX + available * penProgress
                    val py = paperTop + paperHeight * .52f
                    rotate(degrees = -28f, pivot = Offset(px, py)) {
                        drawRoundRect(Color(0xFFFFC857), Offset(px - 7.dp.toPx(), py - 29.dp.toPx()), androidx.compose.ui.geometry.Size(14.dp.toPx(), 45.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()))
                        drawLine(Color(0xFFB77B52), Offset(px, py + 16.dp.toPx()), Offset(px, py + 23.dp.toPx()), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFFEF8DAF), Offset(px - 7.dp.toPx(), py - 19.dp.toPx()), Offset(px + 7.dp.toPx(), py - 19.dp.toPx()), strokeWidth = 3.dp.toPx())
                    }
                }
                Text("✨", modifier = Modifier.align(Alignment.TopEnd).padding(12.dp), fontSize = (22 * sparkle).sp)
                Text("🌼", modifier = Modifier.align(Alignment.BottomStart).padding(10.dp), fontSize = 25.sp)
            }
            Spacer(Modifier.height(10.dp))
            Surface(color = if (isFocusing) Mint else Lilac, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (isFocusing) "تو می‌تونی نوشا جان! فقط یک خط دیگه 💜" else "برای شروع، فقط یک خط بنویس!",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    color = Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )
            }
        }
    }
}
