package com.example.realmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.realmapp.models.City;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class AddEditCityActivity extends AppCompatActivity {

    private int cityId;
    private boolean isCreation;
    private City city;
    private Realm realm;
    private EditText editTextCityName;
    private EditText editTextCityDescription;
    private EditText editTextCityLink;
    private ImageView cityImage;
    private Button btnPreview;
    private FloatingActionButton fab;
    private RatingBar ratingBarCity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_city);

        context = getApplicationContext();
        realm = Realm.getDefaultInstance();
        bindUIReferences();
        // Comprueba la acción para editar o para crear ciudad
        if (getIntent().getExtras() != null) {
            cityId = getIntent().getExtras().getInt("id");
            isCreation = false;
        } else {
            isCreation = true;
        }

        setActivityTitle();

        if (!isCreation) {
            city = getCityById(cityId);
            bindDataToFields();
        }

        fab.setOnClickListener(view -> addEditNewCity());

        btnPreview.setOnClickListener(view -> {
            String link = editTextCityLink.getText().toString();
            if (link.length() > 0)
                loadImageLinkForPreview(editTextCityLink.getText().toString());
        });
    }

    private void bindUIReferences() {
        editTextCityName = findViewById(R.id.editTextCityName);
        editTextCityDescription = findViewById(R.id.editTextCityDescription);
        editTextCityLink = findViewById(R.id.editTextCityImage);
        cityImage = findViewById(R.id.imageViewPreview);
        btnPreview = findViewById(R.id.buttonPreview);
        fab = findViewById(R.id.FABEditCity);
        ratingBarCity = findViewById(R.id.ratingBarCity);
    }

    private void setActivityTitle() {
        String title = "Editar Ciudad";
        if (isCreation) title = "Crear Ciudad Nueva";
        setTitle(title);
    }

    private City getCityById(int cityId) {
        return realm.where(City.class).equalTo("id", cityId).findFirst();
    }

    private void bindDataToFields() {
        editTextCityName.setText(city.getName());
        editTextCityDescription.setText(city.getDescription());
        editTextCityLink.setText(city.getImage());
        loadImageLinkForPreview(city.getImage());
        ratingBarCity.setRating(city.getStars());
    }

    private void loadImageLinkForPreview(String link) {
        //Picasso.get().load(link).fit().into(cityImage);
        //Picasso.get().load(link).into(cityImage);
        Glide.with(context)
                .load(link)
                .centerCrop()
                .into(cityImage);
    }

    private boolean isValidDataForNewCity() {
        if (editTextCityName.getText().toString().length() > 0 &&
                editTextCityDescription.getText().toString().length() > 0 &&
                editTextCityLink.getText().toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AddEditCityActivity.this, CityActivity.class);
        startActivity(intent);
    }

    private void addEditNewCity() {
        if (isValidDataForNewCity()) {
            String name = editTextCityName.getText().toString();
            String description = editTextCityDescription.getText().toString();
            String link = editTextCityLink.getText().toString();
            float stars = ratingBarCity.getRating();

            City city = new City(name, description, link, stars);
            // En caso de que sea una edición en vez de creación pasamos el ID
            if (!isCreation) city.setId(cityId);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(city);
            realm.commitTransaction();
            goToMainActivity();
        } else {
            Toast.makeText(this, "Por favor llene todos los campos y/o revise que no haya errores.", Toast.LENGTH_SHORT).show();
        }
    }

}