package com.subybara.app.feature.subscriptions

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Neutral950
import com.subybara.app.core.designsystem.theme.Primary300
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Secondary200
import com.subybara.app.core.designsystem.theme.Secondary300
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.parseDate
import com.subybara.app.core.model.todayInMillis
import java.util.Locale

private val BillingCycles = listOf("Weekly", "Monthly", "Quarterly", "Yearly")

private val PaymentMethods = listOf(
    "GCash",
    "Maya",
    "Credit Card",
    "Debit Card",
    "PayPal",
    "Bank Transfer"
)

// Allow up to 7 digits and 2 decimal places.
private val AmountInput = Regex("""\d{0,7}(\.\d{0,2})?""")

// Stores form values to check for unsaved changes.
private data class FormValues(
    val name: String,
    val category: SubscriptionCategory?,
    val amount: String,
    val billingCycle: String,
    val billingDate: String,
    val paymentMethod: String?,
    val notes: String,
    val logoUri: String?
) {
    companion object {
        fun from(subscription: Subscription?) = FormValues(
            name = subscription?.name.orEmpty(),
            category = subscription?.category,
            amount = subscription
                ?.let { String.format(Locale.US, "%.2f", it.amount) }
                .orEmpty(),
            billingCycle = subscription?.billingCycle ?: "Monthly",
            billingDate = subscription?.renewalDate ?: formatDate(todayInMillis()),
            paymentMethod = subscription?.paymentMethod?.ifBlank { null },
            notes = subscription?.notes.orEmpty(),
            logoUri = subscription?.logoUri
        )
    }
}

/**
 * Form shared by the Add Subscription and Edit Subscription screens.
 *
 * Asks for confirmation before discarding unsaved changes.
 *
 * @param title Title shown in the top bar.
 * @param headerImage Capybara picture shown next to the logo tile.
 * @param initialSubscription Subscription to edit, or null to add a new one.
 * @param onCloseClick Called when the user leaves the form without saving.
 * @param onSaveClick Called with the new or updated subscription when the
 * form is valid and the user taps Save.
 * @param modifier Modifier applied to the screen.
 */
