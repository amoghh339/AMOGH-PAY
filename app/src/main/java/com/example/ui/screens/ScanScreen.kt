package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.components.outlinedColors
import com.example.ui.theme.FamAmber
import com.example.ui.theme.FamCyan
import com.example.ui.theme.FamDarkBorder
import com.example.ui.theme.FamDarkSurface
import com.example.ui.theme.FamDarkSurfaceElevated
import com.example.ui.theme.FamGreen
import com.example.ui.theme.FamTextMuted
import com.example.ui.theme.FamTextPrimary
import com.example.ui.theme.FamTextSecondary
import com.example.ui.theme.FamYellow

data class DemoMerchantQR(
    val name: String,
    val upiId: String,
    val suggestedAmount: Double,
    val category: String,
    val emoji: String
)

@Composable
fun ScanScreen(
    onScanSuccess: (name: String, upiId: String, amount: Double?, category: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var manualInput by remember { mutableStateOf("") }

    val sampleMerchants = listOf(
        DemoMerchantQR("Starbucks Coffee", "starbucks@icici", 180.0, "FOOD", "☕"),
        DemoMerchantQR("McDonald's Burgers", "mcdonalds@hdfc", 249.0, "FOOD", "🍔"),
        DemoMerchantQR("Steam Games Wallet", "steamgames@kotak", 450.0, "ENTERTAINMENT", "🎮"),
        DemoMerchantQR("Swiggy Food Mart", "swiggy@axis", 199.0, "FOOD", "🍕"),
        DemoMerchantQR("Local Kirana Store", "omkar.kirana@upi", 85.0, "SHOPPING", "🛒"),
        DemoMerchantQR("Rohan Verma", "rohan@fam", 150.0, "TRANSFER", "👦")
    )

    // Animated scanner laser line
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_position"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Scan & Pay 📷",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = FamTextPrimary
                )
                Text(
                    text = "Scan any UPI QR code (BharatPe, Paytm, PhonePe, GPay) without a bank account",
                    fontSize = 12.sp,
                    color = FamCyan
                )
            }
        }

        // Camera Viewfinder Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .testTag("qr_scanner_viewfinder"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = androidx.compose.foundation.BorderStroke(2.dp, FamYellow.copy(alpha = 0.8f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }
                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (e: Exception) {
                                        // Camera not available in emulator / test environment, fallback gracefully
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Camera Permission request fallback view
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F111A))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = FamYellow,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Camera Permission Required",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FamTextPrimary
                            )
                            Text(
                                text = "To scan physical UPI QR codes, allow camera access or use the quick test QRs below.",
                                fontSize = 12.sp,
                                color = FamTextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Enable Camera", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Scanner Viewfinder Overlay & Framing Grid
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Laser scanning beam
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .graphicsLayer {
                                    translationY = laserPosition * 500f
                                }
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, FamYellow, FamCyan, Color.Transparent)
                                    )
                                )
                        )

                        // Central targeting indicator
                        Text(
                            text = "Align any QR code inside frame",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Quick Scan / Test UPI QR Codes
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Test UPI Merchants",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FamTextPrimary
                )
                Text(
                    text = "Tap any popular merchant to simulate scanning their UPI QR code:",
                    fontSize = 11.sp,
                    color = FamTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleMerchants) { merchant ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable {
                                    onScanSuccess(
                                        merchant.name,
                                        merchant.upiId,
                                        merchant.suggestedAmount,
                                        merchant.category
                                    )
                                }
                                .testTag("sample_qr_${merchant.name.lowercase().take(5)}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = merchant.emoji, fontSize = 24.sp)
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = FamYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = merchant.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FamTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = merchant.upiId,
                                    fontSize = 10.sp,
                                    color = FamCyan,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "₹${merchant.suggestedAmount.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FamYellow
                                )
                            }
                        }
                    }
                }
            }
        }

        // Manual UPI ID Search & Pay
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Or Enter Any UPI ID / Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = manualInput,
                        onValueChange = { manualInput = it },
                        placeholder = { Text("e.g. merchant@paytm or 9876543210") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_upi_input"),
                        singleLine = true,
                        trailingIcon = {
                            if (manualInput.isNotBlank()) {
                                Button(
                                    onClick = {
                                        onScanSuccess(
                                            manualInput.split("@").first().replaceFirstChar { it.uppercase() },
                                            manualInput,
                                            null,
                                            "TRANSFER"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Pay", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        colors = outlinedColors()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
