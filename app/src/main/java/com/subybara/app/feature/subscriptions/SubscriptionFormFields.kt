package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Neutral950
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Secondary200
import com.subybara.app.core.designsystem.theme.Secondary300
import com.subybara.app.core.designsystem.theme.WarmBrown
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.parseDate

/**
 * Shared form field card.
 *
 * @param error Validation message shown below the field, or null if valid.
 * @param onClick Opens a picker when the card is tapped.
 * @param trailing Optional icon at the end of the card.
 */
@Composable
internal fun FormFieldCard(
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    onClick: (() -> Unit)? = null,
    labelSpacing: Dp = 8.dp,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    SubybaraCard(modifier = modifier.fillMaxWidth().semantics {
        if (error != null) error(error)
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = WarmBrown
                )

                Spacer(modifier = Modifier.height(labelSpacing))

                content()

                if (error != null) {
                    Text(
                        text = error,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (trailing != null) {
                trailing()
            }
        }
    }
}

@Composable
private fun fieldValueStyle(): TextStyle =
    MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp)

@Composable
internal fun FieldValueText(
    text: String,
    isPlaceholder: Boolean
) {
    Text(
        text = text,
        style = fieldValueStyle(),
        color = if (isPlaceholder) Neutral500 else Neutral950,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val valueStyle = fieldValueStyle()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().semantics { contentDescription = label },
        singleLine = true,
        textStyle = valueStyle.copy(color = Neutral950),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        style = valueStyle,
                        color = if (value.isEmpty()) Neutral500 else Neutral950
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = valueStyle,
                            color = Neutral500
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T : Any> DropdownFieldCard(
    label: String,
    selected: T?,
    placeholder: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        FormFieldCard(
            label = label,
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            error = error,
            trailing = {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        ) {
            FieldValueText(
                text = selected?.let { optionLabel(it) } ?: placeholder,
                isPlaceholder = selected == null
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = White
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Give each billing cycle equal width to keep the options on one row.
@Composable
internal fun BillingCycleCard(
    selectedCycle: String,
    onCycleSelect: (String) -> Unit
) {
    FormFieldCard(
        label = "Billing Cycle",
        labelSpacing = 15.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BillingCycles.forEach { cycle ->
                BillingCycleChip(
                    text = cycle,
                    selected = cycle == selectedCycle,
                    onClick = { onCycleSelect(cycle) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun BillingCycleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clip(shape)
            .background(if (selected) Primary600 else Secondary200)
            .border(BorderStroke(0.3.dp, Secondary300), shape)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) White else Neutral800,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 6.sp,
                maxFontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BillingDatePickerDialog(
    selectedDate: String,
    onDateSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = parseDate(selectedDate)
    )
    val pickerColors = DatePickerDefaults.colors(containerColor = White)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val pickedDate = pickerState.selectedDateMillis

                    if (pickedDate != null) {
                        onDateSelect(formatDate(pickedDate))
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        colors = pickerColors
    ) {
        DatePicker(
            state = pickerState,
            colors = pickerColors,
            showModeToggle = false
        )
    }
}
