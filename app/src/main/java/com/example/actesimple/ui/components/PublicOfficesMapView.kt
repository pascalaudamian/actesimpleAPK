package com.example.actesimple.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actesimple.data.model.Language
import com.example.actesimple.data.model.Procedure
import com.example.actesimple.data.model.PublicOffice
import com.example.actesimple.data.repository.ProcedureRepository

// Semantic colors for different office types
private val ColorSpclep = Color(0xFF2563EB)     // Blue - Evidenta Populatiei
private val ColorDgpci = Color(0xFFD97706)      // Amber - Permise & Inmatriculari
private val ColorPasapoarte = Color(0xFF4F46E5) // Indigo - Pasapoarte
private val ColorIgi = Color(0xFF9333EA)        // Purple - Imigrari & Rezidenta
private val ColorAnaf = Color(0xFF059669)       // Emerald - ANAF Taxe
private val ColorOnrc = Color(0xFF0D9488)       // Teal - Registrul Comertului
private val ColorPolitie = Color(0xFFE11D48)    // Rose - Cazier Judiciar

fun getOfficeTypeColor(type: String): Color {
    return when (type) {
        "SPCLEP" -> ColorSpclep
        "DGPCI_AUTO" -> ColorDgpci
        "DGP_PASAPOARTE" -> ColorPasapoarte
        "IGI" -> ColorIgi
        "ANAF" -> ColorAnaf
        "ONRC" -> ColorOnrc
        "POLITIE_CAZIER" -> ColorPolitie
        else -> Color(0xFF3B82F6)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublicOfficesMapView(
    procedure: Procedure,
    language: Language,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isRo = language == Language.RO
    val repository = remember { ProcedureRepository() }

    var selectedCity by remember { mutableStateOf("toate") }
    var selectedOfficeId by remember { mutableStateOf<String?>(null) }

    val offices = remember(procedure.id, procedure.category, selectedCity) {
        repository.getOfficesForProcedure(procedure.id, procedure.category, selectedCity)
    }

    val availableCities = remember {
        listOf(
            "toate" to if (isRo) "Toate orașele" else "All Cities",
            "București" to "București",
            "Cluj-Napoca" to "Cluj-Napoca",
            "Timișoara" to "Timișoara",
            "Iași" to "Iași",
            "Brașov" to "Brașov"
        )
    }

    val selectedOffice = remember(selectedOfficeId, offices) {
        offices.find { it.id == selectedOfficeId } ?: offices.firstOrNull()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header Info Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRo) "Hartă & Puncte de Lucru Fizice" else "Offices & Physical Counters Map",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isRo)
                            "Ghișee verificate pentru depunerea dosarului, preluare foto sau eliberare acte."
                        else
                            "Verified government desks for submitting dossiers, photo capture or picking up documents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // City Filter Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (isRo) "Filtrează după oraș:" else "Filter by city:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableCities.forEach { (cityKey, cityLabel) ->
                    val isSelected = selectedCity == cityKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCity = cityKey },
                        label = {
                            Text(
                                text = cityLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("city_chip_$cityKey")
                    )
                }
            }
        }

        // Interactive Vector Map of Romania
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isRo) "Harta Națională a Ghișeelor" else "National Office Marker Map",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = "${offices.size} " + (if (isRo) "locații active" else "active desks"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Map Canvas & Markers Box
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            )
                        )
                ) {
                    val mapWidth = maxWidth
                    val mapHeight = maxHeight

                    // Romania stylized territory backdrop
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Stylized polygon contour of Romania
                        val contourPath = Path().apply {
                            moveTo(w * 0.22f, h * 0.22f) // NW (Satu Mare / Baia Mare)
                            lineTo(w * 0.50f, h * 0.16f) // N (Suceava / Botoșani)
                            lineTo(w * 0.82f, h * 0.24f) // NE (Iași / Prut)
                            lineTo(w * 0.88f, h * 0.58f) // E (Galați / Brăila)
                            lineTo(w * 0.94f, h * 0.80f) // SE (Constanța / Mare)
                            lineTo(w * 0.68f, h * 0.88f) // S (Giurgiu / Dunăre)
                            lineTo(w * 0.40f, h * 0.86f) // SW-S (Craiova / Olt)
                            lineTo(w * 0.12f, h * 0.72f) // SW (Drobeta / Cazane)
                            lineTo(w * 0.08f, h * 0.46f) // W (Timișoara / Arad)
                            lineTo(w * 0.15f, h * 0.30f) // NW (Bihor)
                            close()
                        }

                        // Fill region
                        drawPath(
                            path = contourPath,
                            color = Color(0xFF3B82F6).copy(alpha = 0.08f)
                        )

                        // Outline
                        drawPath(
                            path = contourPath,
                            color = Color(0xFF3B82F6).copy(alpha = 0.35f),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Major rivers / axes subtle lines
                        drawLine(
                            color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                            start = Offset(w * 0.12f, h * 0.72f),
                            end = Offset(w * 0.94f, h * 0.80f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Render Markers on Map
                    offices.forEach { office ->
                        val isSelected = office.id == selectedOffice?.id
                        val pinColor = getOfficeTypeColor(office.type)

                        val posX = mapWidth * (office.mapX / 100f)
                        val posY = mapHeight * (office.mapY / 100f)

                        Box(
                            modifier = Modifier
                                .offset(x = posX - 16.dp, y = posY - 28.dp)
                                .clickable {
                                    selectedOfficeId = office.id
                                }
                                .testTag("map_marker_${office.id}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.wrapContentSize()
                            ) {
                                // Pin Marker Head
                                Surface(
                                    color = if (isSelected) pinColor else pinColor.copy(alpha = 0.9f),
                                    shape = CircleShape,
                                    shadowElevation = if (isSelected) 8.dp else 3.dp,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.5.dp else 1.5.dp,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                                    ),
                                    modifier = Modifier.size(if (isSelected) 28.dp else 22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = office.name,
                                            tint = Color.White,
                                            modifier = Modifier.size(if (isSelected) 16.dp else 13.dp)
                                        )
                                    }
                                }

                                // City / Type small tag
                                if (isSelected) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(4.dp),
                                        shadowElevation = 4.dp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = "${office.city} • ${office.type}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = pinColor,
                                            maxLines = 1,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Office Type Color Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val legends = listOf(
                        Triple("SPCLEP", if (isRo) "Evidență Populație" else "Civil Records", ColorSpclep),
                        Triple("DGPCI", if (isRo) "Permise & Înmatriculări" else "Driving/Plates", ColorDgpci),
                        Triple("Pașapoarte", if (isRo) "Pașapoarte" else "Passports", ColorPasapoarte),
                        Triple("IGI", if (isRo) "Imigrări / Rezidență" else "Immigration", ColorIgi),
                        Triple("ANAF", if (isRo) "Finanțe / NIF" else "Taxes / NIF", ColorAnaf),
                        Triple("ONRC", if (isRo) "Registrul Comerțului" else "Trade Registry", ColorOnrc),
                        Triple("Poliție", if (isRo) "Cazier Judiciar" else "Criminal Record", ColorPolitie)
                    )

                    legends.forEach { (_, label, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Selected or Nearest Office Spotlight Card
        if (selectedOffice != null) {
            val officeTypeColor = getOfficeTypeColor(selectedOffice.type)
            val officeName = if (isRo) selectedOffice.name else (selectedOffice.nameEn ?: selectedOffice.name)
            val officeTypeLabel = if (isRo) selectedOffice.typeLabelRo else selectedOffice.typeLabelEn
            val schedule = if (isRo) selectedOffice.scheduleRo else selectedOffice.scheduleEn
            val transit = if (isRo) selectedOffice.publicTransitRo else selectedOffice.publicTransitEn

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, officeTypeColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Office Header with City & Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = officeTypeColor.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = officeTypeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = officeTypeColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = selectedOffice.city,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = officeName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Address
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = officeTypeColor,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Column {
                            Text(
                                text = if (isRo) "Adresă sediu:" else "Address:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedOffice.address,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Schedule / Visiting hours
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Column {
                            Text(
                                text = if (isRo) "Program de lucru cu publicul:" else "Opening Hours:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = schedule,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Public Transit (Metrou / Autobuz / Tramvai)
                    if (!transit.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = if (isRo) "Transport public în apropiere:" else "Public Transit nearby:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = transit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Action Buttons (Google Maps directions & Online Booking)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(selectedOffice.googleMapsQuery)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    // Fallback to web browser Google Maps
                                    val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(selectedOffice.googleMapsQuery)}")
                                    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                                    context.startActivity(webIntent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = officeTypeColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_maps_${selectedOffice.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRo) "Navighează" else "Directions",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        if (!selectedOffice.appointmentUrl.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedOffice.appointmentUrl))
                                        context.startActivity(browserIntent)
                                    } catch (e: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_appointment_${selectedOffice.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRo) "Programare" else "Book Slot",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        if (!selectedOffice.phone.isNullOrBlank()) {
                            IconButton(
                                onClick = {
                                    try {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedOffice.phone}"))
                                        context.startActivity(dialIntent)
                                    } catch (e: Exception) {}
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Sună",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of other offices for this procedure
        if (offices.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isRo) "Toate ghișeele disponibile (${offices.size}):" else "All available counters (${offices.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                offices.forEach { office ->
                    val isCurrent = office.id == selectedOffice?.id
                    val officeTypeColor = getOfficeTypeColor(office.type)

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) officeTypeColor else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOfficeId = office.id }
                            .testTag("office_card_${office.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = officeTypeColor,
                                shape = CircleShape,
                                modifier = Modifier.size(10.dp)
                            ) {}

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isRo) office.name else (office.nameEn ?: office.name),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${office.city} • ${office.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = if (isCurrent) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = if (isCurrent) officeTypeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
