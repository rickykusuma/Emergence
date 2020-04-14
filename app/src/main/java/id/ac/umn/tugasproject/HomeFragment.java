package id.ac.umn.tugasproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_inflater = inflater.inflate(R.layout.fragment_home, container, false);
        CircleMenu circleMenu = (CircleMenu)home_inflater.findViewById(R.id.menu);

        final String[] menus = {
          "Pemadam Kebakaran",
          "Rumsah Sakit",
          "Kantor Polisi"
        };

        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.drawable.ic_add,R.drawable.ic_clear)
                .addSubMenu(Color.parseColor("#00CD00"),R.drawable.ic_home_black_24dp)
                .addSubMenu(Color.parseColor("#00CD00"),R.drawable.ic_home_black_24dp)
                .addSubMenu(Color.parseColor("#00CD00"),R.drawable.ic_home_black_24dp)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        Toast.makeText(getActivity(),"YOU CLICKED " + menus[i], Toast.LENGTH_SHORT).show();
                    }
                });


        return home_inflater;
    }
}
