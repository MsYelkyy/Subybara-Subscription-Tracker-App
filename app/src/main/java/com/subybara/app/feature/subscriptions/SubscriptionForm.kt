package com.subybara.app.feature.subscriptions

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.todayInMillis
import java.util.Locale

internal val BillingCycles = listOf("Weekly", "Monthly", "Quarterly", "Yearly")

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
    val context = LocalContext.current

    val pickLogo = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            keepLogoAccess(context, uri)
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
