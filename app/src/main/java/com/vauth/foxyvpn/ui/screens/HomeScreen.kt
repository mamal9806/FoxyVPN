package com.vauth.foxyvpn.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vauth.foxyvpn.R
import com.vauth.foxyvpn.data.ProxyStateStore
import com.vauth.foxyvpn.data.ThemeMode
import com.vauth.foxyvpn.data.formatBytes
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.displayName
import com.vauth.foxyvpn.data.model.emojiFlag
import com.vauth.foxyvpn.ui.HomeViewModel
import com.vauth.foxyvpn.ui.theme.LocalFoxyStatusColors
import com.vauth.foxyvpn.ui.theme.ThemeController
import com.vauth.foxyvpn.vpn.FoxyVpnService
import com.vauth.foxyvpn.vpn.IpLocationInfo
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    proxyStateStore: ProxyStateStore,
    homeModel: HomeViewModel,
    themeController: ThemeController,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state by FoxyVpnService.state.collectAsStateWithLifecycle()
    val proxyOnlyActive by FoxyVpnService.proxyOnlyActive.collectAsStateWithLifecycle()
    val lastError by FoxyVpnService.lastError.collectAsStateWithLifecycle()
    val liveStats by FoxyVpnService.vpnLiveStats.collectAsStateWithLifecycle()
    val connectedAtElapsedMs by FoxyVpnService.connectedAtElapsedMs.collectAsStateWithLifecycle()
    val pingLatency by FoxyVpnService.pingLatencyMs.collectAsStateWithLifecycle()
    val vpnEdgeIp by FoxyVpnService.vpnEdgeIp.collectAsStateWithLifecycle()
    val isPinging by homeModel.isPinging.collectAsStateWithLifecycle()

    val selectedProxy by proxyStateStore.selectedProxyFlow.collectAsStateWithLifecycle()
    val activeProxy by FoxyVpnService.activeCandidate.collectAsStateWithLifecycle()
    val displayProxy =
        if (state == ConnectionState.DISCONNECTED) selectedProxy else activeProxy ?: selectedProxy

    val statusColors = LocalFoxyStatusColors.current
    val targetRingColor =
        when (state) {
            ConnectionState.CONNECTED -> statusColors.connected
            ConnectionState.CONNECTING -> statusColors.connecting
            ConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.outline
        }
    val ringColor by
        animateColorAsState(targetValue = targetRingColor, label = "connect-ring-color")
    val scale by
        animateFloatAsState(
            targetValue = if (state == ConnectionState.CONNECTED) 1f else 0.94f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "connect-scale",
        )

    val systemInDarkTheme = isSystemInDarkTheme()
    val haptics = LocalHapticFeedback.current

    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    val account by homeModel.account.collectAsStateWithLifecycle()
    val realIp = account.realIp?.let { IpLocationInfo(it, account.country.orEmpty()) }
    val quotaRemaining = account.quotaRemaining
    val quotaMax = account.quotaMax
    val isUnlimited = account.unlimited
    val isConnected = state == ConnectionState.CONNECTED

    LaunchedEffect(Unit) { homeModel.refreshAccount() }

    LaunchedEffect(state) {
        if (state == ConnectionState.CONNECTED) homeModel.refreshAccount()
    }

    LaunchedEffect(state, connectedAtElapsedMs) {
        val startEpoch = connectedAtElapsedMs
        if (state == ConnectionState.CONNECTED && startEpoch != null) {
            while (isActive) {
                val now = android.os.SystemClock.elapsedRealtime()
                elapsedSeconds = ((now - startEpoch) / 1000L).coerceAtLeast(0L)
                delay(1000L)
            }
        } else {
            elapsedSeconds = 0L
        }
    }

    val ipDisplay =
        if (isConnected) {
            vpnEdgeIp?.let { if (it.countryCode.isNotEmpty()) "${it.ip} • ${it.countryCode}" else it.ip }
                ?: "\u2014"
        } else {
            realIp?.let { if (it.countryCode.isNotEmpty()) "${it.ip} • ${it.countryCode}" else it.ip }
                ?: "\u2014"
        }

    val quotaText =
        when {
            quotaRemaining != null && quotaMax != null ->
                "${formatBytes(quotaRemaining)} / ${formatBytes(quotaMax)}"
            quotaRemaining != null -> formatBytes(quotaRemaining)
            isUnlimited -> "Unlimited"
            quotaMax != null -> "Limited to ${formatBytes(quotaMax)}"
            else -> "\u2014"
        }
    val quotaFraction =
        when {
            quotaMax != null && quotaMax > 0L && quotaRemaining != null ->
                (quotaRemaining.toFloat() / quotaMax.toFloat()).coerceIn(0f, 1f)
            isUnlimited -> 1f
            quotaRemaining != null && quotaRemaining > 0L -> 1f
            else -> 0f
        }

    val cardColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val cardBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val mutedText = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Row(
                modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                val mode = themeController.mode
                val showingDark = themeController.resolveDark(systemInDarkTheme)
                Box(
                    modifier =
                        Modifier.size(44.dp)
                            .clip(CircleShape)
                            .combinedClickable(
                                role = Role.Button,
                                onClick = { themeController.toggle(systemInDarkTheme) },
                                onLongClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    themeController.set(ThemeMode.SYSTEM)
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector =
                            when (mode) {
                                ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto
                                ThemeMode.LIGHT -> Icons.Filled.LightMode
                                ThemeMode.DARK -> Icons.Filled.DarkMode
                            },
                        contentDescription =
                            when (mode) {
                                ThemeMode.SYSTEM ->
                                    if (showingDark) stringResource(R.string.theme_desc_system_light)
                                    else stringResource(R.string.theme_desc_system_dark)
                                ThemeMode.LIGHT -> stringResource(R.string.theme_desc_light)
                                ThemeMode.DARK -> stringResource(R.string.theme_desc_dark)
                            },
                        tint = mutedText,
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = mutedText,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Power button with glow ring
            Box(
                modifier =
                    Modifier.size(260.dp)
                        .clip(CircleShape)
                        .background(ringColor.copy(alpha = 0.10f))
                        .clickable(role = Role.Button) {
                            if (state == ConnectionState.DISCONNECTED) onRequestConnect()
                            else onDisconnect()
                        },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier.size((200 * scale).dp)
                            .clip(CircleShape)
                            .border(3.dp, ringColor, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Power,
                        contentDescription =
                            if (state == ConnectionState.DISCONNECTED) "Connect" else "Disconnect",
                        tint = ringColor,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text =
                    when (state) {
                        ConnectionState.CONNECTED -> "Connected"
                        ConnectionState.CONNECTING -> "Connecting\u2026"
                        ConnectionState.DISCONNECTED -> "Disconnected"
                    },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = ringColor,
            )
            Spacer(Modifier.height(4.dp))
            if (state == ConnectionState.CONNECTING) {
                Text(
                    if (lastError != null) "Retrying connection\u2026 Tap to cancel" else "Tap to cancel",
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedText,
                )
            } else {
                Text(
                    text = if (isConnected) formatDuration(elapsedSeconds) else "00:00:00",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                    color = mutedText,
                )
            }

            val error = lastError
            if (error != null && state != ConnectionState.CONNECTING) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Location card
            Card(
                onClick = onOpenServers,
                cardColor = cardColor,
                cardBorder = cardBorder,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val flag = displayProxy?.emojiFlag
                    if (!flag.isNullOrBlank()) {
                        Text(flag, style = MaterialTheme.typography.titleLarge)
                    } else {
                        Icon(imageVector = Icons.Filled.Public, contentDescription = null, tint = mutedText)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Location", style = MaterialTheme.typography.labelMedium, color = mutedText)
                        val displayName =
                            displayProxy?.displayName?.ifBlank { displayProxy.countryCode }
                                ?: stringResource(R.string.home_recommended)
                        Text(
                            displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                        if (
                            state != ConnectionState.DISCONNECTED &&
                                activeProxy != null &&
                                selectedProxy != null &&
                                activeProxy != selectedProxy
                        ) {
                            val nextName =
                                selectedProxy?.displayName?.ifBlank { selectedProxy?.countryCode.orEmpty() }.orEmpty()
                            Text(
                                stringResource(R.string.home_next_connection, nextName),
                                style = MaterialTheme.typography.bodySmall,
                                color = mutedText,
                            )
                        }
                    }
                    Text("\u203A", style = MaterialTheme.typography.titleLarge, color = mutedText)
                }
            }

            Spacer(Modifier.height(12.dp))

            // IP + status card
            Card(cardColor = cardColor, cardBorder = cardBorder) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Current IP", style = MaterialTheme.typography.labelSmall, color = mutedText)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            ipDisplay,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    val badgeColor =
                        if (isConnected) statusColors.connected else MaterialTheme.colorScheme.error
                    Box(
                        modifier =
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isConnected) {
                                if (proxyOnlyActive) "Proxy active" else "Protected"
                            } else "Exposed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats card: data, latency, quota
            Card(cardColor = cardColor, cardBorder = cardBorder) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Data Used", style = MaterialTheme.typography.labelSmall, color = mutedText)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (isConnected) formatBytes(liveStats.sessionBytes) else "0.0 MB",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Column(
                        modifier =
                            Modifier.weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = isConnected && !isPinging, role = Role.Button) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    homeModel.triggerPing()
                                }
                    ) {
                        Text(stringResource(R.string.home_latency), style = MaterialTheme.typography.labelSmall, color = mutedText)
                        Spacer(Modifier.height(2.dp))
                        val currentPing = if (isConnected) pingLatency else null
                        Text(
                            text =
                                when {
                                    isPinging -> stringResource(R.string.home_pinging)
                                    currentPing != null -> "$currentPing ms"
                                    else -> "\u2014 ms"
                                },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color =
                                if (currentPing != null) statusColors.connected
                                else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Monthly Quota",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedText,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        quotaText,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { quotaFraction },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (isConnected) statusColors.connected else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Card(
    cardColor: androidx.compose.ui.graphics.Color,
    cardBorder: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base =
        Modifier.widthIn(max = 520.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardColor)
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
    val withClick = if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base
    Column(modifier = withClick.padding(16.dp), verticalArrangement = Arrangement.Top) {
        content()
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
}
