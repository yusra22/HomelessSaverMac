package com.uyr.yusara.homelesssavermac.ImageSliderTest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uyr.yusara.homelesssavermac.MapTest.UserInformation;
import com.uyr.yusara.homelesssavermac.R;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

public class imageslidertest extends AppCompatActivity {

    FlipperLayout flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageslidertest);
        flipper = (FlipperLayout) findViewById(R.id.flipper);
        setLayout();
    }

    private void setLayout()
    {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Location").child("Location");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

               String  img1 = dataSnapshot.child("latitude").getValue().toString();
               String  img2 = dataSnapshot.child("longitude").getValue().toString();
               String  img3 = dataSnapshot.child("name").getValue().toString();

                Toast.makeText(imageslidertest.this, img3, Toast.LENGTH_SHORT).show();

                String url [] = new String[] {

                        img1,
                        img2
                        /*"https:firebasestorage.googleapis.com/v0/b/homelesssavermac.appspot.com/o/Homeless%20Reported%20Images%2F135909-March-201923%3A10%3A.jpg?alt=media&token=7c0b9380-72b9-45a8-840e-1291083c310a",
                        "https://firebasestorage.googleapis.com/v0/b/homelesssavermac.appspot.com/o/Homeless%20Reported%20Images%2F141209-March-201923%3A10%3A.jpg?alt=media&token=9a707271-be20-487e-bbb3-4116c70f969e"*/
                };

                for(int i =0; i<2; i++)
                {
                    FlipperView view = new FlipperView(getBaseContext());
                    view.setImageUrl(url[i])
                            .setDescription("Image "+(i+1));
                    flipper.addFlipperView(view);
                    view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                        @Override
                        public void onFlipperClick(FlipperView flipperView) {

                            Toast.makeText(imageslidertest.this,"Active" +(flipper.getCurrentPagePosition()+1),Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