@Composable
internal fun SubscriptionForm(
    title: String,
    @DrawableRes headerImage: Int,
    initialSubscription: Subscription?,
    onCloseClick: () -> Unit,
    onSaveClick: (Subscription) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialValues = remember(initialSubscription) {
        FormValues.from(initialSubscription)
    }

    var name by rememberSaveable { mutableStateOf(initialValues.name) }
    var category by rememberSaveable { mutableStateOf(initialValues.category) }
    var amountText by rememberSaveable { mutableStateOf(initialValues.amount) }
    var billingCycle by rememberSaveable { mutableStateOf(initialValues.billingCycle) }
    var billingDate by rememberSaveable { mutableStateOf(initialValues.billingDate) }
    var paymentMethod by rememberSaveable { mutableStateOf(initialValues.paymentMethod) }
    var notes by rememberSaveable { mutableStateOf(initialValues.notes) }
    var logoUri by rememberSaveable { mutableStateOf(initialValues.logoUri) }

    // Use the placeholder color until a date is selected.
    var billingDatePicked by rememberSaveable { mutableStateOf(initialSubscription != null) }

    var showErrors by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val selectedCategory = category
    val selectedPaymentMethod = paymentMethod
    val amount = amountText.toDoubleOrNull()?.takeIf { it > 0.0 }

    val isDirty = FormValues(
        name = name,
        category = category,
        amount = amountText,
        billingCycle = billingCycle,
        billingDate = billingDate,
        paymentMethod = paymentMethod,
        notes = notes,
        logoUri = logoUri
    ) != initialValues

    val focusManager = LocalFocusManager.current

    val pickLogo = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            logoUri = uri.toString()
        }
    }

    val closeForm: () -> Unit = {
        if (isDirty) {
            showDiscardDialog = true
        } else {
            onCloseClick()
        }
    }

    BackHandler(enabled = isDirty) {
        showDiscardDialog = true
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            FormTopBar(
                title = title,
                onCloseClick = closeForm
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                FormHeader(
                    headerImage = headerImage,
                    name = name,
                    logoUri = logoUri,
                    onLogoClick = {
                        pickLogo.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Column(verticalArrangement = Arrangement.spacedBy(17.dp)) {
                    FormFieldCard(
                        label = "Subscription Name",
                        error = "Enter a subscription name"
                            .takeIf { showErrors && name.isBlank() }
                    ) {
                        FormTextField(
                            label = "Subscription Name",
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Ex. Netflix",
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    DropdownFieldCard(
                        label = "Category",
                        selected = selectedCategory,
                        placeholder = "Select Category",
                        options = SubscriptionCategory.entries,
                        optionLabel = { it.label },
                        onSelect = { category = it },
                        error = "Select a category"
                            .takeIf { showErrors && selectedCategory == null }
                    )

                    FormFieldCard(
                        label = "Billing Amount",
                        error = "Enter an amount greater than 0"
                            .takeIf { showErrors && amount == null }
                    ) {
                        FormTextField(
                            label = "Billing Amount",
                            value = amountText,
                            onValueChange = { input ->
                                if (AmountInput.matches(input)) {
                                    amountText = input
                                }
                            },
                            placeholder = "0.00",
                            prefix = "₱",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    BillingCycleCard(
                        selectedCycle = billingCycle,
                        onCycleSelect = { billingCycle = it }
                    )

                    FormFieldCard(
                        label = "Next Billing Date",
                        onClick = { showDatePicker = true },
                        trailing = {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar_edit),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    ) {
                        FieldValueText(
                            text = billingDate,
                            isPlaceholder = !billingDatePicked
                        )
                    }

                    DropdownFieldCard(
                        label = "Payment Method",
                        selected = selectedPaymentMethod,
                        placeholder = "Select Payment Method",
                        options = PaymentMethods,
                        optionLabel = { it },
                        onSelect = { paymentMethod = it },
                        error = "Select a payment method"
                            .takeIf { showErrors && selectedPaymentMethod == null }
                    )

                    FormFieldCard(label = "Notes (Optional)") {
                        FormTextField(
                            label = "Notes",
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = "Add a note",
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                        )
                    }

                    SubybaraPrimaryButton(
                        text = "Save Subscription",
                        onClick = {
                            focusManager.clearFocus()
                            showErrors = true

                            if (
                                name.isNotBlank() &&
                                selectedCategory != null &&
                                amount != null &&
                                selectedPaymentMethod != null
                            ) {
                                onSaveClick(
                                    Subscription(
                                        id = initialSubscription?.id ?: 0,
                                        name = name.trim(),
                                        amount = amount,
                                        billingCycle = billingCycle,
                                        renewalDate = billingDate,
                                        category = selectedCategory,
                                        status = initialSubscription?.status
                                            ?: SubscriptionStatus.ACTIVE,
                                        paymentMethod = selectedPaymentMethod,
                                        startDate = initialSubscription?.startDate
                                            ?: formatDate(todayInMillis()),
                                        notes = notes.trim(),
                                        logoUri = logoUri
                                    )
                                )
                            }
                        },
                        modifier = Modifier.heightIn(min = 48.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        BillingDatePickerDialog(
            selectedDate = billingDate,
            onDateSelect = { selectedDate ->
                billingDate = selectedDate
                billingDatePicked = true
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showDiscardDialog) {
        DiscardChangesDialog(
            onDiscardClick = {
                showDiscardDialog = false
                onCloseClick()
            },
            onKeepEditingClick = { showDiscardDialog = false }
        )
    }
}

@Composable
private fun FormTopBar(
    title: String,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 12.dp, end = 10.dp)
    ) {
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
                tint = Color.Unspecified
            )
        }

        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
            color = Neutral800
        )
    }
}

@Composable
private fun FormHeader(
    @DrawableRes headerImage: Int,
    name: String,
    logoUri: String?,
    onLogoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoTile(
            name = name,
            logoUri = logoUri,
            onClick = onLogoClick
        )

        Spacer(modifier = Modifier.width(28.dp))

        Image(
            painter = painterResource(headerImage),
            contentDescription = null,
            modifier = Modifier.height(108.dp)
        )
    }
}

@Composable
private fun LogoTile(
    name: String,
    logoUri: String?,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val hasLogo = logoUri != null || logoFor(name) != null

    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 70.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 0.dp,
                    spread = 0.dp,
                    color = Primary300,
                    offset = DpOffset(0.dp, 1.dp)
                )
            )
            .background(MaterialTheme.colorScheme.background, shape)
            .border(1.dp, CardBorder, shape)
            .clip(shape)
            .clickable(
                onClickLabel = "Choose logo",
                role = Role.Button,
                onClick = onClick
            )
    ) {
        if (hasLogo) {
            SubscriptionLogo(
                name = name,
                modifier = Modifier.align(Alignment.Center),
                logoUri = logoUri,
                size = 68.dp,
                cornerRadius = 8.dp
            )
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Add Logo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    ),
                    color = Neutral500
                )
            }
        }
    }
}

/**
 * Shared form field card.
 *
 * @param error Validation message shown below the field, or null if valid.
 * @param onClick Opens a picker when the card is tapped.
 * @param trailing Optional icon at the end of the card.
 */
@Composable
private fun FormFieldCard(
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
                    color = SubscriptionBrown
                )

                Spacer(modifier = Modifier.height(labelSpacing))

                content()

                if (error != null) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(top = 4.dp),
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
private fun FieldValueText(
    text: String,
    isPlaceholder: Boolean
) {
    Text(
        text = text,
        style = fieldValueStyle(),
        color = if (isPlaceholder) Neutral500 else Neutral950,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun FormTextField(
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
private fun <T : Any> DropdownFieldCard(
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
private fun BillingCycleCard(
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
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillingDatePickerDialog(
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
