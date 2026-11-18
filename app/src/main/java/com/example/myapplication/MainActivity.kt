package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TemperatureConverterApp()
        }
    }
}

private data class TemperatureState(
    val kelvin: String = "",
    val celsius: String = "",
    val fahrenheit: String = "",
    val activeField: TemperatureField? = null,
    val errorMessage: String? = null
)

private enum class TemperatureField {
    KELVIN,
    CELSIUS,
    FAHRENHEIT
}

private fun temperatureStateSaver(): Saver<TemperatureState, Bundle> =
    Saver(
        save = { state ->
            Bundle().apply {
                putString("kelvin", state.kelvin)
                putString("celsius", state.celsius)
                putString("fahrenheit", state.fahrenheit)
                putString(
                    "activeField",
                    state.activeField?.name
                )
                putString(
                    "errorMessage",
                    state.errorMessage
                )
            }
        },
        restore = { bundle ->
            TemperatureState(
                kelvin = bundle.getString("kelvin") ?: "",
                celsius = bundle.getString("celsius") ?: "",
                fahrenheit = bundle.getString("fahrenheit") ?: "",
                activeField = bundle.getString("activeField")?.let { name ->
                    runCatching {
                        TemperatureField.valueOf(name)
                    }.getOrNull()
                },
                errorMessage = bundle.getString("errorMessage")
            )
        }
    )

