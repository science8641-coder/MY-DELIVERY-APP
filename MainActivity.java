package com.hassan.mydelivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    EditText customer, item, itemPrice, deliveryFee, extraFee, upiId;
    RadioGroup paymentGroup;
    TextView totalView, historyView;
    SharedPreferences prefs;
    JSONArray bills = new JSONArray();

    int green = 0xFF168653;
    int ink = 0xFF17211B;
    int muted = 0xFF65736A;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("my_delivery", MODE_PRIVATE);
        try { bills = new JSONArray(prefs.getString("bills", "[]")); } catch (Exception ignored) {}
        buildUi();
        updateTotal();
    }

    TextView text(String s, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(ink);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, 8, 0, 8);
        return t;
    }

    EditText field(String hint, String value, int type) {
        EditText e = new EditText(this);
        e.setSingleLine(true); e.setHint(hint); e.setText(value);
        e.setInputType(type);
        e.setTextSize(16);
        e.setPadding(18, 12, 18, 12);
        e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFB7C7BC));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 10;
        e.setLayoutParams(p);
        return e;
    }

    Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setAllCaps(false);
        b.setTextSize(15);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
        b.setTextColor(0xFFFFFFFF);
        return b;
    }

    void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 18, 22, 28);
        root.setBackgroundColor(0xFFF6F9F6);
        scroll.addView(root);

        TextView title = text("My Delivery", 28, true);
        title.setTextColor(green);
        root.addView(title);
        root.addView(text("Personal bill maker • Add your own delivery charge", 14, false));
        root.addView(text("CREATE A BILL", 13, true));

        customer = field("Customer name (e.g. Didi)", "",  android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        item = field("What did they ask you to bring?", "", android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        itemPrice = field("Item price (₹)", "", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        deliveryFee = field("Your delivery charge (₹)", "20", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        extraFee = field("Extra expenses (optional ₹)", "0", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        upiId = field("Your UPI ID (optional, for payment request)", "", android.text.InputType.TYPE_CLASS_TEXT);

        root.addView(customer); root.addView(item); root.addView(itemPrice);
        root.addView(deliveryFee); root.addView(extraFee); root.addView(upiId);

        root.addView(text("PAYMENT METHOD", 13, true));
        paymentGroup = new RadioGroup(this);
        paymentGroup.setOrientation(RadioGroup.VERTICAL);
        RadioButton upi = new RadioButton(this); upi.setText("UPI / Online via UPI app"); upi.setId(View.generateViewId());
        RadioButton cash = new RadioButton(this); cash.setText("Cash"); cash.setId(View.generateViewId());
        paymentGroup.addView(upi); paymentGroup.addView(cash); upi.setChecked(true);
        root.addView(paymentGroup);

        totalView = text("Total: ₹0.00", 24, true);
        totalView.setTextColor(green);
        root.addView(totalView);

        Button calculate = button("Update Total");
        root.addView(calculate);
        calculate.setOnClickListener(v -> updateTotal());

        Button share = button("Save Bill & Share");
        root.addView(share);
        share.setOnClickListener(v -> saveAndShare());

        Button pay = button("Open UPI Payment App");
        root.addView(pay);
        pay.setOnClickListener(v -> openUpi());

        root.addView(text("BILL HISTORY", 18, true));
        historyView = text("", 14, false);
        root.addView(historyView);
        Button refresh = button("Refresh History");
        root.addView(refresh);
        refresh.setOnClickListener(v -> showHistory());
        Button clear = button("Clear All History");
        root.addView(clear);
        clear.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Clear history?")
            .setMessage("This deletes saved bills from this phone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear", (d,w) -> { bills = new JSONArray(); prefs.edit().putString("bills","[]").apply(); showHistory(); })
            .show());

        setContentView(scroll);
        showHistory();
    }

    double number(EditText e) {
        try { return Math.max(0, Double.parseDouble(e.getText().toString().trim())); }
        catch (Exception ex) { return 0; }
    }

    double total() { return number(itemPrice) + number(deliveryFee) + number(extraFee); }

    String money(double n) { return String.format(Locale.US, "%.2f", n); }

    void updateTotal() {
        if (totalView != null) totalView.setText("Total: ₹" + money(total()));
    }

    String paymentMethod() {
        RadioButton selected = findViewById(paymentGroup.getCheckedRadioButtonId());
        return selected == null ? "UPI" : (selected.getText().toString().startsWith("Cash") ? "Cash" : "UPI");
    }

    String billText(String date) {
        String who = customer.getText().toString().trim();
        String what = item.getText().toString().trim();
        if (who.isEmpty()) who = "Customer";
        if (what.isEmpty()) what = "Samaan";
        return "MY DELIVERY BILL\n" +
            "Date: " + date + "\nCustomer: " + who + "\nItem: " + what +
            "\nItem price: ₹" + money(number(itemPrice)) +
            "\nDelivery charge: ₹" + money(number(deliveryFee)) +
            "\nExtra expenses: ₹" + money(number(extraFee)) +
            "\nTOTAL: ₹" + money(total()) +
            "\nPayment method: " + paymentMethod() +
            (upiId.getText().toString().trim().isEmpty() ? "" : "\nUPI ID: " + upiId.getText().toString().trim());
    }

    void saveAndShare() {
        if (item.getText().toString().trim().isEmpty()) {
            item.setError("Enter the item name"); return;
        }
        if (itemPrice.getText().toString().trim().isEmpty()) {
            itemPrice.setError("Enter the item price"); return;
        }
        String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        String bill = billText(date);
        try {
            JSONObject obj = new JSONObject();
            obj.put("date", date); obj.put("customer", customer.getText().toString());
            obj.put("item", item.getText().toString()); obj.put("total", total());
            obj.put("bill", bill); obj.put("status", "Unpaid");
            bills.put(obj);
            prefs.edit().putString("bills", bills.toString()).apply();
        } catch (Exception ignored) {}
        showHistory();
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, bill);
        startActivity(Intent.createChooser(send, "Share delivery bill"));
    }

    void openUpi() {
        String id = upiId.getText().toString().trim();
        if (id.isEmpty() || !id.contains("@")) {
            upiId.setError("Enter your UPI ID first"); return;
        }
        String who = customer.getText().toString().trim();
        Uri uri = new Uri.Builder().scheme("upi").authority("pay")
            .appendQueryParameter("pa", id)
            .appendQueryParameter("pn", who.isEmpty() ? "My Delivery" : who)
            .appendQueryParameter("am", money(total()))
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", "Delivery bill")
            .build();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, "Pay using UPI"));
        } catch (Exception e) {
            new AlertDialog.Builder(this).setMessage("No UPI app found on this phone. Share the bill and ask the customer to pay using their UPI app.").setPositiveButton("OK", null).show();
        }
    }

    void showHistory() {
        if (historyView == null) return;
        StringBuilder s = new StringBuilder();
        for (int i = bills.length()-1; i >= 0; i--) {
            JSONObject b = bills.optJSONObject(i);
            if (b != null) s.append("• ").append(b.optString("date")).append("\n  ")
                .append(b.optString("customer")).append(" • ").append(b.optString("item"))
                .append("\n  ₹").append(money(b.optDouble("total"))).append(" • ").append(b.optString("status")).append("\n\n");
        }
        historyView.setText(s.length() == 0 ? "No bills saved yet." : s.toString());
    }
}
