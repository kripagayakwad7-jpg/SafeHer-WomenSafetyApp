package com.womensafety.app.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.womensafety.app.R;
import com.womensafety.app.adapters.ContactAdapter;
import com.womensafety.app.api.RetrofitClient;
import com.womensafety.app.models.ApiResponse;
import com.womensafety.app.models.Contact;
import com.womensafety.app.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrustedContactsActivity extends AppCompatActivity {

    private RecyclerView rvContacts;
    private TextView tvEmpty;
    private ContactAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvContacts = findViewById(R.id.rvContacts);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        adapter = new ContactAdapter();
        adapter.setOnContactActionListener(this::deleteContact);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddContactDialog());

        loadContacts();
    }

    private void loadContacts() {
        RetrofitClient.getInstance().getApiService()
                .getContacts(sessionManager.getUserId())
                .enqueue(new Callback<ApiResponse<List<Contact>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Contact>>> call,
                                           Response<ApiResponse<List<Contact>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<Contact> contacts = response.body().getData();
                            adapter.setContacts(contacts);
                            tvEmpty.setVisibility(contacts == null || contacts.isEmpty()
                                    ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Contact>>> call, Throwable t) {
                        Toast.makeText(TrustedContactsActivity.this, "Failed to load contacts",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddContactDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etContactName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etContactPhone);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    if (!name.isEmpty() && !phone.isEmpty()) {
                        addContact(name, phone);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addContact(String name, String phone) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", sessionManager.getUserId());
        body.put("contactName", name);
        body.put("phoneNumber", phone);

        RetrofitClient.getInstance().getApiService().addContact(body)
                .enqueue(new Callback<ApiResponse<Contact>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Contact>> call,
                                           Response<ApiResponse<Contact>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(TrustedContactsActivity.this, "Contact added",
                                    Toast.LENGTH_SHORT).show();
                            loadContacts();
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : "Failed";
                            Toast.makeText(TrustedContactsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                        Toast.makeText(TrustedContactsActivity.this, "Network error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteContact(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Remove " + contact.getContactName() + "?")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    RetrofitClient.getInstance().getApiService()
                            .deleteContact(contact.getContactId())
                            .enqueue(new Callback<ApiResponse<Object>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Object>> call,
                                                       Response<ApiResponse<Object>> response) {
                                    loadContacts();
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                                    Toast.makeText(TrustedContactsActivity.this, "Delete failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