@Composable
private fun TemperatureConverterApp() {
    var state by rememberSaveable(
        stateSaver = temperatureStateSaver()
    ) {
        mutableStateOf(TemperatureState())
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1B6E8C),
            onPrimary = Color.White,
            surface = Color.White,
            onSurface = Color(0xFF172530),
            background = Color(0xFFF4F8FA),
            onBackground = Color(0xFF172530),
            error = Color(0xFFB3261E),
            surfaceVariant = Color(0xFFE7F1F6),
            onSurfaceVariant = Color(0xFF5C6F79)
        )
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            TemperatureConverterScreen(
                state = state,
                onKelvinChange = { value ->
                    state = updateFromKelvin(
                        state = state,
                        value = value
                    )
                },
                onCelsiusChange = { value ->
                    state = updateFromCelsius(
                        state = state,
                        value = value
                    )
                },
                onFahrenheitChange = { value ->
                    state = updateFromFahrenheit(
                        state = state,
                        value = value
                    )
                },
                onClear = {
                    state = TemperatureState()
                },
                onSwap = {
                    state = swapTemperatureUnits(state)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemperatureConverterScreen(
    state: TemperatureState,
    onKelvinChange: (String) -> Unit,
    onCelsiusChange: (String) -> Unit,
    onFahrenheitChange: (String) -> Unit,
    onClear: () -> Unit,
    onSwap: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF4F8FA),
                            Color(0xFFE7F1F6),
                            Color(0xFFF4F8FA)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .imePadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(20.dp)
            ) {
                TemperatureHeader()

                TemperatureResultCard(
                    state = state
                )

                TemperatureInputCard(
                    state = state,
                    onKelvinChange = onKelvinChange,
                    onCelsiusChange = onCelsiusChange,
                    onFahrenheitChange = onFahrenheitChange,
                    onClear = onClear,
                    onSwap = onSwap
                )

                state.errorMessage?.let { message ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error.copy(
                            alpha = 0.10f
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                TemperatureInfoCard()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TemperatureHeader() {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "محوّل الحرارة",
            color = Color(0xFF172530),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "أدخل درجة الحرارة في أي وحدة، وسيتم تحويلها تلقائيًا إلى الوحدتين الأخريين.",
            color = Color(0xFF5C6F79),
            fontSize = 13.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TemperatureResultCard(
    state: TemperatureState
) {
    val activeValue = when (state.activeField) {
        TemperatureField.KELVIN -> state.kelvin
        TemperatureField.CELSIUS -> state.celsius
        TemperatureField.FAHRENHEIT -> state.fahrenheit
        null -> ""
    }

    val activeUnit = when (state.activeField) {
        TemperatureField.KELVIN -> "كلفن"
        TemperatureField.CELSIUS -> "مئوي"
        TemperatureField.FAHRENHEIT -> "فهرنهايت"
        null -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDCEEF7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "القيمة المدخلة",
                color = Color(0xFF5C6F79),
                fontSize = 12.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            if (activeValue.isBlank()) {
                Text(
                    text = "أدخل قيمة لبدء التحويل",
                    color = Color(0xFF172530),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement =
                        Arrangement.End
                ) {
                    Text(
                        text = activeUnit,
                        color = Color(0xFF1B6E8C),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(
                            bottom = 6.dp
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = activeValue,
                        color = Color(0xFF172530),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.Right
                    )
                }
            }

            Text(
                text = "التحويل يتم مباشرة أثناء الكتابة.",
                color = Color(0xFF5C6F79),
                fontSize = 12.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TemperatureInputCard(
    state: TemperatureState,
    onKelvinChange: (String) -> Unit,
    onCelsiusChange: (String) -> Unit,
    onFahrenheitChange: (String) -> Unit,
    onClear: () -> Unit,
    onSwap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            TemperatureFieldInput(
                label = "كلفن",
                unit = "K",
                value = state.kelvin,
                isActive =
                    state.activeField ==
                            TemperatureField.KELVIN,
                onValueChange = onKelvinChange
            )

            TemperatureFieldInput(
                label = "مئوي",
                unit = "°C",
                value = state.celsius,
                isActive =
                    state.activeField ==
                            TemperatureField.CELSIUS,
                onValueChange = onCelsiusChange
            )

            TemperatureFieldInput(
                label = "فهرنهايت",
                unit = "°F",
                value = state.fahrenheit,
                isActive =
                    state.activeField ==
                            TemperatureField.FAHRENHEIT,
                onValueChange = onFahrenheitChange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE7F1F6),
                        contentColor = Color(0xFF172530)
                    )
                ) {
                    Text(
                        text = "مسح الكل",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onSwap,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "تبديل الوحدات",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TemperatureFieldInput(
    label: String,
    unit: String,
    value: String,
    isActive: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = unit,
                color = Color(0xFF5C6F79),
                fontSize = 12.sp
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = label,
                color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color(0xFF5C6F79)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Right,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7FAFC),
                unfocusedContainerColor = Color(0xFFF7FAFC),
                focusedTextColor = Color(0xFF172530),
                unfocusedTextColor = Color(0xFF172530),
                focusedBorderColor =
                    MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFD3E2EA),
                cursorColor =
                    MaterialTheme.colorScheme.primary,
                focusedLabelColor =
                    MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color(0xFF5C6F79)
            ),
            placeholder = {
                Text(
                    text = "أدخل القيمة",
                    color = Color(0xFF8CA0AA),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                android.R.drawable.ic_menu_close_clear_cancel
                            ),
                            contentDescription = "مسح الحقل",
                            tint = Color(0xFF5C6F79),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun TemperatureInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "معادلات التحويل",
                color = Color(0xFF172530),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            TemperatureFormula(
                title = "من كلفن إلى مئوي",
                formula = "°C = K − 273.15"
            )

            TemperatureFormula(
                title = "من مئوي إلى فهرنهايت",
                formula = "°F = (°C × 9 ÷ 5) + 32"
            )

            TemperatureFormula(
                title = "من فهرنهايت إلى مئوي",
                formula = "°C = (°F − 32) × 5 ÷ 9"
            )
        }
    }
}

@Composable
private fun TemperatureFormula(
    title: String,
    formula: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formula,
            color = Color(0xFF1B6E8C),
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            color = Color(0xFF5C6F79),
            fontSize = 13.sp,
            textAlign = TextAlign.Right
        )
    }
}

private fun updateFromKelvin(
    state: TemperatureState,
    value: String
): TemperatureState {
    val cleanValue = cleanTemperatureInput(value)

    if (cleanValue.isBlank()) {
        return TemperatureState(
            kelvin = "",
            celsius = "",
            fahrenheit = "",
            activeField = TemperatureField.KELVIN
        )
    }

    val kelvin = cleanValue.toDoubleOrNull() ?: return state.copy(
        kelvin = cleanValue,
        activeField = TemperatureField.KELVIN,
        errorMessage =
            "أدخل رقمًا صحيحًا في حقل كلفن."
    )

    if (kelvin < 0.0) {
        return state.copy(
            kelvin = cleanValue,
            activeField = TemperatureField.KELVIN,
            errorMessage =
                "لا يمكن أن تكون درجة كلفن أقل من 0."
        )
    }

    val celsius = kelvin - 273.15
    val fahrenheit = celsius * 9.0 / 5.0 + 32.0

    return TemperatureState(
        kelvin = cleanValue,
        celsius = formatTemperature(celsius),
        fahrenheit = formatTemperature(fahrenheit),
        activeField = TemperatureField.KELVIN
    )
}

private fun updateFromCelsius(
    state: TemperatureState,
    value: String
): TemperatureState {
    val cleanValue = cleanTemperatureInput(value)

    if (cleanValue.isBlank()) {
        return TemperatureState(
            kelvin = "",
            celsius = "",
            fahrenheit = "",
            activeField = TemperatureField.CELSIUS
        )
    }

    val celsius = cleanValue.toDoubleOrNull() ?: return state.copy(
        celsius = cleanValue,
        activeField = TemperatureField.CELSIUS,
        errorMessage =
            "أدخل رقمًا صحيحًا في حقل مئوي."
    )

    val kelvin = celsius + 273.15
    val fahrenheit = celsius * 9.0 / 5.0 + 32.0

    return TemperatureState(
        kelvin = formatTemperature(kelvin),
        celsius = cleanValue,
        fahrenheit = formatTemperature(fahrenheit),
        activeField = TemperatureField.CELSIUS
    )
}

private fun updateFromFahrenheit(
    state: TemperatureState,
    value: String
): TemperatureState {
    val cleanValue = cleanTemperatureInput(value)

    if (cleanValue.isBlank()) {
        return TemperatureState(
            kelvin = "",
            celsius = "",
            fahrenheit = "",
            activeField = TemperatureField.FAHRENHEIT
        )
    }

    val fahrenheit = cleanValue.toDoubleOrNull() ?: return state.copy(
        fahrenheit = cleanValue,
        activeField = TemperatureField.FAHRENHEIT,
        errorMessage =
            "أدخل رقمًا صحيحًا في حقل فهرنهايت."
    )

    val celsius = (fahrenheit - 32.0) * 5.0 / 9.0
    val kelvin = celsius + 273.15

    return TemperatureState(
        kelvin = formatTemperature(kelvin),
        celsius = formatTemperature(celsius),
        fahrenheit = cleanValue,
        activeField = TemperatureField.FAHRENHEIT
    )
}

private fun swapTemperatureUnits(
    state: TemperatureState
): TemperatureState {
    return when (state.activeField) {
        TemperatureField.KELVIN -> {
            state.copy(
                activeField = TemperatureField.CELSIUS
            )
        }

        TemperatureField.CELSIUS -> {
            state.copy(
                activeField = TemperatureField.FAHRENHEIT
            )
        }

        TemperatureField.FAHRENHEIT -> {
            state.copy(
                activeField = TemperatureField.KELVIN
            )
        }

        null -> {
            state.copy(
                activeField = TemperatureField.CELSIUS
            )
        }
    }
}

private fun cleanTemperatureInput(
    value: String
): String {
    return value
        .replace(",", ".")
        .filter { character ->
            character.isDigit() ||
                    character == '.' ||
                    character == '-'
        }
        .take(12)
}

private fun formatTemperature(
    value: Double
): String {
    val rounded = (value * 100.0).roundToInt() / 100.0

    return if (
        rounded == rounded.toLong().toDouble()
    ) {
        rounded.toLong().toString()
    } else {
        rounded.toString()
    }
}