package untad.aldochristopherleo.emergence;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView desc, email, lokasi, telp;
    Location loc;

    ItemClickListener icl;

    public void setIcl(ItemClickListener icl) {
        this.icl = icl;
    }

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        desc = itemView.findViewById(R.id.txtDescr);
        email = itemView.findViewById(R.id.txtEmail);
        lokasi = itemView.findViewById(R.id.txtLokasi);
        telp = itemView.findViewById(R.id.txtTelp);

    }


    @Override
    public void onClick(View v) {
        icl.onClick(v, getAdapterPosition());
    }
}
